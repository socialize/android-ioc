package com.socialize.android.ioc.sample;

import java.util.LinkedList;
import java.util.List;

public class TestClassWithDualListConstructorArg {
	
	private List<TestClassWithInitMethod> beanList = null;
	private List<String> stringList = null;
	
	public TestClassWithDualListConstructorArg(List<TestClassWithInitMethod> list) {
		super();
		this.beanList = list;
	}
	
	public TestClassWithDualListConstructorArg(LinkedList<String> list) {
		super();
		this.stringList = list;
	}
	
	public TestClassWithDualListConstructorArg(List<TestClassWithInitMethod> listA, LinkedList<String> listB) {
		super();
		this.beanList = listA;
		this.stringList = listB;
	}


	public List<TestClassWithInitMethod> getBeanList() {
		return beanList;
	}

	public List<String> getStringList() {
		return stringList;
	}

	public void setStringList(List<String> stringList) {
		this.stringList = stringList;
	}
}