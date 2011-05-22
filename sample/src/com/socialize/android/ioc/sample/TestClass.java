package com.socialize.android.ioc.sample;

public class TestClass {
	
	private boolean initialized = false;
	
	public TestClass() {
		super();
	}
	
	public void doInit() {
		initialized = true;
	}

	public boolean isInitialized() {
		return initialized;
	}
}