/*
 * Copyright (c) 2011 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.android.ioc;

import static com.socialize.android.ioc.Argument.RefType.BEAN;
import static com.socialize.android.ioc.Argument.RefType.CONTEXT;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import android.content.Context;

import com.socialize.android.ioc.Argument.CollectionType;
import com.socialize.android.ioc.Argument.RefType;

/**
 * @author Jason Polites
 */
public class ContainerBuilder {

	static final int MAX_ITERATIONS = 5;
	
	private BeanBuilder builder = null;
	private BeanMappingParser parser = null;
	private Context context;
	
	public ContainerBuilder(Context context) {
		this(context, new BeanMappingParser());
	}
	
	public ContainerBuilder(Context context, BeanMappingParser parser) {
		super();
		builder = new BeanBuilder();
		this.parser = parser;
		this.context = context;
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T buildBean(Container container, BeanRef beanRef)  {
		Object bean = null;
		
		try {
			List<Argument> constructorArgs = beanRef.getConstructorArgs();
			if(constructorArgs != null && constructorArgs.size() > 0) {
				Object[] args = getArguments(context, container, constructorArgs);
				if(args != null && args.length > 0) {
					bean = builder.construct(beanRef.getClassName(), args);
				}
			}
			else {
				bean = builder.construct(beanRef.getClassName());
			}
			
		}
		catch (Exception e) {
			Logger.e(getClass().getSimpleName(), "Failed to create bean [" +
					beanRef.getName() +
					"]", e);
		}

		return (T) bean;

	}
	
	public void setBeanProperties(Container container, BeanRef ref, Object bean)  {
		try {
			List<Argument> properties = ref.getProperties();
			
			if(properties != null && properties.size() > 0) {
				for (Argument property : properties) {
					if(property.getType().equals(BEAN)) {
						Object refBean = container.getBean(property.getValue());
						builder.setProperty(bean, property.getKey(), refBean);
					}
					else if(property.getType().equals(CONTEXT)) {
						builder.setProperty(bean, property.getKey(), context);
					}
					else {
						Object value = builder.coerce(property);
						builder.setProperty(bean, property.getKey(), value);
					}
				}
			}
		}
		catch (Exception e) {
			Logger.e(getClass().getSimpleName(), "Failed to set properties on bean [" +
					ref.getName() +
					"]", e);
		}
	}
	
	public Container build(Context context) throws IOException {
		BeanMapping mapping = this.parser.parse(context);
		return build(context, mapping);
	}
	
	public Container build(Context context, String filename) throws IOException {
		BeanMapping mapping = this.parser.parse(context, filename);
		return build(context, mapping);
	}

	public Container build(Context context, BeanMapping mapping) {
		Container container = new Container(mapping, this);

		// Build beans
		buildBeans(context, container, builder, mapping, mapping.getBeanRefs(), 0);
		
		// Set properties
		Map<String, Object> beans = container.getBeans();
		
		Set<Entry<String, Object>> entrySet = beans.entrySet();
		
		for (Entry<String, Object> entry : entrySet) {
			BeanRef ref = mapping.getBeanRef(entry.getKey());
			Object bean = entry.getValue();
			setBeanProperties(container, ref, bean);
			initBean(container, ref, bean);
		}

		return container;
	}
	
	public void destroyBean(Container container, BeanRef beanRef, Object bean) {
		if(bean != null && beanRef.getDestroyMethod() != null) {
			Object[] args = getArguments(context, container, beanRef.getDestroyMethod().getArguments());
			
			Method method = builder.getMethodFor(bean.getClass(), beanRef.getDestroyMethod().getName(), args);
			
			if(method != null) {
				try {
					method.invoke(bean, args);
				}
				catch (Exception e) {
					Logger.e(getClass().getSimpleName(), "Failed to invoke destroy method [" +
							beanRef.getDestroyMethod() +
							"] on bean [" +
							beanRef.getName() +
							"]", e);
				}
			}
			else {
				Logger.e(getClass().getSimpleName(), "Could not find method matching [" +
						beanRef.getDestroyMethod().getName() +
						"] in bean [" +
						beanRef.getName()  +
						"]");
			}
		}
	}
	
	public void initBean(Container container, BeanRef beanRef, Object bean) {
		
		if(bean != null) {
			if(beanRef.getInitMethod() != null) {
				
				Object[] args = getArguments(context, container, beanRef.getInitMethod().getArguments());
				
				Method method = builder.getMethodFor(bean.getClass(), beanRef.getInitMethod().getName(), args);
				
				if(method != null) {
					try {
						method.invoke(bean, args);
					}
					catch (Exception e) {
						Logger.e(getClass().getSimpleName(), "Failed to invoke init method [" +
								beanRef.getInitMethod() +
								"] on bean [" +
								beanRef.getName() +
								"]", e);
					}
				}
				else {
					Logger.e(getClass().getSimpleName(), "Could not find method matching [" +
							beanRef.getInitMethod().getName() +
							"] in bean [" +
							beanRef.getName()  +
							"]");
				}
			}
		}
	}
	
	private void buildBeans(Context context, Container container, BeanBuilder builder, BeanMapping mapping, Collection<BeanRef> beanRefs, int iteration) {
		if(iteration > MAX_ITERATIONS) {
			throw new StackOverflowError("Too many iterations.  Possible circular reference in bean mapping, or bean construction failed.  Check the logs.");
		}
		
		List<BeanRef> doLaterBeans = new LinkedList<BeanRef>();

		for (BeanRef beanRef : beanRefs) {

			if(beanRef.isSingleton() && !container.containsBean(beanRef.getName())) {

				Object bean = null;

				try {
					bean = buildBean(container, beanRef);
					
					if(bean == null) {
						// We can't construct this now, flag for later.
						
						Logger.i(getClass().getSimpleName(), "Cannot create bean [" +
								beanRef.getName() +
								"] now due to dependent bean not existing.  Marking for later creation");
						
						doLaterBeans.add(beanRef);
					}
					else {
						container.putBean(beanRef.getName(), bean);
					}
				}
				catch (Exception e) {
					Logger.e(getClass().getSimpleName(), "Failed to create bean [" +
							beanRef.getName() +
							"]", e);
				}
			}
		}
		
		if(!doLaterBeans.isEmpty()) {
			buildBeans(context, container, builder, mapping, doLaterBeans, ++iteration);
		}
	}
	
	public Object[] getArguments(Context context, Container container, List<Argument> list) {
		if(list != null) {
			Object[] args = new Object[list.size()];
			int argIndex = 0;
			for (Argument arg : list) {
				
				Object argumentValue = getArgumentValue(container, arg);
				
				if(argumentValue == null && !arg.getType().equals(RefType.NULL)) {
					// Bail
					return null;
				}
				
				args[argIndex] = argumentValue;
				argIndex++;
			}
			
			return args;
		}
		
		return null;
	}
	
	private Object getArgumentValue(Container container, Argument arg) {
		Object object = null;
		if(arg.getType() != null) {
			
			switch(arg.getType()) {
				case BEAN:
					// Look for the bean
					if(container.containsBean(arg.getValue())) {
						object = container.getBean(arg.getValue());
					}
					else {
						// We can't construct this now
						object = null;
						break;
					}
					break;
					
				case CONTEXT:
					object = context;
					break;
					
				case LIST:
					object = makeList(container, arg);
					break;
					
				case SET:
					object = makeSet(container, arg);
					break;
					
				case MAP:
					object = makeMap(container, arg);
					break;
					
				case MAPENTRY:
					object = makeMapEntry(container, arg);
					break;
					
				default:
					// Coerce the type based on the typed parameter of the constructor
					object = builder.coerce(arg);
					break;
			}
		}
		else {
			Logger.e(getClass().getSimpleName(), "No argument type specified!");
		}
		
		return object;
	}
	
	@SuppressWarnings("unchecked")
	private Object makeList(Container container, Argument arg) {
		List<Object> list = null;
		
		Collection<Argument> children = arg.getChildren();
		CollectionType collectionType = arg.getCollectionType();
		
		if(collectionType == null) {
			collectionType = CollectionType.LINKEDLIST;
		}
		
		try {
			switch (collectionType) {
				case LINKEDLIST:
					list = LinkedList.class.newInstance();
					break;
					
				case ARRAYLIST:
					list = ArrayList.class.newInstance();
					break;
					
				case STACK:
					list = Stack.class.newInstance();
					break;
					
				case VECTOR:
					list = Vector.class.newInstance();
					break;
					
				default:
					throw new IllegalArgumentException("Invalid list type " + collectionType);
			}
			
			if(children != null) {
				for (Argument child : children) {
					Object value = getArgumentValue(container, child);
					
					if(value == null && !child.getType().equals(RefType.NULL)) {
						// Can't complete so just abort
						return null;
					}
					
					if(value != null) {
						list.add(value);
					}
				}
			}
		}
		catch (Exception e) {
			Logger.e(getClass().getSimpleName(), "Failed to create list for argument of type[" +
					arg.getType() +
					"]", e);
		}
		return list;
	}
	
	private Object makeMapEntry(Container container, Argument arg) {
		MapEntry entry = new MapEntry();
		List<Argument> children = arg.getChildren();
		
		Argument keyArg = children.get(0);
		Argument valArg = children.get(1);

		if(valArg.getKey() != null && valArg.getKey().equals("key")) {
			
			Argument tmp = keyArg;
			keyArg = valArg;
			valArg = tmp;
		}
		
		Object key = getArgumentValue(container, keyArg);
		Object value = getArgumentValue(container, valArg);
		
		if(key == null) {
			// No bueno.. return here (must be dependency failure)
			return null;
		}
		
		if(value == null && !valArg.getType().equals(RefType.NULL)) {
			return null;
		}
		
		entry.setKey(key);
		entry.setValue(value);
		
		return entry;
	}
	
	@SuppressWarnings("unchecked")
	private Object makeMap(Container container, Argument arg) {
		Map<Object, Object> list = null;
		
		Collection<Argument> children = arg.getChildren();

		CollectionType collectionType = arg.getCollectionType();
		
		if(collectionType == null) {
			collectionType = CollectionType.HASHMAP;
		}
		
		try {
			switch (collectionType) {
				case HASHMAP:
					list = HashMap.class.newInstance();
					break;
					
				case TREEMAP:
					list = TreeMap.class.newInstance();
					break;
					
					
				default:
					throw new IllegalArgumentException("Invalid map type " + collectionType);
			}
			
			if(children != null) {
				for (Argument child : children) {
					
					if(child.getType().equals(RefType.MAPENTRY)) {
						MapEntry entry = (MapEntry) getArgumentValue(container, child);
						
						if(entry == null) {
							// Can't complete so just abort
							return null;
						}
						
						list.put(entry.getKey(), entry.getValue());
						
					}
					else {
						throw new IllegalArgumentException("Invalid argument type.  Expected " +
								RefType.MAPENTRY.name() +
								" but found " + child.getType());
					}
				}
			}
		}
		catch (Exception e) {
			Logger.e(getClass().getSimpleName(), "Failed to create map for argument of type[" +
					arg.getType() +
					"]", e);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	private Object makeSet(Container container, Argument arg) {
		Set<Object> list = null;
		
		Collection<Argument> children = arg.getChildren();
		CollectionType collectionType = arg.getCollectionType();
		
		if(collectionType == null) {
			collectionType = CollectionType.HASHSET;
		}
		
		try {
			switch (collectionType) {
				case HASHSET:
					list = HashSet.class.newInstance();
					break;
					
				case TREESET:
					list = TreeSet.class.newInstance();
					break;
					
				default:
					throw new IllegalArgumentException("Invalid set type " + collectionType);
			}
			
			if(children != null) {
				for (Argument child : children) {
					Object value = getArgumentValue(container, child);
					
					if(value == null && !child.getType().equals(RefType.NULL)) {
						// Can't complete so just abort
						return null;
					}
					
					if(value != null) {
						list.add(value);
					}
				}
			}
		}
		catch (Exception e) {
			Logger.e(getClass().getSimpleName(), "Failed to create set for argument of type [" +
					arg.getType() +
					"]", e);
		}
		return list;
	}

}
