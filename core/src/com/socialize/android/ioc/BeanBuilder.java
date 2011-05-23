/*
 * Copyright (c) 2011 Socialize Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.socialize.android.ioc;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Jason Polites
 * 
 */
public class BeanBuilder {

	@SuppressWarnings("unchecked")
	public <T extends Object> T construct(String className) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return (T) construct(className, (Object[]) null);
	}

	@SuppressWarnings("unchecked")
	public <T extends Object> T construct(String className, Object... args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException,
			ClassNotFoundException {
		return (T) construct((Class<T>) Class.forName(className), args);
	}

	public <T extends Object> T construct(Class<T> clazz) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return (T) construct(clazz, (Object[]) null);
	}

	public <T extends Object> T construct(Class<T> clazz, Object... args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {

		T object = null;

		Constructor<T> matched = getConstructorFor(clazz, args);

		if (matched != null) {
			object = matched.newInstance(args);
		}
		else {
			// No constructor found.
			Logger.e(getClass().getSimpleName(), "No valid constructor found for class [" + clazz.getName() + "]");
		}

		return object;
	}

	public Object coerce(Argument value) {

		Object coerced = null;

		if (value.getValue() != null) {
			switch (value.getType()) {

			case BOOLEAN:
				coerced = Boolean.valueOf(value.getValue());
				break;

			case BYTE:
				coerced = Byte.valueOf(value.getValue());
				break;

			case CHAR:
				coerced = Character.valueOf(value.getValue().toString().toCharArray()[0]);
				break;

			case INTEGER:
				coerced = Integer.valueOf(value.getValue());
				break;

			case LONG:
				coerced = Long.valueOf(value.getValue());
				break;

			case SHORT:
				coerced = Short.valueOf(value.getValue());
				break;

			case STRING:
				coerced = value.getValue();
				break;

			default:
				throw new IllegalArgumentException("Cannot coerce a value of type " + value.getType().name());

			}
		}

		return coerced;
	}

	public void setProperty(Object instance, String name, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Class<?> cls = instance.getClass();

		String setterName = "set" + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());

		Method[] methods = cls.getMethods();

		for (Method method : methods) {

			if (method.getName().equals(setterName)) {
				Class<?>[] paramTypes = method.getParameterTypes();
				Type[] types = method.getGenericParameterTypes();
				if (types != null && types.length == 1) {
					if(isMethodMatched(paramTypes, types, value)) {
						method.invoke(instance, value);
						break;
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Constructor<T> getConstructorFor(Class<T> clazz, Object... args) throws SecurityException {
		Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
		Constructor<T> compatibleConstructor = null;
		for (Constructor<T> constructor : constructors) {
			Class<?>[] params = constructor.getParameterTypes();
			Type[] types = constructor.getGenericParameterTypes();
			if(isMethodMatched(params,types, args)) {
				compatibleConstructor = constructor;
				break;
			}
		}

		if (compatibleConstructor != null) {
			return compatibleConstructor;
		}

		return null;
	}

	public Method getMethodFor(Class<?> clazz, String name, Object... args) {
		Method[] methods = clazz.getMethods();
		Method compatibleMethod = null;
		for (Method method : methods) {
			if (method.getName().equals(name)) {
				Class<?>[] params = method.getParameterTypes();
				Type[] types = method.getGenericParameterTypes();
				if(isMethodMatched(params, types, args)) {
					compatibleMethod = method;
					break;
				}
			}
		}

		if (compatibleMethod != null) {
			return compatibleMethod;
		}

		return null;
	}
	
	private boolean isMethodMatched(Class<?>[] params, Type[] genericParams, Object...args) {
		
		if (params != null && args != null && params.length == args.length) {
			boolean match = true;
			for (int i = 0; i < params.length; ++i) {
				
				Object arg = args[i];
				
				if (arg == null) {
					arg = Void.TYPE;
				}

				if (!params[i].isAssignableFrom(arg.getClass())) {
					if (params[i].isPrimitive()) {
						match &= isUnboxableToPrimitive(params[i], arg);
					}
					else {
						match = false;
					}
				}
				else if(List.class.isAssignableFrom(params[i])) {
					match &= isListMatch(genericParams, i, arg);
				}
				else if(Set.class.isAssignableFrom(params[i])) {
					match &= isSetMatch(genericParams, i, arg);
				}
				else if(Map.class.isAssignableFrom(params[i])) {
					match &= isMapMatch(genericParams, i, arg);
				}
				else if(params[i].isArray()) {
					match &= isArrayMatch(genericParams, i, arg);
				}
				else {
					match &= true;
				}
				
				if(!match) break;
			}
			
			return match;
		}
		else if ((params == null || params.length == 0) && (args == null || args.length == 0)) {
			return true;
		}
		
		return false;
	}
	
	private boolean isListMatch(Type[] genericParams, int index, Object arg) {

		List<?> asColl = (List<?>) arg;

		if(asColl.size() > 0) {
			ParameterizedType parType = (ParameterizedType) genericParams[index];
			if(parType.getActualTypeArguments()[0].equals(asColl.get(0).getClass())) {
				return true;
			}
		}
		else {
			return true;
		}
			
		return false;
	}
	
	private boolean isSetMatch(Type[] genericParams, int index, Object arg) {
		Set<?> asColl = (Set<?>) arg;

		if(asColl.size() > 0) {
			ParameterizedType parType = (ParameterizedType) genericParams[index];
			
			Type actualType = parType.getActualTypeArguments()[0];
			Class<?> componentType = asColl.iterator().next().getClass();
			
			if(actualType.equals(componentType)) {
				return true;
			}
		}
		else {
			return true;
		}
			
		return false;
	}
	
	private boolean isMapMatch(Type[] genericParams, int index, Object arg) {
		
		Map<?,?> asMap = (Map<?,?>) arg;
		
		if(asMap.size() > 0) {
			
			ParameterizedType parType = (ParameterizedType) genericParams[index];
			
			Type keyType = parType.getActualTypeArguments()[0];
			Type valType = parType.getActualTypeArguments()[1];
			
			Object key = asMap.keySet().iterator().next();
			Object value = asMap.get(key);
			
			if(keyType.equals(key.getClass()) && valType.equals(value.getClass())) {
				return true;
			}
		}
		else {
			return true;
		}
		
		return false;
	}
	
	private boolean isArrayMatch(Type[] genericParams, int index, Object arg) {
		ParameterizedType parType = (ParameterizedType) genericParams[index];
		
		if(parType.getActualTypeArguments()[0].equals(arg.getClass().getComponentType())) {
			return true;
		}
		
		return false;
	}

	private boolean isUnboxableToPrimitive(Class<?> clazz, Object arg) {
		if (!clazz.isPrimitive()) {
			throw new IllegalArgumentException("Internal Error - The class to test against is not a primitive");
		}
		Class<?> unboxedType = null;
		if (arg.getClass().equals(Integer.class)) {
			unboxedType = Integer.TYPE;
		}
		else if (arg.getClass().equals(Long.class)) {
			unboxedType = Long.TYPE;
		}
		else if (arg.getClass().equals(Byte.class)) {
			unboxedType = Byte.TYPE;
		}
		else if (arg.getClass().equals(Short.class)) {
			unboxedType = Short.TYPE;
		}
		else if (arg.getClass().equals(Character.class)) {
			unboxedType = Character.TYPE;
		}
		else if (arg.getClass().equals(Float.class)) {
			unboxedType = Float.TYPE;
		}
		else if (arg.getClass().equals(Double.class)) {
			unboxedType = Double.TYPE;
		}
		else if (arg.getClass().equals(Boolean.class)) {
			unboxedType = Boolean.TYPE;
		}
		else {
			return false;
		}

		return isAssignable(clazz, unboxedType);
	}

	private boolean isAssignable(Class<?> to, Class<?> from) {
		if (to == Byte.TYPE) {
			return from == Byte.TYPE;
		}
		else if (to == Short.TYPE) {
			return from == Byte.TYPE || from == Short.TYPE || from == Character.TYPE;
		}
		else if (to == Integer.TYPE || to == Character.TYPE) {
			return from == Byte.TYPE || from == Short.TYPE || from == Integer.TYPE || from == Character.TYPE;
		}
		else if (to == Long.TYPE) {
			return from == Byte.TYPE || from == Short.TYPE || from == Integer.TYPE || from == Long.TYPE || from == Character.TYPE;
		}
		else if (to == Float.TYPE) {
			return from == Byte.TYPE || from == Short.TYPE || from == Integer.TYPE || from == Character.TYPE || from == Float.TYPE;
		}
		else if (to == Double.TYPE) {
			return from == Byte.TYPE || from == Short.TYPE || from == Integer.TYPE || from == Long.TYPE || from == Character.TYPE || from == Float.TYPE || from == Double.TYPE;
		}
		else if (to == Boolean.TYPE) {
			return from == Boolean.TYPE;
		}
		else {
			return to.isAssignableFrom(from);
		}
	}
}
