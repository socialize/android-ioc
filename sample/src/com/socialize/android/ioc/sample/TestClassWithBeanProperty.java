package com.socialize.android.ioc.sample;

public class TestClassWithBeanProperty {

	private Object bean;

	@SuppressWarnings("unchecked")
	public <T extends Object> T getBean() {
		return (T) bean;
	}

	public void setBean(Object bean) {
		this.bean = bean;
	}
	
}
