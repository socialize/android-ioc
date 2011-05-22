package com.socialize.android.ioc.test;

import java.util.LinkedList;
import java.util.List;

import android.test.AndroidTestCase;

import com.socialize.android.ioc.BeanBuilder;
import com.socialize.android.ioc.sample.TestClassWithInitMethod;
import com.socialize.android.ioc.sample.TestClassWithIntConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithListConstructorArg;

public class BeanBuilderTest extends AndroidTestCase {

	public void testConstructor() throws Exception {
		BeanBuilder constructor = new BeanBuilder();
		
		TestClassWithInitMethod b = constructor.construct(TestClassWithInitMethod.class);
		
		assertNotNull(b);
		
		try {
			constructor.construct(TestClassWithIntConstructorArg.class);
			fail();
		}
		catch (Exception e) {}
		
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
		
		assertEquals(val, b.getParam());
		
		builder.setProperty(b, "param", 200);
		
		assertEquals(200, b.getParam());
	}
	
}
