package com.socialize.android.ioc.sample;

import java.util.List;


public class TestClassWithListInit {
	
	private List<TestClassWithInitMethod> list = null;
	
	public TestClassWithListInit() {
		super();
	}

	public List<TestClassWithInitMethod> getList() {
		return list;
	}

	public void init(List<TestClassWithInitMethod> list) {
		this.list = list;
	}
}