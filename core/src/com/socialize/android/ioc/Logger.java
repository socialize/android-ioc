package com.socialize.android.ioc;

import android.util.Log;

public class Logger {

	public static final String LOG_KEY = "AndroidIOC";
	
	public static final void d(String tag, String msg) {
		Log.d(LOG_KEY, tag + ": " + msg);
	}

	public static final void i(String tag, String msg) {
		Log.i(LOG_KEY, tag + ": " + msg);
	}
	
	public static final void w(String tag, String msg) {
		Log.w(LOG_KEY, tag + ": " + msg);
	}
	
	public static final void w(String tag, String msg, Throwable e) {
		Log.w(LOG_KEY, tag + ": " + msg, e);
	}

	public static final void e(String tag, String msg) {
		Log.e(LOG_KEY, tag + ": " + msg);
	}
	
	public static final void e(String tag, String msg, Throwable e) {
		Log.e(LOG_KEY, tag + ": " + msg, e);
	}
}
