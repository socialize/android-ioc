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

/**
 * 
 * @author Jason Polites
 *
 */
public class BeanBuilder {

	@SuppressWarnings("unchecked")
	public <T extends Object> T construct(String className) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return (T) construct(className, (Object[])null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T construct(String className, Object...args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
		return (T) construct((Class<T>)Class.forName(className), args);
	}
	
	public <T extends Object> T construct(Class<T> clazz) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		return (T) construct(clazz, (Object[])null);
	}

	public <T extends Object> T construct(Class<T> clazz, Object...args) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {

		T object = null;

		Constructor<T> matched = getConstructorFor(clazz, args);

		if(matched != null) {
			object = matched.newInstance(args);
		}
		else {
			// No constructor found.
			throw new InstantiationException("No valid constructor found for class [" +
					clazz.getName() +
			"]");
		}

		return object;
	}
	
	
	@SuppressWarnings("unchecked")
	public <T extends Object> T coerce(KeyValuePair value) {
		
		Object coerced = null;
		
		if(value.getValue() != null) {
			switch(value.getType()) {
			
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
				
				case BEAN:
						throw new IllegalArgumentException("Cannot coerce a bean");
			
			}
		}
		
		return (T) coerced;
	}
	
	public void setProperty(Object instance, String name, Object value) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		
		Class<?> cls = instance.getClass();
		
		String setterName = "set" + name.substring(0,1).toUpperCase() + name.substring(1, name.length());
		
		Method[] methods = cls.getMethods();
		
		for (Method method : methods) {
			
			if(method.getName().equals(setterName)) {
				Class<?>[] types = method.getParameterTypes();
				if(types != null && types.length == 1) {
					
					Class<?> type = types[0];
					
					if(type.isPrimitive()) {
						if(isUnboxableToPrimitive(type, value, true)) {
							method.invoke(instance, value);
							break;
						}
					}
					else if(types[0].isAssignableFrom(value.getClass())) {
						method.invoke(instance, value);
						break;
					}
					
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private <T> Constructor<T> getConstructorFor(Class<T> clazz, Object... args)
	throws SecurityException {
		Constructor<T>[] constructors = (Constructor<T>[]) clazz.getConstructors();
		Constructor<T> compatibleConstructor = null;
		for (Constructor<T> constructor : constructors) {
			Class<?>[] params = constructor.getParameterTypes();
			if (params != null && args != null && params.length == args.length) {
				boolean exactMatch = true;
				boolean compatibleMatch = true;
				for (int i = 0; i < params.length; ++i) {
					Object arg = args[i];
					if (arg == null) {
						arg = Void.TYPE;
					}

					if (!params[i].isAssignableFrom(arg.getClass())) {
						if (params[i].isPrimitive()) {
							exactMatch &= isUnboxableToPrimitive(params[i], arg, true);
							compatibleMatch &= isUnboxableToPrimitive(params[i], arg, false);
						} else {
							exactMatch = false;
							compatibleMatch = false;
						}
					}
				}
				if (exactMatch) {
					return constructor;
				} else if (compatibleMatch) {
					compatibleConstructor = constructor;
				}
			}
			else if((params == null || params.length == 0) && (args == null || args.length == 0)) {
				return constructor;
			}
		}
		
		if (compatibleConstructor != null) {
			return compatibleConstructor;
		}

		return null;
	}

	private boolean isUnboxableToPrimitive(Class<?> clazz, Object arg, boolean exactMatch) {
		if (!clazz.isPrimitive()) {
			throw new IllegalArgumentException(
					"Internal Error - The class to test against is not a primitive");
		}
		Class<?> unboxedType = null;
		if (arg.getClass().equals(Integer.class)) {
			unboxedType = Integer.TYPE;
		} else if (arg.getClass().equals(Long.class)) {
			unboxedType = Long.TYPE;
		} else if (arg.getClass().equals(Byte.class)) {
			unboxedType = Byte.TYPE;
		} else if (arg.getClass().equals(Short.class)) {
			unboxedType = Short.TYPE;
		} else if (arg.getClass().equals(Character.class)) {
			unboxedType = Character.TYPE;
		} else if (arg.getClass().equals(Float.class)) {
			unboxedType = Float.TYPE;
		} else if (arg.getClass().equals(Double.class)) {
			unboxedType = Double.TYPE;
		} else if (arg.getClass().equals(Boolean.class)) {
			unboxedType = Boolean.TYPE;
		} else {
			return false;
		}
		if (exactMatch) {
			return clazz == unboxedType;
		}
		return isAssignable(clazz, unboxedType);
	}

	private boolean isAssignable(Class<?> to, Class<?> from) {
		if (to == Byte.TYPE) {
			return from == Byte.TYPE;
		} else if (to == Short.TYPE){
			return from == Byte.TYPE || from == Short.TYPE || from == Character.TYPE;
		} else if (to == Integer.TYPE || to == Character.TYPE) {
			return from == Byte.TYPE || from == Short.TYPE || from == Integer.TYPE
			|| from == Character.TYPE;
		} else if (to == Long.TYPE) {
			return from == Byte.TYPE || from == Short.TYPE || from == Integer.TYPE || from == Long.TYPE
			|| from == Character.TYPE;
		} else if (to == Float.TYPE) {
			return from == Byte.TYPE || from == Short.TYPE || from == Integer.TYPE
			|| from == Character.TYPE || from == Float.TYPE;
		} else if (to == Double.TYPE) {
			return from == Byte.TYPE || from == Short.TYPE || from == Integer.TYPE || from == Long.TYPE
			|| from == Character.TYPE || from == Float.TYPE || from == Double.TYPE;
		} else if (to == Boolean.TYPE) {
			return from == Boolean.TYPE;
		} else {
			return to.isAssignableFrom(from);
		}
	}
}
