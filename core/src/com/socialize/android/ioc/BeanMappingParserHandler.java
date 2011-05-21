package com.socialize.android.ioc;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.socialize.android.ioc.KeyValuePair.RefType;

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
