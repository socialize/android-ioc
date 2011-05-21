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

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import static com.socialize.android.ioc.KeyValuePair.RefType.*;

/**
 * 
 * @author Jason Polites
 *
 */
public class ContainerBuilder {

	static final int MAX_ITERATIONS = 20;
	
	private BeanBuilder builder = null;
	private BeanMappingParser parser = null;
	private Context context;
	
	public ContainerBuilder(Context context) {
		super();
		builder = new BeanBuilder();
		parser = new BeanMappingParser();
		this.context = context;
	}
	
	public ContainerBuilder(Context context, BeanBuilder builder) {
		this(context);
		this.builder = builder;
	}
	
	public ContainerBuilder(Context context, BeanBuilder builder, BeanMappingParser parser) {
		this(context, builder);
		this.parser = parser;
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T buildBean(Container container, BeanRef beanRef)  {
		Object bean = null;
		
		try {
			
			List<KeyValuePair> constructorArgs = beanRef.getConstructorArgs();
			
			if(constructorArgs != null && constructorArgs.size() > 0) {
				Object[] args = new Object[constructorArgs.size()];
				int argIndex = 0;
				boolean okToGo = true;
				for (KeyValuePair arg : constructorArgs) {
					if(arg.getType().equals(BEAN)) {
						// Look for the bean
						if(container.containsBean(arg.getValue())) {
							args[argIndex] = container.getBean(arg.getValue());
						}
						else {
							// We can't construct this now
							okToGo = false;
							break;
						}
					}
					else if(arg.getType().equals(CONTEXT)) {
						args[argIndex] = context;
					}
					else {
						// Coerce the type based on the typed parameter of the constructor
						args[argIndex] = builder.coerce(arg);
					}
					
					argIndex++;
				}
				
				if(okToGo) {
					bean = builder.construct(beanRef.getClassName(), args);
				}
			}
			else {
				bean = builder.construct(beanRef.getClassName());
			}
			
		}
		catch (Exception e) {
			Log.e(getClass().getSimpleName(), "Failed to create bean [" +
					beanRef.getName() +
					"]", e);
		}
		return (T) bean;

	}
	
	public void setBeanProperties(Container container, BeanRef ref, Object bean)  {
		try {
			List<KeyValuePair> properties = ref.getProperties();
			if(properties != null && properties.size() > 0) {
				for (KeyValuePair property : properties) {
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
			Log.e(getClass().getSimpleName(), "Failed to set properties on bean [" +
					ref.getName() +
					"]", e);
		}
	}
	
	public Container build(Context context) throws IOException {
		return buildFromClassPath(context);
	}
	
	public Container buildFromAsset(Context context) throws IOException {
		BeanMapping mapping = this.parser.parseFromAsset(context);
		return build(context, mapping);
	}

	public Container buildFromClassPath(Context context) throws IOException {
		BeanMapping mapping = this.parser.parseFromClassPath(context);
		return build(context, mapping);
	}
	
	public Container buildFromAsset(Context context, String filename) throws IOException {
		BeanMapping mapping = this.parser.parseFromAsset(context, filename);
		return build(context, mapping);
	}

	public Container buildFromClassPath(Context context, String filename) throws IOException {
		BeanMapping mapping = this.parser.parseFromClassPath(context, filename);
		return build(context, mapping);
	}
	
	public Container build(Context context, BeanMapping mapping) {
		Container container = new Container(context, mapping, this);

		// Build beans
		buildBeans(context, container, builder, mapping, mapping.getBeanRefs(), 0);
		
		// Set properties
		Map<String, Object> beans = container.getBeans();
		Set<String> names = beans.keySet();
		
		for (String name : names) {
			BeanRef ref = mapping.getBeanRef(name);
			Object bean = beans.get(name);
			setBeanProperties(container, ref, bean);
		}

		return container;
	}
	
	
	private void buildBeans(Context context, Container container, BeanBuilder builder, BeanMapping mapping, Collection<BeanRef> beanRefs, int iteration) {
		if(iteration > MAX_ITERATIONS) {
			throw new StackOverflowError("Too many iterations.  Possible circular reference in bean mapping");
		}
		
		List<BeanRef> doLaterBeans = new LinkedList<BeanRef>();

		for (BeanRef beanRef : beanRefs) {

			if(beanRef.isSingleton() && !container.containsBean(beanRef.getName())) {

				Object bean = null;

				try {
					bean = buildBean(container, beanRef);
					
					if(bean == null) {
						// We can't construct this now, flag for later.
						doLaterBeans.add(beanRef);
					}
					else {
						container.putBean(beanRef.getName(), bean);
					}
				}
				catch (Exception e) {
					Log.e(getClass().getSimpleName(), "Failed to create bean [" +
							beanRef.getName() +
							"]", e);
				}
			}
		}
		
		if(!doLaterBeans.isEmpty()) {
			buildBeans(context, container, builder, mapping, doLaterBeans, ++iteration);
		}
	}

}
