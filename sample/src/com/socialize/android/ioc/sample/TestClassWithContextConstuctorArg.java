package com.socialize.android.ioc.sample;

import android.content.Context;

public class TestClassWithContextConstuctorArg {
	Context context;
	
	public TestClassWithContextConstuctorArg() {
		super();
	}
	public TestClassWithContextConstuctorArg(Context context) {
		super();
		this.context = context;
	}
	public Context getContext() {
		return context;
	}
	public void setContext(Context context) {
		this.context = context;
	}
	
}