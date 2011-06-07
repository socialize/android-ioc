package com.socialize.android.ioc.sample;

import com.socialize.android.ioc.Container;
import com.socialize.android.ioc.ContainerAware;

public class TestClassContainerAware implements ContainerAware {

	private Container container;
	
	@Override
	public void setContainer(Container container) {
		this.container = container;
	}

	public Container getContainer() {
		return container;
	}
}
