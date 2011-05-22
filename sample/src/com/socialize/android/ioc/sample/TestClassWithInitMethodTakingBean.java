package com.socialize.android.ioc.sample;

import android.content.Context;

public class TestClassWithInitMethodTakingBean {

	private TestClassWithInitMethod object;
	private Context context;

	public TestClassWithInitMethodTakingBean() {
		super();
	}
	
	public void init(Context context, TestClassWithInitMethod object) {
		this.context = context;
		this.object = object;
	}

	public TestClassWithInitMethod getObject() {
		return object;
	}

	public Context getContext() {
		return context;
	}
}
