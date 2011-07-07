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
package com.socialize.android.ioc.test;

import com.socialize.android.ioc.Logger;

import android.test.AndroidTestCase;

public class AndroidIOCLoggerTest extends AndroidTestCase {

	public void testLogger() {
		
		final String tag = "TestLog";
		final String msg = "TestMsg";
		final Throwable t = new Exception("TEST ERROR - Ignore Me");
		
		// We cant actaully "test" that logs are written, but we're just doing this
		// to make sure no errors are produced.
		
		Logger.d(tag, msg);
		Logger.i(tag, msg);
		Logger.w(tag, msg);
		Logger.e(tag, msg);
		Logger.w(tag, msg,t);
		Logger.e(tag, msg,t);
	}
	
}
