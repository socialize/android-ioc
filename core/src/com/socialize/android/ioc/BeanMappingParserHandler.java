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
	
	public static final String ARG = "arg";
	public static final String PROPERTY = "property";
	
	private BeanMapping beanMapping;
	
	private BeanRef currentRef = null;

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		if(localName.equals(BEANS)) {
			beanMapping = new BeanMapping();
		}
		else if(localName.equals(BEAN)) {
			currentRef = new BeanRef();
			currentRef.setClassName(attributes.getValue("class"));
			currentRef.setName(attributes.getValue("name"));
			
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
			KeyValuePair arg = new KeyValuePair();
			arg.setValue(attributes.getValue("value"));
			
			String type = attributes.getValue("type").trim().toUpperCase();
			
			arg.setType(RefType.valueOf(type));
			
			currentRef.addConstructorArgument(arg);
		}
		else if(localName.equals(PROPERTY)) {
			KeyValuePair prop = new KeyValuePair();
			prop.setKey(attributes.getValue("name"));
			prop.setValue(attributes.getValue("value"));
			String type = attributes.getValue("type").trim().toUpperCase();
			prop.setType(RefType.valueOf(type));
			currentRef.addProperty(prop);
		}
	}


	public BeanMapping getBeanMapping() {
		return beanMapping;
	}
}
