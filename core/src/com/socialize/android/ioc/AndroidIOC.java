/*
 * Copyright (c) 2011 Socialize Inc.
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
	
	public final void init(Context context, String filename) throws IOException {
		ContainerBuilder builder = new ContainerBuilder(context);
		container = builder.build(context, filename);
	}
	
	@SuppressWarnings("unchecked")
	public final <T extends Object> T getBean(String name) {
		return (T) container.getBean(name);
	}
}
