package com.socialize.android.ioc;

import java.util.LinkedList;
import java.util.List;

public class BeanRef {

	private String name;
	private String className;

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
	
}
