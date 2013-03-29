package com.socialize.android.ioc;

import java.lang.reflect.Constructor;
import android.content.Context;

public interface Allocator {

	public <T> T allocate(Constructor<T> constructor, Object... args) throws Exception;

}