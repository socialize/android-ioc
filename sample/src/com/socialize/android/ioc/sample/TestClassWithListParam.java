package com.socialize.android.ioc.sample;

import java.util.List;


public class TestClassWithListParam {
	
	private List<TestClassWithInitMethod> list = null;
	
	public TestClassWithListParam() {
		super();
	}

	public List<TestClassWithInitMethod> getList() {
		return list;
	}

	public void setList(List<TestClassWithInitMethod> list) {
		this.list = list;
	}
}