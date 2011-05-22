package com.socialize.android.ioc.test;

import android.test.AndroidTestCase;

import com.socialize.android.ioc.AndroidIOC;

public class AndroidIOCTest extends AndroidTestCase {

	public void testAndroidIOCInit() throws Exception {
        AndroidIOC ioc = AndroidIOC.getInstance();
		ioc.init(getContext());
	}
	
	public void testAndroidIOCInitWithName() throws Exception {
        AndroidIOC ioc = AndroidIOC.getInstance();
		ioc.init(getContext(), "android-beans.xml");
	}
	
}
