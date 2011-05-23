package com.socialize.android.ioc.sample;

import java.util.List;


public class TestClassWithListConstructorArg {
	
	private List<TestClassWithInitMethod> list = null;
	
	public TestClassWithListConstructorArg(List<TestClassWithInitMethod> list) {
		super();
		this.list = list;
	}

	public List<TestClassWithInitMethod> getList() {
		return list;
	}
	
}