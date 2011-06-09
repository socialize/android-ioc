package com.socialize.android.ioc.sample;

import java.util.Map;

public class TestClassWithMapProperty {
	
	private Map<String, TestClassWithInitMethod> beanMap = null;

	public Map<String, TestClassWithInitMethod> getBeanMap() {
		return beanMap;
	}

	public void setBeanMap(Map<String, TestClassWithInitMethod> beanMap) {
		this.beanMap = beanMap;
	}
}