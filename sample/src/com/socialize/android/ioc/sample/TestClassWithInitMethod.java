package com.socialize.android.ioc.sample;

public class TestClassWithInitMethod {
	
	private boolean initialized = false;
	
	public TestClassWithInitMethod() {
		super();
	}
	
	public void doInit() {
		initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}
}