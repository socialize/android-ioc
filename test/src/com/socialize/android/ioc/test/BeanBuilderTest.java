package com.socialize.android.ioc.test;

import java.util.LinkedList;
import java.util.List;

import android.test.AndroidTestCase;

import com.socialize.android.ioc.BeanBuilder;
import com.socialize.android.ioc.sample.SubClass2;
import com.socialize.android.ioc.sample.TestClass;
import com.socialize.android.ioc.sample.TestClass2;
import com.socialize.android.ioc.sample.TestClass3;

public class BeanBuilderTest extends AndroidTestCase {

	public void testConstructor() throws Exception {
		BeanBuilder constructor = new BeanBuilder();
		
		TestClass b = constructor.construct(TestClass.class);
		
		assertNotNull(b);
		
		try {
			constructor.construct(TestClass2.class);
			fail();
		}
		catch (Exception e) {}
		
		TestClass2 b2 = constructor.construct(TestClass2.class, 1);
		
		assertNotNull(b2);
		
		List<SubClass2> subList = new LinkedList<SubClass2>();
		
		TestClass3 c3 = constructor.construct(TestClass3.class, subList);
		
		assertNotNull(c3);
		
		List<TestClass2> subList2 = new LinkedList<TestClass2>();
		
		TestClass3 c4 = constructor.construct(TestClass3.class, subList2);
		
		assertNotNull(c4);
		
	}
	
	public void testPropertySetter() throws Exception {
		BeanBuilder builder = new BeanBuilder();
		
		int val = 69;
		
		TestClass2 b = new TestClass2(val);
		
		assertEquals(val, b.getParam());
		
		builder.setProperty(b, "param", 200);
		
		assertEquals(200, b.getParam());
	}
	
}
