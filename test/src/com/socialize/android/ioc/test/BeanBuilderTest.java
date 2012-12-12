/*
 * Copyright (c) 2012 Socialize Inc. 
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
package com.socialize.android.ioc.test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.test.AndroidTestCase;

import com.socialize.android.ioc.BeanBuilder;
import com.socialize.android.ioc.BeanRef;
import com.socialize.android.ioc.sample.SubClassOfTestClassWithInitMethod;
import com.socialize.android.ioc.sample.TestClassWithDualMapConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithGenericTypeList;
import com.socialize.android.ioc.sample.TestClassWithGenericTypeMap;
import com.socialize.android.ioc.sample.TestClassWithGenericTypeSet;
import com.socialize.android.ioc.sample.TestClassWithInitMethod;
import com.socialize.android.ioc.sample.TestClassWithIntConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithListConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithWildcardMap;
import com.socialize.android.ioc.sample.types.DerivedType;

public class BeanBuilderTest extends AndroidTestCase {

	public void testConstructor() throws Exception {
		BeanBuilder constructor = new BeanBuilder();
		
		TestClassWithInitMethod b = constructor.construct(TestClassWithInitMethod.class);
		
		assertNotNull(b);
		
		TestClassWithIntConstructorArg fail = constructor.construct(TestClassWithIntConstructorArg.class);
		
		assertNull(fail);
		
		TestClassWithIntConstructorArg b2 = constructor.construct(TestClassWithIntConstructorArg.class, 1);
		
		assertNotNull(b2);
		
		List<TestClassWithInitMethod> subList = new LinkedList<TestClassWithInitMethod>();
		
		TestClassWithListConstructorArg c3 = constructor.construct(TestClassWithListConstructorArg.class, subList);
		
		assertNotNull(c3);
		
		List<TestClassWithIntConstructorArg> subList2 = new LinkedList<TestClassWithIntConstructorArg>();
		
		TestClassWithListConstructorArg c4 = constructor.construct(TestClassWithListConstructorArg.class, subList2);
		
		assertNotNull(c4);
		
	}
	
	public void testPropertySetter() throws Exception {
		BeanBuilder builder = new BeanBuilder();
		
		int val = 69;
		
		TestClassWithIntConstructorArg b = new TestClassWithIntConstructorArg(val);
		BeanRef dummy = new BeanRef();
		
		assertEquals(val, b.getParam());
		
		builder.setProperty(dummy, b, "param", 200);
		
		assertEquals(200, b.getParam());
	}
	
	public void testListComponentTypeMatchConstructor() {
		BeanBuilder builder = new BeanBuilder();
		
		List<TestClassWithInitMethod> linkedObject = new LinkedList<TestClassWithInitMethod>();
		List<String> linkedString = new LinkedList<String>();
		List<TestClassWithInitMethod> arrayObject = new ArrayList<TestClassWithInitMethod>();
		List<String> arrayString = new ArrayList<String>();
		
		linkedObject.add(new TestClassWithInitMethod());
		linkedString.add("foobar");
		arrayObject.add(new TestClassWithInitMethod());
		arrayString.add("foobar");
		
		assertNotNull(builder.getConstructorFor(TestClassWithListConstructorArg.class, linkedObject));
		assertNull(builder.getConstructorFor(TestClassWithListConstructorArg.class, linkedString));
		assertNotNull(builder.getConstructorFor(TestClassWithListConstructorArg.class, arrayObject));
		assertNull(builder.getConstructorFor(TestClassWithListConstructorArg.class, arrayString));
		
	}
	
	public void testMapComponentTypeMatchConstructor() {
		BeanBuilder builder = new BeanBuilder();
		
		Map<String, TestClassWithInitMethod> map0 = new HashMap<String, TestClassWithInitMethod>();
		Map<String, SubClassOfTestClassWithInitMethod> map1 = new HashMap<String, SubClassOfTestClassWithInitMethod>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Integer> map3 = new HashMap<String, Integer>();
		Map<String, Number> map4 = new HashMap<String, Number>();
		
		map0.put("foobar", new TestClassWithInitMethod());
		map1.put("foobar", new SubClassOfTestClassWithInitMethod());
		map2.put("foobar", new Object());
		map3.put("foobar", new Integer(0));
		map4.put("foobar", new BigInteger("1"));
		
		assertNotNull(builder.getConstructorFor(TestClassWithDualMapConstructorArg.class, map0));
		assertNotNull(builder.getConstructorFor(TestClassWithDualMapConstructorArg.class, map1));
		assertNull(builder.getConstructorFor(TestClassWithDualMapConstructorArg.class, map2));
		assertNotNull(builder.getConstructorFor(TestClassWithDualMapConstructorArg.class, map3));
		assertNull(builder.getConstructorFor(TestClassWithDualMapConstructorArg.class, map4));
	}
	
	public void testMapComponentTypeMatchMethod() {
		BeanBuilder builder = new BeanBuilder();
		
		Map<String, TestClassWithInitMethod> map0 = new HashMap<String, TestClassWithInitMethod>();
		Map<String, SubClassOfTestClassWithInitMethod> map1 = new HashMap<String, SubClassOfTestClassWithInitMethod>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		Map<String, Integer> map3 = new HashMap<String, Integer>();
		Map<String, Number> map4 = new HashMap<String, Number>();
		
		map0.put("foobar", new TestClassWithInitMethod());
		map1.put("foobar", new SubClassOfTestClassWithInitMethod());
		map2.put("foobar", new Object());
		map3.put("foobar", new Integer(0));
		map4.put("foobar", new BigInteger("1"));
		
		assertNotNull(builder.getMethodFor(TestClassWithDualMapConstructorArg.class, "setBeanMap",  map0));
		assertNotNull(builder.getMethodFor(TestClassWithDualMapConstructorArg.class, "setBeanMap", map1));
		assertNull(builder.getMethodFor(TestClassWithDualMapConstructorArg.class, "setBeanMap", map2));
		assertNotNull(builder.getMethodFor(TestClassWithDualMapConstructorArg.class, "setStringMap", map3));
		assertNull(builder.getMethodFor(TestClassWithDualMapConstructorArg.class, "setStringMap", map4));
	}
	
	public void testWildcardTypeMatch() {
		BeanBuilder builder = new BeanBuilder();
		
		Map<String, Integer> map0 = new HashMap<String, Integer>();
		Map<String, Object> map1 = new HashMap<String, Object>();
		Map<String, Number> map2 = new HashMap<String, Number>();
		Map<Integer, Number> map3 = new HashMap<Integer, Number>();
		
		map0.put("foobar", new Integer(1));
		map1.put("foobar", new Object());
		map2.put("foobar", new BigDecimal("0"));
		map3.put(1, new BigDecimal("1"));
		
		assertNotNull(builder.getMethodFor(TestClassWithWildcardMap.class, "setWilcardMap",  map0));
		assertNull(builder.getMethodFor(TestClassWithWildcardMap.class, "setWilcardMap", map1));
		assertNotNull(builder.getMethodFor(TestClassWithWildcardMap.class, "setWilcardMap", map2));
		assertNull(builder.getMethodFor(TestClassWithWildcardMap.class, "setWilcardMap", map3));
		
		assertNotNull(builder.getMethodFor(TestClassWithWildcardMap.class, "setAnythingMap",  map0));
		assertNotNull(builder.getMethodFor(TestClassWithWildcardMap.class, "setAnythingMap", map1));
		assertNotNull(builder.getMethodFor(TestClassWithWildcardMap.class, "setAnythingMap", map2));
		assertNull(builder.getMethodFor(TestClassWithWildcardMap.class, "setAnythingMap", map3));
		
	}
	
	public void testGenericTypeMapMatch() {
		BeanBuilder builder = new BeanBuilder();
		Map<String, DerivedType> map0 = new HashMap<String, DerivedType>();
		map0.put("foobar", new DerivedType());
		assertNotNull(builder.getMethodFor(TestClassWithGenericTypeMap.class, "setParam",  map0));
	}
	
	public void testGenericTypeListMatch() {
		BeanBuilder builder = new BeanBuilder();
		List<DerivedType> map0 = new LinkedList<DerivedType>();
		map0.add(new DerivedType());
		assertNotNull(builder.getMethodFor(TestClassWithGenericTypeList.class, "setParam",  map0));
	}
	
	public void testGenericTypeSetMatch() {
		BeanBuilder builder = new BeanBuilder();
		Set<DerivedType> map0 = new HashSet<DerivedType>();
		map0.add(new DerivedType());
		assertNotNull(builder.getMethodFor(TestClassWithGenericTypeSet.class, "setParam",  map0));
	}
}
