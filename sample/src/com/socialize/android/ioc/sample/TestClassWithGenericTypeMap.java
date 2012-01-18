package com.socialize.android.ioc.sample;

import java.util.Map;

import com.socialize.android.ioc.sample.types.GenericType;
import com.socialize.android.ioc.sample.types.SimpleType;

public class TestClassWithGenericTypeMap {

	private Map<String, GenericType<SimpleType>> param;

	public Map<String, GenericType<SimpleType>> getParam() {
		return param;
	}

	public void setParam(Map<String, GenericType<SimpleType>> param) {
		this.param = param;
	}
}
