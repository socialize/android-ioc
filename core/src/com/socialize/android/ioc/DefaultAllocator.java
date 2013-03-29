package com.socialize.android.ioc;

import java.lang.reflect.Constructor;
import android.content.Context;


public class DefaultAllocator implements Allocator {
	@Override
	public <T> T allocate(Constructor<T> constructor, Object... args) throws Exception {
		return constructor.newInstance(args);
	}
}
