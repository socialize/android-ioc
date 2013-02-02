package com.socialize.android.ioc.sample;

public class TestClassWithBeanProperty {

	public int PROPERTY_SET_CALLS = 0;
	
	private Object bean;

	@SuppressWarnings("unchecked")
	public <T extends Object> T getBean() {
		return (T) bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
		PROPERTY_SET_CALLS++;
	}
}
