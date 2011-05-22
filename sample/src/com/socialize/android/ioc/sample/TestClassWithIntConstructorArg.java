package com.socialize.android.ioc.sample;

public class TestClassWithIntConstructorArg {
	int param;
	
	public TestClassWithIntConstructorArg(int param) {
		super();
		this.param = param;
	}

	public int getParam() {
		return param;
	}

	public void setParam(int param) {
		this.param = param;
	}
}