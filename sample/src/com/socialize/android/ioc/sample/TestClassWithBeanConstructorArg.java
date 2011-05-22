package com.socialize.android.ioc.sample;

public class TestClassWithBeanConstructorArg {
	SubClassOfTestClassWithInitMethod object;
	
	
	public TestClassWithBeanConstructorArg() {
		super();
	}
	
	public TestClassWithBeanConstructorArg(SubClassOfTestClassWithInitMethod object) {
		super();
		this.object = object;
	}

	public SubClassOfTestClassWithInitMethod getObject() {
		return object;
	}

	public void setObject(SubClassOfTestClassWithInitMethod object) {
		this.object = object;
	}
}