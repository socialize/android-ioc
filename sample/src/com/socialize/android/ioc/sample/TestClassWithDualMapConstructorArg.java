package com.socialize.android.ioc.sample;

import java.util.HashMap;
import java.util.Map;

public class TestClassWithDualMapConstructorArg {
	
	private Map<String, TestClassWithInitMethod> beanMap = null;
	private Map<String, Integer> stringMap = null;
	
	public TestClassWithDualMapConstructorArg(Map<String, TestClassWithInitMethod> list) {
		super();
		this.beanMap = list;
	}
	
	public TestClassWithDualMapConstructorArg(HashMap<String, Integer> list) {
		super();
		this.stringMap = list;
	}
	
	public TestClassWithDualMapConstructorArg(Map<String, TestClassWithInitMethod> listA, Map<String, Integer> listB) {
		super();
		this.beanMap = listA;
		this.stringMap = listB;
	}

	public Map<String, TestClassWithInitMethod> getBeanMap() {
		return beanMap;
	}

	public Map<String, Integer> getStringMap() {
		return stringMap;
	}
	
}