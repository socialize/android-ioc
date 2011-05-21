package com.socialize.android.ioc.test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import android.test.AndroidTestCase;

import com.socialize.android.ioc.BeanMapping;
import com.socialize.android.ioc.BeanMappingParser;
import com.socialize.android.ioc.BeanRef;
import com.socialize.android.ioc.KeyValuePair.RefType;

public class BeanMapperParserTest extends AndroidTestCase {
	
	public void testBeanMapperParser() throws IOException {
		
		BeanMappingParser parser = new BeanMappingParser();
		
		BeanMapping mapping = parser.parseFromAsset(getContext());
		
		assertNotNull(mapping);
		
		Collection<BeanRef> beanRefs = mapping.getBeanRefs();
		
		assertNotNull(beanRefs);
		assertEquals(8, beanRefs.size());
		
		 // Taken from test file
		Set<String> names = new HashSet<String>();

		names.add("bean0");
		names.add("bean1");
		names.add("bean2");
		names.add("bean3");
		names.add("bean4");
		names.add("bean5");
		names.add("bean6");
		names.add("bean7");
		
		for (BeanRef beanRef : beanRefs) {
			assertTrue(names.contains(beanRef.getName()));
			
			// Remove otherwise multiple names of the same will pass the test
			names.remove(beanRef.getName());
		}
		
		// Make sure each bean has correct data.
		
		/**
		 * <bean name="bean0" class="com.socialize.android.ioc.sample.TestClass"/>
		 */
		BeanRef beanRef = mapping.getBeanRef("bean0");
		assertNotNull(beanRef);
		assertEquals("com.socialize.android.ioc.sample.TestClass", beanRef.getClassName());
		
		
		/**
		 * <bean name="bean1" class="com.socialize.android.ioc.sample.TestClass2">
				<arg value="22" type="integer"/>
			</bean>
		 */
		
		beanRef = mapping.getBeanRef("bean1");
		assertNotNull(beanRef);
		assertEquals("com.socialize.android.ioc.sample.TestClass2", beanRef.getClassName());
		assertNotNull(beanRef.getConstructorArgs());
		assertEquals(1, beanRef.getConstructorArgs().size());
		assertNotNull(beanRef.getConstructorArgs().get(0));
		assertEquals("22", beanRef.getConstructorArgs().get(0).getValue());
		assertEquals(RefType.INTEGER, beanRef.getConstructorArgs().get(0).getType());
		
		/**
		 * <bean name="bean2" class="com.socialize.android.ioc.sample.SubClass">
				<arg value="foobar" type="string"/>
			</bean>
		 */
		beanRef = mapping.getBeanRef("bean2");
		assertNotNull(beanRef);
		assertEquals("com.socialize.android.ioc.sample.SubClass", beanRef.getClassName());
		assertNotNull(beanRef.getConstructorArgs());
		assertEquals(1, beanRef.getConstructorArgs().size());
		assertNotNull(beanRef.getConstructorArgs().get(0));
		assertEquals("foobar", beanRef.getConstructorArgs().get(0).getValue());
		assertEquals(RefType.STRING, beanRef.getConstructorArgs().get(0).getType());
		
		/**
		 * 	<bean name="bean3" class="com.socialize.android.ioc.sample.SubClass">
				<property name="param" value="foobar" type="string"/>
			</bean>
		 */
		beanRef = mapping.getBeanRef("bean3");
		assertNotNull(beanRef);
		assertEquals("com.socialize.android.ioc.sample.SubClass", beanRef.getClassName());
		assertNotNull(beanRef.getProperties());
		assertEquals(1, beanRef.getProperties().size());
		assertNotNull(beanRef.getProperties().get(0));
		assertEquals("param", beanRef.getProperties().get(0).getKey());
		assertEquals("foobar", beanRef.getProperties().get(0).getValue());
		assertEquals(RefType.STRING, beanRef.getProperties().get(0).getType());
		
		
		/**
		 * 	<bean name="bean4" class="com.socialize.android.ioc.sample.ContainedClass">
				<arg value="bean2" type="bean"/>
			</bean>
		 */
		beanRef = mapping.getBeanRef("bean4");
		assertNotNull(beanRef);
		assertEquals("com.socialize.android.ioc.sample.ContainedClass", beanRef.getClassName());
		assertNotNull(beanRef.getConstructorArgs());
		assertEquals(1, beanRef.getConstructorArgs().size());
		assertNotNull(beanRef.getConstructorArgs().get(0));
		assertEquals("bean2", beanRef.getConstructorArgs().get(0).getValue());
		assertEquals(RefType.BEAN, beanRef.getConstructorArgs().get(0).getType());
		
		/**
		 * 	<bean name="bean5" class="com.socialize.android.ioc.sample.ContextClass">
				<arg type="context"/>
			</bean>
		 */
		beanRef = mapping.getBeanRef("bean5");
		assertNotNull(beanRef);
		assertEquals("com.socialize.android.ioc.sample.ContextClass", beanRef.getClassName());
		assertNotNull(beanRef.getConstructorArgs());
		assertEquals(1, beanRef.getConstructorArgs().size());
		assertNotNull(beanRef.getConstructorArgs().get(0));
		assertEquals(RefType.CONTEXT, beanRef.getConstructorArgs().get(0).getType());
		
		
		/**
		 * 	<bean name="bean6" class="com.socialize.android.ioc.sample.ContextClass">
				<property name="context" type="context"/>
			</bean>
		 */
		beanRef = mapping.getBeanRef("bean6");
		assertNotNull(beanRef);
		assertEquals("com.socialize.android.ioc.sample.ContextClass", beanRef.getClassName());
		assertNotNull(beanRef.getProperties());
		assertEquals(1, beanRef.getProperties().size());
		assertNotNull(beanRef.getProperties().get(0));
		assertEquals("context", beanRef.getProperties().get(0).getKey());
		assertEquals(RefType.CONTEXT, beanRef.getProperties().get(0).getType());
		
		
		/**
			<bean name="bean7" class="com.socialize.android.ioc.sample.MultiProperty">
				<property name="string" value="foobar" type="string"/>
				<property name="integer" value="22" type="integer"/>
				<property name="lng" value="333333" type="long"/>
				<property name="shrt" value="4" type="short"/>
				<property name="chr" value="d" type="char"/>
				<property name="bool" value="true" type="boolean"/>
			</bean>
		 */
		beanRef = mapping.getBeanRef("bean7");
		assertNotNull(beanRef);
		assertEquals("com.socialize.android.ioc.sample.MultiProperty", beanRef.getClassName());
		assertNotNull(beanRef.getProperties());
		assertEquals(6, beanRef.getProperties().size());
		assertNotNull(beanRef.getProperties().get(0));
		
		//<property name="string" value="foobar" type="string"/>
		assertEquals("string", beanRef.getProperties().get(0).getKey());
		assertEquals("foobar", beanRef.getProperties().get(0).getValue());
		assertEquals(RefType.STRING, beanRef.getProperties().get(0).getType());
		
		//<property name="integer" value="22" type="integer"/>
		assertEquals("integer", beanRef.getProperties().get(1).getKey());
		assertEquals("22", beanRef.getProperties().get(1).getValue());
		assertEquals(RefType.INTEGER, beanRef.getProperties().get(1).getType());
		
		//<property name="lng" value="333333" type="long"/>
		assertEquals("lng", beanRef.getProperties().get(2).getKey());
		assertEquals("333333", beanRef.getProperties().get(2).getValue());
		assertEquals(RefType.LONG, beanRef.getProperties().get(2).getType());
		
		//<property name="shrt" value="4" type="short"/>
		assertEquals("shrt", beanRef.getProperties().get(3).getKey());
		assertEquals("4", beanRef.getProperties().get(3).getValue());
		assertEquals(RefType.SHORT, beanRef.getProperties().get(3).getType());
		
		//<property name="chr" value="d" type="char"/>
		assertEquals("chr", beanRef.getProperties().get(4).getKey());
		assertEquals("d", beanRef.getProperties().get(4).getValue());
		assertEquals(RefType.CHAR, beanRef.getProperties().get(4).getType());
		
		//<property name="bool" value="true" type="boolean"/>
		assertEquals("bool", beanRef.getProperties().get(5).getKey());
		assertEquals("true", beanRef.getProperties().get(5).getValue());
		assertEquals(RefType.BOOLEAN, beanRef.getProperties().get(5).getType());
		
	}
	
}
