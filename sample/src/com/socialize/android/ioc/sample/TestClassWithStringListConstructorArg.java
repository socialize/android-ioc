package com.socialize.android.ioc.sample;

import java.util.List;

public class TestClassWithStringListConstructorArg {
	
	private List<String> stringList = null;

	public TestClassWithStringListConstructorArg(List<String> list) {
		super();
		this.stringList = list;
	}
	
	public List<String> getStringList() {
		return stringList;
	}

	public void setStringList(List<String> stringList) {
		this.stringList = stringList;
	}
}