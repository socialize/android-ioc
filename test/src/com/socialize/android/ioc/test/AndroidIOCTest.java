/*
 * Copyright (c) 2012 Socialize Inc. 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy 
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.android.ioc.test;

import java.io.InputStream;

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
        InputStream in = null;
        try {
        	in = getContext().getAssets().open("android-beans.xml");
        	ioc.init(getContext(), in);
		}
		finally {
			if(in != null) {
				in.close();
			}
		}
	}
	
	@UsesMocks ({ContainerBuilder.class, Container.class})
	public void testAndroidIOCInitWithMultipleConfig() throws Exception {
		
		ContainerBuilder builder = AndroidMock.createMock(ContainerBuilder.class);
		Container container = AndroidMock.createMock(Container.class);
		InputStream[] in = new InputStream[2];
		
		AndroidMock.expect(builder.build(getContext(), in)).andReturn(container);
		
		AndroidMock.replay(builder);
		
		AndroidIOC ioc = new AndroidIOC();
		ioc.init(getContext(), builder, in);
		
		AndroidMock.verify(builder);
	}
	
	@UsesMocks ({ContainerBuilder.class, Container.class, InputStream.class})
	public void testAndroidIOCSize() throws Exception {
		ContainerBuilder builder = AndroidMock.createMock(ContainerBuilder.class);
		Container container = AndroidMock.createMock(Container.class);
		InputStream in = AndroidMock.createMock(InputStream.class);
		
		InputStream[] inArray = {in};
		
		MockContext mockContext = new MockContext();
		
		AndroidIOC ioc = new AndroidIOC();
		
		final int returned = 10;
		
		AndroidMock.expect(builder.build(AndroidMock.eq(mockContext), AndroidMock.eq(inArray))).andReturn(container);
		AndroidMock.expect(container.size()).andReturn(returned);
		
		AndroidMock.replay(builder);
		AndroidMock.replay(container);
		
		ioc.init(mockContext, builder, inArray);
		
		assertEquals(10, ioc.size());
		
		AndroidMock.verify(builder);
		AndroidMock.verify(container);
	}
	
	@UsesMocks ({ContainerBuilder.class, Container.class, InputStream.class})
	public void testDestroy() throws Exception {
		ContainerBuilder builder = AndroidMock.createMock(ContainerBuilder.class);
		Container container = AndroidMock.createMock(Container.class);
		InputStream in = AndroidMock.createMock(InputStream.class);
		
		InputStream[] inArray = {in};
		
		MockContext mockContext = new MockContext();
		
		AndroidIOC ioc = new AndroidIOC();
		
		AndroidMock.expect(builder.build(AndroidMock.eq(mockContext), AndroidMock.eq(inArray))).andReturn(container);
		container.destroy();
		
		AndroidMock.replay(builder);
		AndroidMock.replay(container);
		
		ioc.init(mockContext, builder, inArray);
		ioc.destroy();
		
		AndroidMock.verify(builder);
		AndroidMock.verify(container);
	}
	
}
