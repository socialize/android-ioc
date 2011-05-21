package com.socialize.android.ioc.sample;

public class SubClass extends TestClass {
	String param;
	
	public SubClass(String param) {
		super();
		this.param = param;
	}
	public SubClass() {
		super();
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
}