package com.socialize.android.ioc.sample;

public class SubClassOfTestClassWithInitMethod extends TestClassWithInitMethod {
	String param;
	
	public SubClassOfTestClassWithInitMethod(String param) {
		super();
		this.param = param;
	}
	public SubClassOfTestClassWithInitMethod() {
		super();
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
}