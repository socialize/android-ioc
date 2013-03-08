package com.socialize.android.ioc;

import java.lang.reflect.Constructor;
import android.content.Context;


public class DefaultAllocator implements Allocator {
	@Override
	public <T> T allocate(Context context, Constructor<T> constructor, Object... args) throws Exception {
		return constructor.newInstance(args);
	}
}
