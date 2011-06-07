package com.socialize.android.ioc.test;

import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.AndroidIOC;
import com.socialize.android.ioc.Container;
import com.socialize.android.ioc.ContainerBuilder;

public class AndroidIOCTest extends AndroidTestCase {

	public void testAndroidIOCInit() throws Exception {
        AndroidIOC ioc = new AndroidIOC();
		ioc.init(getContext());
	}
	
	public void testAndroidIOCInitWithName() throws Exception {
        AndroidIOC ioc = new AndroidIOC();
		ioc.init(getContext(), "android-beans.xml");
	}
	
	@UsesMocks ({ContainerBuilder.class, Container.class})
	public void testAndroidIOCSize() throws Exception {
		ContainerBuilder builder = AndroidMock.createMock(ContainerBuilder.class, getContext());
		Container container = AndroidMock.createMock(Container.class);
		final String filename = "foobar";
		
		MockContext mockContext = new MockContext();
		
		AndroidIOC ioc = new AndroidIOC();
		
		final int returned = 10;
		
		AndroidMock.expect(builder.build(AndroidMock.eq(mockContext), AndroidMock.eq(filename))).andReturn(container);
		AndroidMock.expect(container.size()).andReturn(returned);
		
		AndroidMock.replay(builder);
		AndroidMock.replay(container);
		
		ioc.init(mockContext, filename, builder);
		
		assertEquals(10, ioc.size());
		
		AndroidMock.verify(builder);
		AndroidMock.verify(container);
	}
	
	@UsesMocks ({ContainerBuilder.class, Container.class})
	public void testDestroy() throws Exception {
		ContainerBuilder builder = AndroidMock.createMock(ContainerBuilder.class, getContext());
		Container container = AndroidMock.createMock(Container.class);
		final String filename = "foobar";
		
		MockContext mockContext = new MockContext();
		
		AndroidIOC ioc = new AndroidIOC();
		
		AndroidMock.expect(builder.build(AndroidMock.eq(mockContext), AndroidMock.eq(filename))).andReturn(container);
		container.destroy();
		
		AndroidMock.replay(builder);
		AndroidMock.replay(container);
		
		ioc.init(mockContext, filename, builder);
		ioc.destroy();
		
		AndroidMock.verify(builder);
		AndroidMock.verify(container);
	}
	
}
