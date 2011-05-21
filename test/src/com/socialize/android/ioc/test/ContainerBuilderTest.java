package com.socialize.android.ioc.test;

import android.test.AndroidTestCase;

import com.socialize.android.ioc.BeanMapping;
import com.socialize.android.ioc.BeanRef;
import com.socialize.android.ioc.Container;
import com.socialize.android.ioc.ContainerBuilder;
import com.socialize.android.ioc.KeyValuePair;
import com.socialize.android.ioc.KeyValuePair.RefType;
import com.socialize.android.ioc.test.mocks.TestClass;
import com.socialize.android.ioc.test.mocks.TestClass2;
import com.socialize.android.ioc.test.mocks.ContainedClass;
import com.socialize.android.ioc.test.mocks.ContextClass;
import com.socialize.android.ioc.test.mocks.MultiProperty;
import com.socialize.android.ioc.test.mocks.SubClass;


public class ContainerBuilderTest extends AndroidTestCase {

	public void testContainerBuilderSimpleBean() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClass.class.getName());
		ref.setName(beanName);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClass.class.isAssignableFrom(bean.getClass()));
	}
	
	public void testContainerBuilderSingleBeanWithConstructorArgs() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClass2.class.getName());
		ref.setName(beanName);
		ref.addConstructorArgument(new KeyValuePair(null, "69", RefType.INTEGER));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClass2.class.isAssignableFrom(bean.getClass()));
		
		TestClass2 bc2 = container.getBean(beanName);
		
		assertEquals(69, bc2.getParam());
	}
	
	public void testContainerBuilderSingleBeanWithContextConstructorArgs() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(ContextClass.class.getName());
		ref.setName(beanName);
		ref.addConstructorArgument(new KeyValuePair(null, null, RefType.CONTEXT));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(ContextClass.class.isAssignableFrom(bean.getClass()));
		
		ContextClass bc2 = container.getBean(beanName);
		
		assertNotNull(bc2.getContext());
	}
	
	
	public void testContainerBuilderBeanWithStringProperty() throws Exception {
		String beanName = "bean";
		String prop = "property";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(SubClass.class.getName());
		ref.setName(beanName);
		ref.addProperty(new KeyValuePair("param", prop, RefType.STRING));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(SubClass.class.isAssignableFrom(bean.getClass()));
		
		SubClass bc2 = container.getBean(beanName);
		
		assertEquals(prop, bc2.getParam());
	}
	
	
	public void testContainerBuilderBeanWithContextProperty() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(ContextClass.class.getName());
		ref.setName(beanName);
		ref.addProperty(new KeyValuePair("context", null, RefType.CONTEXT));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(ContextClass.class.isAssignableFrom(bean.getClass()));
		
		ContextClass bc2 = container.getBean(beanName);
		
		assertNotNull(bc2.getContext());
	}
	
	
	public void testContainerBuilderBeanWithMultiProperty() throws Exception {
		String beanName = "bean";
		
		int i = 69;
		long l = 696969;
		short s = 6;
		char c = 'c';
		boolean b = true;
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(MultiProperty.class.getName());
		ref.setName(beanName);
		ref.addProperty(new KeyValuePair("integer", String.valueOf(i), RefType.INTEGER));
		ref.addProperty(new KeyValuePair("lng", String.valueOf(l), RefType.LONG));
		ref.addProperty(new KeyValuePair("shrt", String.valueOf(s), RefType.SHORT));
		ref.addProperty(new KeyValuePair("chr", String.valueOf(c), RefType.CHAR));
		ref.addProperty(new KeyValuePair("bool", String.valueOf(b), RefType.BOOLEAN));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(MultiProperty.class.isAssignableFrom(bean.getClass()));
		
		MultiProperty multi = container.getBean(beanName);
		
		assertEquals(i, multi.getInteger());
		assertEquals(l, multi.getLng());
		assertEquals(s, multi.getShrt());
		assertEquals(c, multi.getChr());
		assertEquals(b, multi.isBool());		
	}
	
	public void testContainerBuilderBeanWithBeanProperty() throws Exception {
		String bean1Name = "bean1";
		String bean2Name = "bean2";
		String prop = "property";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(SubClass.class.getName());
		ref.setName(bean1Name);
		ref.addProperty(new KeyValuePair("param", prop, RefType.STRING));
		
		BeanRef ref2 = new BeanRef();
		ref2.setClassName(ContainedClass.class.getName());
		ref2.setName(bean2Name);
		ref2.addProperty(new KeyValuePair("object", bean1Name, RefType.BEAN));
		
		mapping.addBeanRef(ref);
		mapping.addBeanRef(ref2);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(bean1Name);
		Object bean2 = container.getBean(bean2Name);
		
		assertNotNull(bean);
		assertNotNull(bean2);
		
		assertTrue(SubClass.class.isAssignableFrom(bean.getClass()));
		assertTrue(ContainedClass.class.isAssignableFrom(bean2.getClass()));
		
		ContainedClass bc2 = container.getBean(bean2Name);
		
		assertNotNull(bc2.getObject());
		assertEquals(prop, bc2.getObject().getParam());
	}
	
	
	public void testContainerBuilderBeanWithBeanPropertyReversed() throws Exception {
		String bean1Name = "bean1";
		String bean2Name = "bean2";
		String prop = "property";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(SubClass.class.getName());
		ref.setName(bean1Name);
		ref.addProperty(new KeyValuePair("param", prop, RefType.STRING));
		
		BeanRef ref2 = new BeanRef();
		ref2.setClassName(ContainedClass.class.getName());
		ref2.setName(bean2Name);
		ref2.addProperty(new KeyValuePair("object", bean1Name, RefType.BEAN));
		
		mapping.addBeanRef(ref2);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(bean1Name);
		Object bean2 = container.getBean(bean2Name);
		
		assertNotNull(bean);
		assertNotNull(bean2);
		
		assertTrue(SubClass.class.isAssignableFrom(bean.getClass()));
		assertTrue(ContainedClass.class.isAssignableFrom(bean2.getClass()));
		
		ContainedClass bc2 = container.getBean(bean2Name);
		
		assertNotNull(bc2.getObject());
		assertEquals(prop, bc2.getObject().getParam());
	}
	
	
	public void testContainerBuilderBeanWithBeanConstructorArgumentReversed() throws Exception {
		String bean1Name = "bean1";
		String bean2Name = "bean2";
		String prop = "property";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(SubClass.class.getName());
		ref.setName(bean1Name);
		ref.addProperty(new KeyValuePair("param", prop, RefType.STRING));
		
		BeanRef ref2 = new BeanRef();
		ref2.setClassName(ContainedClass.class.getName());
		ref2.setName(bean2Name);
		ref2.addConstructorArgument(new KeyValuePair(null, bean1Name, RefType.BEAN));
		
		mapping.addBeanRef(ref2);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(bean1Name);
		Object bean2 = container.getBean(bean2Name);
		
		assertNotNull(bean);
		assertNotNull(bean2);
		
		assertTrue(SubClass.class.isAssignableFrom(bean.getClass()));
		assertTrue(ContainedClass.class.isAssignableFrom(bean2.getClass()));
		
		ContainedClass bc2 = container.getBean(bean2Name);
		
		assertNotNull(bc2.getObject());
		assertEquals(prop, bc2.getObject().getParam());
	}
	
}
