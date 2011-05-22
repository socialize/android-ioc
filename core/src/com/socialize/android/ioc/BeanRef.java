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
	
	private List<KeyValuePair> properties;
	private List<KeyValuePair> constructorArgs;
	
	private boolean singleton = true;
	
	public synchronized void addConstructorArgument(KeyValuePair arg) {
		if(constructorArgs == null) constructorArgs = new LinkedList<KeyValuePair>();
		constructorArgs.add(arg);
	}
	
	public synchronized void addProperty(KeyValuePair arg) {
		if(properties == null) properties = new LinkedList<KeyValuePair>();
		properties.add(arg);
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

	public List<KeyValuePair> getProperties() {
		return properties;
	}

	public void setProperties(List<KeyValuePair> properties) {
		this.properties = properties;
	}

	public List<KeyValuePair> getConstructorArgs() {
		return constructorArgs;
	}

	public void setConstructorArgs(List<KeyValuePair> constructorArgs) {
		this.constructorArgs = constructorArgs;
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
	
	
}
