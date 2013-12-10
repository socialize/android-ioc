/*
 * Copyright (c) 2012 Socialize Inc. 
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

import java.io.IOException;
import java.io.InputStream;
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
import android.app.Activity;
import android.content.Context;
import com.socialize.android.ioc.Argument.CollectionType;
import com.socialize.android.ioc.Argument.RefType;

/**
 * @author Jason Polites
 */
public class ContainerBuilder {

	static final int MAX_ITERATIONS = 10;
	
	private BeanBuilder builder = null;
	private BeanMappingParser parser = null;
	private ResourceLocator resourceLocator;
	
	private Map<String, BeanMapping> imports;
	
	public ContainerBuilder() {
		this(new BeanMappingParser());
	}
	
	public ContainerBuilder(BeanMappingParser parser) {
		super();
		builder = new BeanBuilder();
		resourceLocator = new ResourceLocator();
		imports = new HashMap<String, BeanMapping>();
		this.parser = parser;
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T buildBean(Container container, BeanRef beanRef)  {
		return (T) buildBean(container, beanRef, (Object[]) null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T buildBean(Container container, BeanRef beanRef, Object...args)  {
		Object bean = null;
		
		try {
			if(Logger.isDebugEnabled())
				Logger.d(getClass().getSimpleName(), "Creating bean " + beanRef.getName());
			
			List<Argument> constructorArgs = beanRef.getConstructorArgs();
			if(constructorArgs != null && constructorArgs.size() > 0) {
				Object[] cargs = getArguments(container, constructorArgs, false);
				if(cargs != null && cargs.length > 0) {
					if(args != null && args.length > 0) {
						Object[] concat = new Object[cargs.length + args.length];
						System.arraycopy(cargs, 0, concat, 0, cargs.length);
						System.arraycopy(args, 0, concat, cargs.length, args.length);
						cargs = concat;
					}
				}
				else {
					cargs = args;
				}
				
				if(cargs != null && cargs.length > 0) {
					
					if(containsContext(cargs)) {
						beanRef.setContextSensitiveConstructor(true);
					}
					
					bean = builder.construct(beanRef.getClassName(), cargs);
				}
			}
			else if(args != null && args.length > 0) {
				
				if(containsContext(args)) {
					beanRef.setContextSensitiveConstructor(true);
				}
				
				bean = builder.construct(beanRef.getClassName(), args);
			}
			else {
				bean = builder.construct(beanRef.getClassName());
			}
			
			if(bean != null) {
				if(Logger.isDebugEnabled())
					Logger.d(getClass().getSimpleName(), "Bean " + beanRef.getName() + " created");
			}
		}
		catch (Exception e) {
			Logger.e(getClass().getSimpleName(), "Failed to create bean [" +
					beanRef.getName() +
					"]", e);
		}
		
		if(bean != null && bean instanceof ContainerAware) {
			ContainerAware aware = (ContainerAware) bean;
			aware.onCreate(container);
		}

		return (T) bean;
	}
	
	private boolean containsContext(Object[] args) {
		if(args != null) {
			for (Object obj : args) {
				if(obj instanceof Context) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public void setBeanProperties(Container container, BeanRef ref, Object bean) {
		setBeanProperties(container, ref, bean, null);
	}
	
	public void setBeanProperties(Container container, BeanRef ref, Object bean, List<SetPropertyLater> setLater)  {
		
		if(!ref.isSingleton() || !ref.isPropertiesSet()) {
			try {
				if(Logger.isDebugEnabled())
					Logger.d(getClass().getSimpleName(), "Setting properties on bean " + ref.getName());
				
				List<Argument> properties = ref.getProperties();
				
				if(properties != null && properties.size() > 0) {
					for (Argument property : properties) {
						if(property.getKey() != null) {
							setBeanProperty(container, ref, bean, property, setLater);
						}
						else {
							Logger.e(getClass().getSimpleName(), "Cannot set property on bean [" +
									ref.getName() +
									"] with null name");
						}
					}
				}

				if(Logger.isDebugEnabled())
					Logger.d(getClass().getSimpleName(), "Properties set on bean " + ref.getName());
				
				if(ref.isSingleton()) {
					ref.setPropertiesSet(true);
				}
			}
			catch (Exception e) {
				Logger.e(getClass().getSimpleName(), "Failed to set properties on bean [" +
						ref.getName() +
						"]", e);
			}
		}
	}
	
	private void setBeanProperty(Container container, BeanRef ref, Object bean, Argument property, List<SetPropertyLater> setLater) {
		Object value = getArgumentValue(container, property, false);
		if(value == null) {
			
			// Try to set later
			if(setLater != null) {
				SetPropertyLater later = new SetPropertyLater();
				later.ref = ref;
				later.bean = bean;
				later.arg = property;
				setLater.add(later);
			}
			else {
				Logger.w(getClass().getSimpleName(), "Failed to locate value for property [" +
						property.getKey() +
						"] of bean [" +
						ref.getName() +
						"].  The bean may be incomplete as a result!");
			}
		}
		else {
			try {
				builder.setProperty(ref, bean, property.getKey(), value);
			}
			catch (Exception e) {
				Logger.e(getClass().getSimpleName(), "Failed to set property [" +
						property.getKey() +
						"] on bean [" +
						ref.getName() +
						"]", e);
			}
		}
	}
	
	public Container build(Context context, String filename) throws IOException {
		BeanMapping mapping = this.parser.parse(context, filename);
		return build(context, mapping);
	}
	
	public Container build(Context context, InputStream...streams) throws IOException {
		BeanMapping primary = this.parser.parse(context, streams);
		return build(context, primary);
	}
	
	public Container build(Context context, BeanMappingSource source) throws IOException {
		
		BeanMapping beanMapping = BeanMappingCache.get(source.getName());
		
		if(beanMapping == null) {
			InputStream[] sources = source.getSources();
			try {
				beanMapping = this.parser.parse(context, sources);
				BeanMappingCache.put(source.getName(), beanMapping);
			}
			finally {
				IOException ex = null;
				for (InputStream in : sources) {
					try {
						in.close();
					}
					catch (IOException e) {
						ex = e;
					}
				}
				if(ex != null) {
					throw ex;
				}
			}
		}
		else {
			// Reset any property set variables in bean refs for this mapping
			beanMapping.reset();
		}
		
		return build(context, beanMapping);
	}
	
	protected void resolveImports(Context context, BeanMapping original) throws IOException {
		BeanMapping imported = new BeanMapping();
		resolveImports(context, original, imported, 0);
		imports.clear();
		int count = 0;
		if(!imported.isEmpty()) {
			while(resolveDependencies(original, imported, count) > 0) {
				count++;
			}
		}
	}
	
	protected int resolveDependencies(BeanMapping original, BeanMapping imported, int recursionCount) {
		
		if(recursionCount > MAX_ITERATIONS) {
			throw new RuntimeException("Too many iterations during import resolution.  Possible circular reference in bean mapping, or bean init failed.  Check the logs.");
		}
		
		int unresolved = 0;

		if(Logger.isDebugEnabled())
			Logger.d(getClass().getSimpleName(), "Resolving import dependencies in loop " + recursionCount);
		
		Collection<BeanRef> beanRefs = original.getBeanRefs();
		Collection<FactoryRef> factoryRefs = original.getFactoryRefs();
		Set<String> proxyRefs = original.getProxyRefs();
		
		BeanMapping needed = new BeanMapping();
		
		for (BeanRef beanRef : beanRefs) {
			
			if(!beanRef.isResolved()) {
				
				beanRef.setResolved(true);
				
				Set<Argument> allArgs = beanRef.getAllArguments();
				
				for (Argument argument : allArgs) {
					
					if(argument.getType().equals(RefType.BEAN)) {
						
						String beanName = argument.getValue();
						
						if(!original.containsBean(beanName)) {
							BeanRef argRef = imported.getBeanRef(beanName);
							
							if(argRef != null) {
								needed.addBeanRef(argRef);
								unresolved++;
							}
							else {
								FactoryRef fRef = imported.getFactoryRef(beanName);
								
								if(fRef != null) {
									needed.addFactoryRef(fRef);
									unresolved++;
								}
								else {
									
									if(imported.hasProxy(beanName)) {
										needed.addProxyRef(beanName);
										unresolved++;
									}
									else {
										Logger.w(getClass().getSimpleName(), "Could not locate bean [" +
												beanName +
												"] found as an argument to bean [" +
												beanRef.getName() +
												"] during import resolution");
									}
								}
							}
						}
					}
				}	
			}
		}
		
		for (FactoryRef factoryRef : factoryRefs) {
			String beanName = factoryRef.getMakes();
			if(!original.containsBean(beanName)) {
				BeanRef argRef = imported.getBeanRef(beanName);
				if(argRef != null) {
					needed.addBeanRef(argRef);
					unresolved++;
				}
				else {
					Logger.w(getClass().getSimpleName(), "Could not locate bean [" +
							beanName +
							"] found as an argument to factory [" +
							factoryRef.getName() +
							"] during import resolution");
				}
			}
		}
		
		for (String beanName : proxyRefs) {
			if(!original.containsBean(beanName)) {
				BeanRef argRef = imported.getBeanRef(beanName);
				
				if(argRef != null) {
					needed.addBeanRef(argRef);
					unresolved++;
				}
				else {
					Logger.w(getClass().getSimpleName(), "Could not locate bean [" +
							beanName +
							"] found as proxy during import resolution");
				}				
			}
		}
		
		if(!needed.isEmpty()) {
			original.merge(needed);
		}
		
		Logger.i(getClass().getSimpleName(), "Resolving import complete.  There are " + unresolved + " unresolved dependencies");
		
		return unresolved;
	}

 	
	protected void resolveImports(Context context, BeanMapping original, BeanMapping merged, int recursionCount) throws IOException {
		
		if(recursionCount > MAX_ITERATIONS) {
			throw new RuntimeException("Too many iterations during import resolution.  Possible circular reference in bean mapping, or bean init failed.  Check the logs.");
		}
		
		Collection<ImportRef> importRefs = original.getImportRefs();
		
		if(importRefs != null && !importRefs.isEmpty()) {
			Set<String> invalidSources = new HashSet<String>();
			
			for (ImportRef ref : importRefs) {

				BeanMapping imported = null;

				if(!imports.containsKey(ref.getSource())) {

					if(!invalidSources.contains(ref.getSource())) {

						if(Logger.isDebugEnabled()) {
							Logger.d(getClass().getSimpleName(), "Resolving imports for [" +
									ref.getSource() +
									"]");
						}

						// Try to locate the source
						InputStream in = resourceLocator.locate(context, ref.getSource());

						if(in != null) {
							imported = this.parser.parse(context, in);

							// Resolve imports for this file
							resolveImports(context, imported, merged, ++recursionCount);

							imports.put(ref.getSource(), imported);
						}
						else {

							Logger.e(getClass().getSimpleName(), "Cannot resolve import of bean [" +
									ref.getName() +
									"] because the corresponding source [" +
									ref.getSource() +
									"] could not be located.");

							invalidSources.add(ref.getSource());	
						}
					}
					else {
						Logger.e(getClass().getSimpleName(), "Cannot resolve import of bean [" +
								ref.getName() +
								"] because the corresponding source [" +
								ref.getSource() +
								"] could not be located.");
					}

					if(imported != null) {
						// Single bean
						if(ref.getName() == null || ref.getName().trim().length() == 0) {
							if(ref.isDependentOnly()) {
								merged.merge(imported);
							}
							else {
								original.merge(imported);
							}
						}
					}					 
				}
				else {
					imported = imports.get(ref.getSource());
				}

				// Single bean
				if(imported != null) {
					if(ref.getName() != null && ref.getName().trim().length() > 0) {
						BeanRef beanRef = imported.getBeanRef(ref.getName());
						if(beanRef != null) {
							merged.addBeanRef(beanRef);
						}
						else {
							Logger.w(getClass().getSimpleName(), "Bean [" +
									ref.getName() +
									"] does not exist in source [" +
									ref.getSource());	
						}
					}
				}
			}
		}
	}
	
	public Container build(Context context, BeanMapping mapping) throws IOException {
		
		resolveImports(context, mapping);
		
		Container container = new Container(context, mapping, this);

		// Setup static proxies
		if(Container.staticProxies.size() > 0) {
			Set<Entry<String, Object>> entries = Container.staticProxies.entrySet();
			for (Entry<String, Object> entry : entries) {
				container.setRuntimeProxyInternal(entry.getKey(), entry.getValue(), true);
			}
		}

		// Build factories
		buildFactories(container, mapping.getFactoryRefs());
		
		// Build beans
		buildBeans(container, builder, mapping, mapping.getBeanRefs(), 0);
		
		// Set properties
		Map<String, Object> beans = container.getBeans();
		
		// We may add to the set, so create a copy to avoid modification errors
		Set<Entry<String, Object>> entrySet = new HashSet<Map.Entry<String,Object>>(beans.entrySet());
		
		List<SetPropertyLater> setLaterProperties = new LinkedList<SetPropertyLater>();
		
		for (Entry<String, Object> entry : entrySet) {
			BeanRef ref = mapping.getBeanRef(entry.getKey());
			
			if(ref != null) { // Might be a factory
				Object bean = entry.getValue();
				setBeanProperties(container, ref, bean, setLaterProperties);
			}
		}
		
		if(!setLaterProperties.isEmpty()) {
			for (SetPropertyLater later : setLaterProperties) {
				setBeanProperty(container, later.ref, later.bean, later.arg, null);
			}
			setLaterProperties.clear();
		}
		
		initBeans(container, mapping, beans, 0);

		return container;
	}
	
	private void initBeans(Container container, BeanMapping mapping, Map<String, Object> beans, int iteration) {
		if(iteration > MAX_ITERATIONS) {
			throw new RuntimeException("Too many iterations during init.  Possible circular reference in bean mapping, or bean init failed.  Check the logs.");
		}
		
		Map<String, Object> doLaterBeans = new HashMap<String, Object>();
		
		Set<Entry<String, Object>> entrySet = beans.entrySet();
		
		for (Entry<String, Object> entry : entrySet) {
			BeanRef ref = mapping.getBeanRef(entry.getKey());
			
			if(ref != null) {
				Object bean = entry.getValue();
				if(!initBean(container, ref, bean)) {

					if(Logger.isDebugEnabled())
						Logger.d(getClass().getSimpleName(), "Cannot init bean [" +
								ref.getName() +
								"] now.  Marking for later init...");

					doLaterBeans.put(entry.getKey(), entry.getValue());
				}
				else {
					ref.setInitCalled(true);
					if(Logger.isDebugEnabled())
						Logger.d(getClass().getSimpleName(), "Bean [" +
								ref.getName() +
								"] initialized.");
				}
			}
			else {
				// May be factory
			}
		}
		
		if(!doLaterBeans.isEmpty()) {
			initBeans(container, mapping, doLaterBeans, ++iteration);
		}
	}
	
	public void destroyBean(Container container, BeanRef beanRef, Object bean) {
		if(bean != null && beanRef.getDestroyMethod() != null) {

			if(Logger.isDebugEnabled())
				Logger.d(getClass().getSimpleName(), "Destroying bean " + beanRef.getName());
			
			Object[] args = getArguments(container, beanRef.getDestroyMethod().getArguments(), false);
			
			Method method = builder.getMethodFor(bean.getClass(), beanRef.getDestroyMethod().getName(), args);
			
			if(method != null) {
				try {
					method.invoke(bean, args);
				}
				catch (Exception e) {
					Logger.e(getClass().getSimpleName(), "Failed to invoke destroy method [" +
							beanRef.getDestroyMethod().getName() +
							"] on bean [" +
							beanRef.getName() +
							": " +
							beanRef.getClassName() +
							"]", e);
				}
			}
			else {
				Logger.e(getClass().getSimpleName(), "Could not find method matching [" +
						beanRef.getDestroyMethod().getName() +
						"] in bean [" +
						beanRef.getName() +
						": " +
						beanRef.getClassName() +
						"]");
			}
		}
		
		if(bean instanceof ContainerAware) {
			ContainerAware aware = (ContainerAware) bean;
			aware.onDestroy(container);
		}
	}
	
	public boolean initBean(Container container, BeanRef beanRef, Object bean) {
		
		if(bean != null) {
			MethodRef initMethod = beanRef.getInitMethod();
			if(initMethod != null) {
				Object[] args = null;
				List<Argument> arguments = initMethod.getArguments();
				if(arguments != null && arguments.size() > 0) {
					args = getArguments(container, arguments, true);
					if(args == null) {
						return false;
					}
				}
				
				if(beanRef.isContextSensitive() && !beanRef.isContextSensitiveConstructor()) {
					beanRef.setContextSensitiveInitMethod(containsContext(args));
				}
				
				Method method = builder.getMethodFor(bean.getClass(), initMethod.getName(), args);
				
				if(method != null) {
					try {
						method.invoke(bean, args);
						
						return true;
					}
					catch (Exception e) {
						Logger.e(getClass().getSimpleName(), "Failed to invoke init method [" +
								initMethod.getName() +
								"] on bean [" +
								beanRef.getName() +
								": " +
								beanRef.getClassName() +
								"]", e);
					}
				}
				else {
					Logger.e(getClass().getSimpleName(), "Could not find method matching [" +
							initMethod.getName() +
							"] in bean [" +
							beanRef.getName() +
							": " +
							beanRef.getClassName() +
							"]");
				}
			}
			else {
				// no need to init, ok to go
				return true;
			}
		}
		
		return false;
	}
	
	private void buildFactories(Container container, Collection<FactoryRef> factoryRefs) {
		if(factoryRefs != null) {
			for (FactoryRef factoryRef : factoryRefs) {
				try {
					BeanFactory<?> factory = BeanFactory.class.newInstance();
					factory.setBeanName(factoryRef.getMakes());
					factory.setContainer(container);
					container.putBean(factoryRef.getName(), factory);
				}
				catch (Exception e) {
					Logger.e(getClass().getSimpleName(), "Failed to create bean [" +
							factoryRef.getName() +
							"]", e);
				}
			}
		}
	}
	
	private void buildBeans(Container container, BeanBuilder builder, BeanMapping mapping, Collection<BeanRef> beanRefs, int iteration) {
		if(iteration > MAX_ITERATIONS) {
			throw new RuntimeException("Too many iterations.  Possible circular reference in bean mapping, or bean construction failed.  Check the logs.");
		}
		
		List<BeanRef> doLaterBeans = new LinkedList<BeanRef>();

		for (BeanRef beanRef : beanRefs) {

			if(!beanRef.isAbstractBean() && !beanRef.isLazy() && beanRef.isSingleton() && !container.containsBean(beanRef.getName())) {

				Object bean = null;

				try {
					bean = buildBean(container, beanRef);
					
					if(bean == null) {
						// We can't construct this now, flag for later.
						if(Logger.isDebugEnabled())
							Logger.d(getClass().getSimpleName(), "Cannot create bean [" +
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
			buildBeans(container, builder, mapping, doLaterBeans, ++iteration);
		}
	}
	
	public Object[] getArguments(Container container, List<Argument> list, boolean forInit) {
		if(list != null) {
			Object[] args = new Object[list.size()];
			int argIndex = 0;
			for (Argument arg : list) {
				
				Object argumentValue = getArgumentValue(container, arg, forInit);
				
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
	
	private Object getArgumentValue(Container container, Argument arg, boolean forInit) {
		Object object = null;

		if(arg.getType() != null) {
			
			switch(arg.getType()) {
				case BEAN:
					object = getBeanValue(container, arg.getValue(), forInit);
					break;
					
				case CONTEXT:
					object = container.getContext();
					break;
					
				case ACTIVITY:
					
					if(!(container.getContext() instanceof Activity)) {
						Logger.e(getClass().getSimpleName(),
								"Argument [" +
                                        arg.getKey() +
                                        "] of type activity found but current context is not an Activity.  " +
										"The container MUST be initialized from an Actvity for activity type arguments. Expected context but was [" +
										container.getContext() +
										"]");
					}
					
					object = container.getContext();
					
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
	
	private Object getBeanValue(Container container, String name, boolean forInit) {
		Object object = null;
		
		// Look for the bean
		if(!container.containsBean(name)) {
			BeanRef beanRef = container.getBeanRef(name);
			if(beanRef != null) {
				if(!beanRef.isSingleton() || beanRef.isLazy()) {
					// Not a singleton, create one
					object = container.getBean(name);
				}
				else {
					Logger.w(getClass().getSimpleName(), "No bean found with name [" +
							name +
							"].  May not have been created yet");
				}
			}
			else {
				// We can't construct this now
				Logger.w(getClass().getSimpleName(), "No bean definition found with name [" +
						name +
						"].  May not have been created yet");
			}
		}
		else {
			object = container.getBean(name);
			
			if(forInit && object != null) {
				BeanRef beanRef = container.getBeanRef(name);
				if(beanRef != null && beanRef.getInitMethod() != null) {
					// Make sure this bean has been initialized
					if(!beanRef.isInitCalled()) {
						// We can't init this now
						if(Logger.isDebugEnabled())
							Logger.d(getClass().getSimpleName(), "Bean argument [" +
									beanRef.getName() +
									"] has not been initialized yet so cannot be used as an argument for another init method");

						object = null;
					}
				}
			}
		}
		
		if(object instanceof IBeanMaker) {
			// This is a maker bean, recurse
			BeanRef beanRef = container.getBeanRef(name);
			
			// It's possible this maker has not had it's properties set yet.. so set them
			setBeanProperties(container, beanRef, object);
			
			String beanName = ((IBeanMaker)object).getBeanName(object, container);
			
			if(beanName == null) {
				Logger.w(getClass().getSimpleName(), "Bean maker with name [" +
						name +
						"] returned null for bean name");
				return null;
			}
			else {
				return getBeanValue(container, beanName, forInit);
			}
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
					Object value = getArgumentValue(container, child, false);
					
					if(value == null && !child.getType().equals(RefType.NULL)) {
						// Can't complete so just abort
						if(Logger.isDebugEnabled())
							Logger.d(getClass().getSimpleName(), "Cannot create list for argument [" +
									arg.getKey() +
									"] now due to dependent bean not existing.  Marking for later creation");

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
		
		Object key = getArgumentValue(container, keyArg, false);
		Object value = getArgumentValue(container, valArg, false);
		
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
						MapEntry entry = (MapEntry) getArgumentValue(container, child, false);
						
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
					Object value = getArgumentValue(container, child, false);
					
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

	class SetPropertyLater {
		BeanRef ref;
		Object bean;
		Argument arg;
	}
}
