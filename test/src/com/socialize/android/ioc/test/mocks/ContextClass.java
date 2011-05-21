package com.socialize.android.ioc.test.mocks;

import android.content.Context;

public class ContextClass {
	Context context;
	
	public ContextClass() {
		super();
	}
	public ContextClass(Context context) {
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