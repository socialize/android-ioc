package com.socialize.android.ioc.test;

import android.test.AndroidTestCase;

import com.socialize.android.ioc.AndroidIOC;

public class AndroidIOCTest extends AndroidTestCase {

	public void testAndroidIOCInitFromAsset() throws Exception {
        AndroidIOC ioc = AndroidIOC.getInstance();
		ioc.initFromAsset(getContext());
	}
	
	public void testAndroidIOCInitFromAssetWithName() throws Exception {
        AndroidIOC ioc = AndroidIOC.getInstance();
		ioc.initFromAsset(getContext(), "android-beans-asset.xml");
	}
	
	public void testAndroidIOCInitFromClassPath() throws Exception {
        AndroidIOC ioc = AndroidIOC.getInstance();
		ioc.initFromClassPath(getContext());
	}
	
	public void testAndroidIOCInitFromClassPathWithName() throws Exception {
        AndroidIOC ioc = AndroidIOC.getInstance();
		ioc.initFromClassPath(getContext(), "android-beans-cp.xml");
	}
}
