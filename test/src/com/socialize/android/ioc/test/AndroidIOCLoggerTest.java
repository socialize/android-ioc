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
