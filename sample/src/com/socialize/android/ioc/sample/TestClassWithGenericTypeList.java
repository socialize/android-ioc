package com.socialize.android.ioc.sample;

import java.util.List;

import com.socialize.android.ioc.sample.types.GenericType;
import com.socialize.android.ioc.sample.types.SimpleType;

public class TestClassWithGenericTypeList {

	private List<GenericType<SimpleType>> param;

	public List<GenericType<SimpleType>> getParam() {
		return param;
	}

	public void setParam(List<GenericType<SimpleType>> param) {
		this.param = param;
	}
}
