package com.socialize.android.ioc.test;

import android.test.AndroidTestCase;

import com.socialize.android.ioc.BeanMapping;
import com.socialize.android.ioc.BeanRef;
import com.socialize.android.ioc.Container;
import com.socialize.android.ioc.ContainerBuilder;
import com.socialize.android.ioc.Argument;
import com.socialize.android.ioc.Argument.RefType;
import com.socialize.android.ioc.MethodRef;
import com.socialize.android.ioc.sample.TestClassWithBeanConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithContextConstuctorArg;
import com.socialize.android.ioc.sample.TestClassWithMultipleProperties;
import com.socialize.android.ioc.sample.SubClassOfTestClassWithInitMethod;
import com.socialize.android.ioc.sample.TestClassWithInitMethod;
import com.socialize.android.ioc.sample.TestClassWithIntConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithInitMethodTakingBean;


public class ContainerBuilderTest extends AndroidTestCase {

	public void testContainerBuilderSimpleBean() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(bean.getClass()));
	}
	
	public void testContainerBuilderSingleBeanWithConstructorArgs() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithIntConstructorArg.class.getName());
		ref.setName(beanName);
		ref.addConstructorArgument(new Argument(null, "69", RefType.INTEGER));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithIntConstructorArg.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithIntConstructorArg bc2 = container.getBean(beanName);
		
		assertEquals(69, bc2.getParam());
	}
	
	public void testContainerBuilderSingleBeanWithContextConstructorArgs() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithContextConstuctorArg.class.getName());
		ref.setName(beanName);
		ref.addConstructorArgument(new Argument(null, null, RefType.CONTEXT));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithContextConstuctorArg.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithContextConstuctorArg bc2 = container.getBean(beanName);
		
		assertNotNull(bc2.getContext());
	}
	
	
	public void testContainerBuilderBeanWithStringProperty() throws Exception {
		String beanName = "bean";
		String prop = "property";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(SubClassOfTestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.addProperty(new Argument("param", prop, RefType.STRING));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(SubClassOfTestClassWithInitMethod.class.isAssignableFrom(bean.getClass()));
		
		SubClassOfTestClassWithInitMethod bc2 = container.getBean(beanName);
		
		assertEquals(prop, bc2.getParam());
	}
	
	
	public void testContainerBuilderBeanWithContextProperty() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithContextConstuctorArg.class.getName());
		ref.setName(beanName);
		ref.addProperty(new Argument("context", null, RefType.CONTEXT));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithContextConstuctorArg.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithContextConstuctorArg bc2 = container.getBean(beanName);
		
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
		ref.setClassName(TestClassWithMultipleProperties.class.getName());
		ref.setName(beanName);
		ref.addProperty(new Argument("integer", String.valueOf(i), RefType.INTEGER));
		ref.addProperty(new Argument("lng", String.valueOf(l), RefType.LONG));
		ref.addProperty(new Argument("shrt", String.valueOf(s), RefType.SHORT));
		ref.addProperty(new Argument("chr", String.valueOf(c), RefType.CHAR));
		ref.addProperty(new Argument("bool", String.valueOf(b), RefType.BOOLEAN));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithMultipleProperties.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithMultipleProperties multi = container.getBean(beanName);
		
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
		ref.setClassName(SubClassOfTestClassWithInitMethod.class.getName());
		ref.setName(bean1Name);
		ref.addProperty(new Argument("param", prop, RefType.STRING));
		
		BeanRef ref2 = new BeanRef();
		ref2.setClassName(TestClassWithBeanConstructorArg.class.getName());
		ref2.setName(bean2Name);
		ref2.addProperty(new Argument("object", bean1Name, RefType.BEAN));
		
		mapping.addBeanRef(ref);
		mapping.addBeanRef(ref2);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(bean1Name);
		Object bean2 = container.getBean(bean2Name);
		
		assertNotNull(bean);
		assertNotNull(bean2);
		
		assertTrue(SubClassOfTestClassWithInitMethod.class.isAssignableFrom(bean.getClass()));
		assertTrue(TestClassWithBeanConstructorArg.class.isAssignableFrom(bean2.getClass()));
		
		TestClassWithBeanConstructorArg bc2 = container.getBean(bean2Name);
		
		assertNotNull(bc2.getObject());
		assertEquals(prop, bc2.getObject().getParam());
	}
	
	
	public void testContainerBuilderBeanWithBeanPropertyReversed() throws Exception {
		String bean1Name = "bean2";
		String bean2Name = "bean1";
		String prop = "property";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(SubClassOfTestClassWithInitMethod.class.getName());
		ref.setName(bean1Name);
		ref.addProperty(new Argument("param", prop, RefType.STRING));
		
		BeanRef ref2 = new BeanRef();
		ref2.setClassName(TestClassWithBeanConstructorArg.class.getName());
		ref2.setName(bean2Name);
		ref2.addProperty(new Argument("object", bean1Name, RefType.BEAN));
		
		mapping.addBeanRef(ref2);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(bean1Name);
		Object bean2 = container.getBean(bean2Name);
		
		assertNotNull(bean);
		assertNotNull(bean2);
		
		assertTrue(SubClassOfTestClassWithInitMethod.class.isAssignableFrom(bean.getClass()));
		assertTrue(TestClassWithBeanConstructorArg.class.isAssignableFrom(bean2.getClass()));
		
		TestClassWithBeanConstructorArg bc2 = container.getBean(bean2Name);
		
		assertNotNull(bc2.getObject());
		assertEquals(prop, bc2.getObject().getParam());
	}
	
	
	public void testContainerBuilderBeanWithBeanConstructorArgumentReversed() throws Exception {
		String bean1Name = "bean2";
		String bean2Name = "bean1";
		String prop = "property";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(SubClassOfTestClassWithInitMethod.class.getName());
		ref.setName(bean1Name);
		ref.addProperty(new Argument("param", prop, RefType.STRING));
		
		BeanRef ref2 = new BeanRef();
		ref2.setClassName(TestClassWithBeanConstructorArg.class.getName());
		ref2.setName(bean2Name);
		ref2.addConstructorArgument(new Argument(null, bean1Name, RefType.BEAN));
		
		mapping.addBeanRef(ref2);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(bean1Name);
		Object bean2 = container.getBean(bean2Name);
		
		assertNotNull(bean);
		assertNotNull(bean2);
		
		assertTrue(SubClassOfTestClassWithInitMethod.class.isAssignableFrom(bean.getClass()));
		assertTrue(TestClassWithBeanConstructorArg.class.isAssignableFrom(bean2.getClass()));
		
		TestClassWithBeanConstructorArg bc2 = container.getBean(bean2Name);
		
		assertNotNull(bc2.getObject());
		assertEquals(prop, bc2.getObject().getParam());
	}
	
	public void testContainerBuilderSingletonBean() {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(true);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object beanA = container.getBean(beanName);
		Object beanB = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanA.getClass()));
		
		assertNotNull(beanB);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanB.getClass()));
		
		assertTrue(beanA == beanB);
	}
	
	public void testContainerBuilderNonSingletonBean() {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		Object beanA = container.getBean(beanName);
		Object beanB = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanA.getClass()));
		
		assertNotNull(beanB);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanB.getClass()));
		
		assertFalse(beanA == beanB);
	}
	
	public void testContainerBuilderSingletonBeanInitMethod() {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(true);
		ref.setInitMethod(new MethodRef("doInit"));
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithInitMethod beanA = container.getBean(beanName);
		
		assertTrue(beanA.isInitialized());
		
	}
	
	public void testContainerBuilderNonSingletonBeanInitMethod() {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		ref.setInitMethod(new MethodRef("doInit"));
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithInitMethod beanA = container.getBean(beanName);
		
		assertTrue(beanA.isInitialized());
		
	}
	
	public void testContainerBuilderBeanInitMethodWithArgs() {
		String beanName = "bean";
		String beanName2 = "bean2";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		
		mapping.addBeanRef(ref);
		
		BeanRef ref2 = new BeanRef();
		ref2.setClassName(TestClassWithInitMethodTakingBean.class.getName());
		ref2.setName(beanName2);
		
		MethodRef method = new MethodRef("init");
		method.addArgument(new Argument(null, null, RefType.CONTEXT));
		method.addArgument(new Argument(null, beanName, RefType.BEAN));
		
		ref2.setInitMethod(method);
		
		mapping.addBeanRef(ref2);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithInitMethodTakingBean beanA = container.getBean(beanName2);
		
		assertNotNull(beanA.getContext());
		assertNotNull(beanA.getObject());
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanA.getObject().getClass()));
	}
	
	public void testContainerBuilderBeanInitMethodWithArgsReversed() {
		String beanName = "bean2";
		String beanName2 = "bean";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		
		BeanRef ref2 = new BeanRef();
		ref2.setClassName(TestClassWithInitMethodTakingBean.class.getName());
		ref2.setName(beanName2);
		
		MethodRef method = new MethodRef("init");
		method.addArgument(new Argument(null, null, RefType.CONTEXT));
		method.addArgument(new Argument(null, beanName, RefType.BEAN));
		
		ref2.setInitMethod(method);
		
		mapping.addBeanRef(ref2);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithInitMethodTakingBean beanA = container.getBean(beanName2);
		
		assertNotNull(beanA.getContext());
		assertNotNull(beanA.getObject());
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanA.getObject().getClass()));
	}
	
}
