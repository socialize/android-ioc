package com.socialize.android.ioc.sample;

import java.util.Set;

import com.socialize.android.ioc.sample.types.GenericType;
import com.socialize.android.ioc.sample.types.SimpleType;

public class TestClassWithGenericTypeSet {

	private Set<GenericType<SimpleType>> param;

	public Set<GenericType<SimpleType>> getParam() {
		return param;
	}

	public void setParam(Set<GenericType<SimpleType>> param) {
		this.param = param;
	}
}
