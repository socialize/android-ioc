package com.socialize.android.ioc.test.mocks;

public class ContainedClass {
	SubClass object;
	
	
	public ContainedClass() {
		super();
	}
	
	public ContainedClass(SubClass object) {
		super();
		this.object = object;
	}

	public SubClass getObject() {
		return object;
	}

	public void setObject(SubClass object) {
		this.object = object;
	}
}