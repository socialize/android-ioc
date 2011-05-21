package com.socialize.android.ioc;

import java.io.IOException;

import android.content.Context;

public final class AndroidIOC {

	private static final AndroidIOC instance = new AndroidIOC();
	
	private Container container;
	
	private AndroidIOC() {
		super();
	}
	
	public static final AndroidIOC getInstance() {
		return instance;
	}
	
	public final void init(Context context) throws IOException {
		ContainerBuilder builder = new ContainerBuilder(context);
		container = builder.build(context);
	}
	
	@SuppressWarnings("unchecked")
	public final <T extends Object> T getBean(String name) {
		return (T) container.getBean(name);
	}
}
