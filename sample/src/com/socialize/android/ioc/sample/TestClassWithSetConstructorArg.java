package com.socialize.android.ioc.sample;

import java.util.Set;


public class TestClassWithSetConstructorArg {
	
	private Set<TestClassWithInitMethod> set = null;
	
	public TestClassWithSetConstructorArg(Set<TestClassWithInitMethod> set) {
		super();
		this.set = set;
	}

	public Set<TestClassWithInitMethod> getSet() {
		return set;
	}
	
}