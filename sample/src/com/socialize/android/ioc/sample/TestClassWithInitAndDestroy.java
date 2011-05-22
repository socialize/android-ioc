package com.socialize.android.ioc.sample;


public class TestClassWithInitAndDestroy {


	private boolean init = false;
	private boolean destroy = false;

	public TestClassWithInitAndDestroy() {
		super();
	}
	
	public void init() {
		init = true;
	}
	
	public void destroy() {
		destroy = true;
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	public boolean isDestroy() {
		return destroy;
	}

	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}
}
