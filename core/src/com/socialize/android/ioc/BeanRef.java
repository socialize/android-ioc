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

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Jason Polites
 *
 */
public class BeanRef {

	private String name;
	private String className;
	
	private MethodRef initMethod;
	private MethodRef destroyMethod;
	
	private List<Argument> properties;
	private List<Argument> constructorArgs;
	
	private String extendsBean = null;
	
	private boolean singleton = true;
	
	public synchronized void addConstructorArgument(Argument arg) {
		if(constructorArgs == null) constructorArgs = new LinkedList<Argument>();
		
		if(!constructorArgs.contains(arg)) {
			constructorArgs.add(arg);
		}
	}
	
	public synchronized void addProperty(Argument arg) {
		if(properties == null) properties = new LinkedList<Argument>();
		
		if(!properties.contains(arg)) {
			properties.add(arg);
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String type) {
		this.className = type;
	}

	public List<Argument> getProperties() {
		return properties;
	}

	public List<Argument> getConstructorArgs() {
		return constructorArgs;
	}

	public boolean isSingleton() {
		return singleton;
	}

	public void setSingleton(boolean singleton) {
		this.singleton = singleton;
	}

	public MethodRef getInitMethod() {
		return initMethod;
	}

	public void setInitMethod(MethodRef initMethod) {
		this.initMethod = initMethod;
	}

	public MethodRef getDestroyMethod() {
		return destroyMethod;
	}

	public void setDestroyMethod(MethodRef destroyMethod) {
		this.destroyMethod = destroyMethod;
	}

	public String getExtendsBean() {
		return extendsBean;
	}

	public void setExtendsBean(String extendsBean) {
		this.extendsBean = extendsBean;
	}
	
	/**
	 * Merges the config of the given bean into this one.
	 * Does NOT overwrite discrete values, but will merge collections
	 * @param ref
	 */
	public void merge(BeanRef ref) {
		List<Argument> props = ref.getProperties();
		
		if(props != null) {
			for (Argument arg : props) {
				addProperty(arg);
			}
		}
		
		List<Argument> args = ref.getConstructorArgs();
		
		if(props != null) {
			for (Argument arg : args) {
				addConstructorArgument(arg);
			}
		}
		
		if(this.className == null) {
			this.className = ref.getClassName();
		}
		
		if(this.initMethod == null) {
			this.initMethod = ref.getInitMethod();
		}
		
		if(this.destroyMethod == null) {
			this.destroyMethod = ref.getDestroyMethod();
		}
	
	}
}
