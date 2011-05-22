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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.socialize.android.ioc.KeyValuePair.RefType;

/**
 * 
 * @author Jason Polites
 *
 */
public class BeanMappingParserHandler extends DefaultHandler {
	
	public static final String BEANS = "beans";
	public static final String BEAN = "bean";
	
	public static final String PROPERTY = "property";
	public static final String INIT_METHOD = "init";
	public static final String DESTROY_METHOD = "destroy";
	public static final String ARG = "arg";
	
	private BeanMapping beanMapping;
	
	private BeanRef currentRef = null;
	
	private boolean inInitMethod = false;
	private boolean inDestroyMethod = false;
	

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(localName.equals(BEANS)) {
			beanMapping = new BeanMapping();
		}
		else if(localName.equals(BEAN)) {
			currentRef = new BeanRef();
			currentRef.setClassName(attributes.getValue("class"));
			currentRef.setName(attributes.getValue("name"));
			
			String initMethod = attributes.getValue("initMethod");
			if(initMethod != null && initMethod.trim().length() > 0) {
				MethodRef initM = new MethodRef();
				initM.setName(initMethod);
				currentRef.setInitMethod(initM);
			}
			
			String singleton = attributes.getValue("singleton");
			
			if(singleton != null && singleton.trim().length() > 0) {
				currentRef.setSingleton(Boolean.parseBoolean(singleton));
			}
			else {
				currentRef.setSingleton(true);
			}
			
			beanMapping.addBeanRef(currentRef);
		}
		else if(localName.equals(ARG)) {
			
			if(inInitMethod) {
				if(currentRef.getInitMethod() != null) {
					currentRef.getInitMethod().addArgument(getProperty(attributes));
				}
			}
			else if(inDestroyMethod) {
				if(currentRef.getDestroyMethod() != null) {
					currentRef.getDestroyMethod().addArgument(getProperty(attributes));
				}
			}
			else {
				currentRef.addConstructorArgument(getProperty(attributes));
			}
		}
		else if(localName.equals(PROPERTY)) {
			currentRef.addProperty(getProperty(attributes));
		}
		
		else if(localName.equals(INIT_METHOD)) {
			inInitMethod = true;
			currentRef.setInitMethod(createMethod(attributes));
		}
		else if(localName.equals(DESTROY_METHOD)) {
			inDestroyMethod = true;
			currentRef.setDestroyMethod(createMethod(attributes));
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if(localName.equals(INIT_METHOD)) {
			inInitMethod = false;
		}
		else if(localName.equals(DESTROY_METHOD)) {
			inDestroyMethod = false;
		}
	}

	private MethodRef createMethod(Attributes attributes) {
		String methodName = attributes.getValue("name");
		MethodRef method = null;
		if(methodName != null && methodName.trim().length() > 0) {
			method = new MethodRef();
			method.setName(methodName);
			currentRef.setDestroyMethod(method);
		}
		return method;
	}
	
	private KeyValuePair getProperty(Attributes attributes) {
		KeyValuePair prop = new KeyValuePair();
		prop.setKey(attributes.getValue("name"));
		
		String value = attributes.getValue("value");
		String ref = attributes.getValue("ref");
		
		if(ref != null && ref.trim().length() > 0) {
			prop.setValue(ref);
			prop.setType(RefType.BEAN);
		}
		else {
			
			prop.setValue(value);
			
			String type = attributes.getValue("type");
			
			if(type != null && type.trim().length() > 0) {
				type = type.trim().toUpperCase();
				prop.setType(RefType.valueOf(type));
			}
		}


		return prop;
	}


	public BeanMapping getBeanMapping() {
		return beanMapping;
	}
}
