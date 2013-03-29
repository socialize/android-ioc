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

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.*;
import com.socialize.android.ioc.Argument.CollectionType;
import com.socialize.android.ioc.Argument.RefType;
import com.socialize.android.ioc.sample.*;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.util.*;


public class ContainerBuilderTest extends AndroidTestCase {
	
	@UsesMocks ({BeanMappingParserHandler.class, BeanMapping.class, ParserHandlerFactory.class, SAXParserFactory.class, SAXParser.class})
	public void testContainerBuilderMultipleConfig() throws Exception {
		
		BeanMappingParserHandler handler = AndroidMock.createNiceMock(BeanMappingParserHandler.class);
		BeanMapping mapping0 = AndroidMock.createMock(BeanMapping.class);
		BeanMapping mapping1 = AndroidMock.createMock(BeanMapping.class);
		ParserHandlerFactory factory = AndroidMock.createMock(ParserHandlerFactory.class);
		SAXParserFactory saxFactory = AndroidMock.createMock(SAXParserFactory.class);
		SAXParser saxParser = AndroidMock.createMock(SAXParser.class);
		
		InputStream in0 = new ByteArrayInputStream(new byte[]{});
		InputStream in1 = new ByteArrayInputStream(new byte[]{});
		
		InputStream[] streams = {in0, in1};
		
		Context context = new MockContext();
		
		BeanMappingParser parser = new BeanMappingParser(factory, saxFactory);
		
		AndroidMock.expect(saxFactory.newSAXParser()).andReturn(saxParser);
		
		saxParser.parse(in0, handler);
		saxParser.parse(in1, handler);
		
		AndroidMock.expect(factory.newInstance()).andReturn(handler).times(streams.length);
		AndroidMock.expect(handler.getBeanMapping()).andReturn(mapping0);
		AndroidMock.expect(handler.getBeanMapping()).andReturn(mapping1);
		AndroidMock.expect(mapping0.getBeanRefs()).andReturn(null);
		
		// Expect merge
		mapping0.merge(mapping1);

		AndroidMock.replay(saxFactory);
		AndroidMock.replay(saxParser);
		AndroidMock.replay(factory);
		AndroidMock.replay(handler);
		AndroidMock.replay(mapping0);
		AndroidMock.replay(mapping1);
		
		ContainerBuilder builder = new ContainerBuilder(parser) {
			@Override
			public Container build(Context context, BeanMapping mapping) {
				// Don't want this to be tested in this test.
				// We will verify the actual build process in a more detailed integration test.
				return null;
			}
		};
		
		builder.build(getContext(), streams);
		
		AndroidMock.verify(factory);
		AndroidMock.verify(handler);
		AndroidMock.verify(mapping0);
		AndroidMock.verify(mapping1);
	}

	public void testContainerBuilderSimpleBean() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(bean.getClass()));
	}
	
	public void testContainerBuilderProxyFailsWithNonInterfaceClass() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithBeanProperty.class.getName());
		ref.setName(beanName);
		
		mapping.addBeanRef(ref);
		mapping.addProxyRef(beanName);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertFalse(Proxy.isProxyClass(bean.getClass()));
	}
	
	@SuppressWarnings("unchecked")
	public void testContainerBuilderProxy() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithPrintMethod.class.getName());
		ref.setName(beanName);
		
		mapping.addBeanRef(ref);
		mapping.addProxyRef(beanName);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		ProxyObject<TestClassWithPrintMethod> proxy = container.getProxy(beanName);
		
		assertNotNull(proxy);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(Proxy.isProxyClass(bean.getClass()));

		ProxyObject<TestClassWithPrintMethod> proxy2 = (ProxyObject<TestClassWithPrintMethod>) Proxy.getInvocationHandler(bean);
		
		assertNotNull(proxy2.getDelegate());
		
		TestClassWithPrintMethod newBean = new  TestClassWithPrintMethod();
		
		// Now change the proxy delegate
		proxy2.setDelegate(newBean);
		
		bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(Proxy.isProxyClass(bean.getClass()));
		
		ProxyObject<TestClassWithPrintMethod> proxy3 = (ProxyObject<TestClassWithPrintMethod>) Proxy.getInvocationHandler(bean);
		
		assertNotNull(proxy3.getDelegate());
		assertSame(newBean, proxy3.getDelegate());
		
		ProxyObject<TestClassWithPrintMethod> proxy4 = container.getProxy(beanName);
		
		assertNotNull(proxy4.getDelegate());
		assertSame(newBean, proxy4.getDelegate());
		
	}	
	
	public void testContainerBuilderProxyUsesDelegate() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithPrintMethod.class.getName());
		ref.setName(beanName);
		
		mapping.addBeanRef(ref);
		mapping.addProxyRef(beanName);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
	
		ITestClassWithPrintMethod bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertEquals("Hello World", bean.print());
		
		ProxyObject<TestClassWithPrintMethod> proxy = container.getProxy(beanName);
		
		proxy.setDelegate(new TestClassWithPrintMethod(){
			@Override
			public String print() {
				return "foobar";
			}
		});
		
		bean = container.getBean(beanName);

		assertNotNull(bean);
		assertEquals("foobar", bean.print());
		
	}	
	
	public void testRuntimeProxyUsesDelegate() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithPrintMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
	
		ITestClassWithPrintMethod bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertEquals("Hello World", bean.print());
		
		container.setStaticRuntimeProxy(beanName, new TestClassWithPrintMethod(){
			@Override
			public String print() {
				return "foobar";
			}
		});
		
		bean = container.getBean(beanName);

		assertNotNull(bean);
		
		assertEquals("foobar", bean.print());
	}	
	
	public void testStaticProxyUsesDelegate() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithPrintMethod.class.getName());
		ref.setName(beanName);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		AndroidIOC.registerProxy(beanName, new TestClassWithPrintMethod(){
			@Override
			public String print() {
				return "foobar";
			}
		});
		
		Container container = builder.build(getContext(), mapping);
	
		ITestClassWithPrintMethod bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertEquals("foobar", bean.print());
		
		AndroidIOC.unregisterProxy(beanName);
		
		bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertEquals("Hello World", bean.print());
	}		
		
	
	public void testContainerBuilderProxyDoesNOTUseDelegateOnNonSingleton() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithPrintMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		
		mapping.addBeanRef(ref);
		mapping.addProxyRef(beanName);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
	
		ITestClassWithPrintMethod bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertEquals("Hello World", bean.print());
		
		ProxyObject<TestClassWithPrintMethod> proxy = container.getProxy(beanName);
		
		proxy.setDelegate(new TestClassWithPrintMethod(){
			@Override
			public String print() {
				return "foobar";
			}
		});
		
		bean = container.getBean(beanName);

		assertNotNull(bean);
		assertEquals("Hello World", bean.print());
		
	}	
	
	public void testContainerAware() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName("com.socialize.android.ioc.sample.TestClassContainerAware");
		ref.setName(beanName);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassContainerAware.class.isAssignableFrom(bean.getClass()));
		
		TestClassContainerAware aware = (TestClassContainerAware) bean;
		
		assertNotNull(aware.getContainer());
		assertSame(container, aware.getContainer());
	}
	
	public void testContainerSize() throws Exception {
		
		String beanName0 = "bean0";
		String beanName1 = "bean1";
		String beanName2 = "bean2";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef ref0 = new BeanRef();
		BeanRef ref1 = new BeanRef();
		BeanRef ref2 = new BeanRef();
		
		ref0.setClassName(TestClassWithInitMethod.class.getName());
		ref0.setName(beanName0);
		
		ref1.setClassName(TestClassWithInitMethod.class.getName());
		ref1.setName(beanName1);
		
		ref2.setClassName(TestClassWithInitMethod.class.getName());
		ref2.setName(beanName2);
		
		
		mapping.addBeanRef(ref0);
		mapping.addBeanRef(ref1);
		mapping.addBeanRef(ref2);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		assertEquals(3, container.size());
	}
	
	public void testContainerBuilderBeanWithIntConstructorArgs() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithIntConstructorArg.class.getName());
		ref.setName(beanName);
		ref.addConstructorArgument(new Argument(null, "69", RefType.INTEGER));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithIntConstructorArg.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithIntConstructorArg bc2 = container.getBean(beanName);
		
		assertEquals(69, bc2.getParam());
	}
	
	public void testBeanMaker() throws Exception {
		String beanName = "bean";
		String beanMakerName = "beanMaker";
		String beanMakeeName = "beanMakee";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef makerRef = new BeanRef();
		makerRef.setClassName("com.socialize.android.ioc.sample.TestBeanMaker");
		makerRef.addProperty(new Argument("beanName", beanMakeeName, RefType.STRING));
		makerRef.setName(beanMakerName);
		makerRef.setSingleton(true);
		
		BeanRef makeeRef = new BeanRef();
		makeeRef.setClassName(TestBeanMakee.class.getName());
		makeeRef.setName(beanMakeeName);
		makeeRef.setSingleton(true);			
		
		BeanRef beanRef = new BeanRef();
		beanRef.setClassName(TestClassWithBeanProperty.class.getName());
		beanRef.addProperty(new Argument("bean", beanMakerName, RefType.BEAN));
		beanRef.setName(beanName);
		beanRef.setSingleton(true);		
		
		mapping.addBeanRef(beanRef);
		mapping.addBeanRef(makeeRef);
		mapping.addBeanRef(makerRef);
		
		ContainerBuilder builder = new ContainerBuilder();
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithBeanProperty bean = container.getBean(beanName);
		assertNotNull(bean);
		
		Object object = bean.getBean();
		
		assertNotNull(object);
		assertTrue(object instanceof TestBeanMakee);
	}
	
	public void testExtraConstructorArgsInt() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithMultipleConstructorArg.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithMultipleConstructorArg bean = null;
		
		bean = container.getBean(beanName, 69);
		assertNotNull(bean);
		assertEquals(69, bean.getInteger());
	}
	
	public void testExtraConstructorArgsString() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithMultipleConstructorArg.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithMultipleConstructorArg bean = null;
		
		bean = container.getBean(beanName, "foobar");
		assertNotNull(bean);
		assertEquals("foobar", bean.getString());
	}
	
	public void testExtraConstructorArgsObject() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithMultipleConstructorArg.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithMultipleConstructorArg bean = null;
		
		Object value = new Long(23);
		bean = container.getBean(beanName, value);
		assertNotNull(bean);
		assertEquals(value, bean.getObject());
	}
	
	public void testExtraConstructorArgsList() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithMultipleConstructorArg.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithMultipleConstructorArg bean = null;
		
		List<String> strings = new LinkedList<String>();
		bean = container.getBean(beanName, strings);
		assertNotNull(bean);
		assertSame(strings, bean.getStringList());
	}
	
	public void testExtraConstructorArgsMultiList() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithMultipleConstructorArg.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithMultipleConstructorArg bean = null;
		
		List<Integer> ints = new LinkedList<Integer>();
		List<String> strings = new LinkedList<String>();
		
		bean = container.getBean(beanName, strings, ints);
		assertNotNull(bean);
		assertSame(strings, bean.getStringList());
		assertSame(ints, bean.getIntList());
	}
	
	
	
	
	
	public void testContainerBuilderBeanWithStringListConstructorArgsOnMultiConstructorClass() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithDualListConstructorArg.class.getName());
		ref.setName(beanName);
		
		Argument listElement0 = new Argument(null, "foo", RefType.STRING);
		Argument listElement1 = new Argument(null, "bar", RefType.STRING);
		Argument list = new Argument(null, null, RefType.LIST);
		
		list.addChild(listElement0);
		list.addChild(listElement1);
		
		ref.addConstructorArgument(list);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithDualListConstructorArg.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithDualListConstructorArg typedBean = container.getBean(beanName);
		
		List<String> stringList = typedBean.getStringList();
		
		assertNotNull(stringList);
		assertTrue(LinkedList.class.isAssignableFrom(stringList.getClass()));
		assertEquals(2, stringList.size());
		
		String string0 = stringList.get(0);
		String string1 = stringList.get(1);
		
		assertEquals("foo", string0);
		assertEquals("bar", string1);
	}
	
	public void testExtraConstructorArgsMergedArgs() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithMultipleConstructorArg.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		
		Argument listElement0 = new Argument(null, "foo", RefType.STRING);
		Argument listElement1 = new Argument(null, "bar", RefType.STRING);
		Argument list = new Argument(null, null, RefType.LIST);
		
		list.setCollectionType(CollectionType.LINKEDLIST);
		list.addChild(listElement0);
		list.addChild(listElement1);
		
		ref.addConstructorArgument(list);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithMultipleConstructorArg bean = null;
		
		List<Integer> ints = new LinkedList<Integer>();
		
		bean = container.getBean(beanName, ints);
		
		assertNotNull(bean);
		assertSame(ints, bean.getIntList());
		assertNotNull(bean.getStringList());
		
		assertEquals("foo", bean.getStringList().get(0));
		assertEquals("bar", bean.getStringList().get(1));
	}
	
	private void testContainerBuilderBeanWithListConstructorArgs(CollectionType collType, Class<?> collClass) throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithStringListConstructorArg.class.getName());
		ref.setName(beanName);
		
		Argument listElement0 = new Argument(null, "foo", RefType.STRING);
		Argument listElement1 = new Argument(null, "bar", RefType.STRING);
		Argument list = new Argument(null, null, RefType.LIST);
		
		list.setCollectionType(collType);
		
		list.addChild(listElement0);
		list.addChild(listElement1);
		
		ref.addConstructorArgument(list);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithStringListConstructorArg.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithStringListConstructorArg typedBean = container.getBean(beanName);
		
		List<String> stringList = typedBean.getStringList();
		
		assertNotNull(stringList);
		assertTrue(collClass.isAssignableFrom(stringList.getClass()));
		assertEquals(2, stringList.size());
		
		String string0 = stringList.get(0);
		String string1 = stringList.get(1);
		
		assertEquals("foo", string0);
		assertEquals("bar", string1);
	}
	
	public void testContainerBuilderBeanWithStringListConstructorArgs() throws Exception {
		
		testContainerBuilderBeanWithListConstructorArgs(null, LinkedList.class);
		
	}
	
	public void testContainerBuilderBeanWithArrayListConstructorArgs() throws Exception {
		testContainerBuilderBeanWithListConstructorArgs(CollectionType.ARRAYLIST, ArrayList.class);
	}
	
	public void testContainerBuilderBeanWithVectorConstructorArgs() throws Exception {
		testContainerBuilderBeanWithListConstructorArgs(CollectionType.VECTOR, Vector.class);
	}
	
	public void testContainerBuilderBeanWithStackConstructorArgs() throws Exception {
		testContainerBuilderBeanWithListConstructorArgs(CollectionType.STACK, Stack.class);
	}
	
	// Tests creating referenced beans and using in a constructor
	public void testContainerBuilderBeanWithBeanListConstructorArgs() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithDualListConstructorArg.class.getName());
		ref.setName(beanName);
		
		BeanRef refBean0 = new BeanRef();
		refBean0.setClassName(TestClassWithInitMethod.class.getName());
		refBean0.setName("refBean0");
		refBean0.setInitMethod(new MethodRef("doInit"));
		
		
		BeanRef refBean1 = new BeanRef();
		refBean1.setClassName(TestClassWithInitMethod.class.getName());
		refBean1.setName("refBean1");
		
		mapping.addBeanRef(refBean0);
		mapping.addBeanRef(refBean1);
		
		Argument listElement0 = new Argument(null, "refBean0", RefType.BEAN);
		Argument listElement1 = new Argument(null, "refBean1", RefType.BEAN);
		Argument list = new Argument(null, null, RefType.LIST);
		
		list.addChild(listElement0);
		list.addChild(listElement1);
		
		ref.addConstructorArgument(list);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithDualListConstructorArg.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithDualListConstructorArg typedBean = container.getBean(beanName);
		
		List<TestClassWithInitMethod> beanList = typedBean.getBeanList();
		
		assertNotNull(beanList);
		assertTrue(LinkedList.class.isAssignableFrom(beanList.getClass()));
		assertEquals(2, beanList.size());
		
		TestClassWithInitMethod bean0 = beanList.get(0);
		TestClassWithInitMethod bean1 = beanList.get(1);
		
		assertTrue(bean0.isInitialized());
		assertFalse(bean1.isInitialized());
	}
	
	// Tests creating referenced beans and using a SET in the constructor
	public void testContainerBuilderBeanWithHashSetConstructorArgs() throws Exception {
		testContainerBuilderBeanWithSetConstructorArgs(null, HashSet.class);
	}
	public void testContainerBuilderBeanWithTreeSetConstructorArgs() throws Exception {
		testContainerBuilderBeanWithSetConstructorArgs(CollectionType.TREESET, TreeSet.class);
	}
	
	private void testContainerBuilderBeanWithSetConstructorArgs(CollectionType collType, Class<?> collectionClass) throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithSetConstructorArg.class.getName());
		ref.setName(beanName);
		
		BeanRef refBean0 = new BeanRef();
		refBean0.setClassName(TestClassWithInitMethod.class.getName());
		refBean0.setName("refBean0");
		refBean0.setInitMethod(new MethodRef("doInit"));
		
		
		BeanRef refBean1 = new BeanRef();
		refBean1.setClassName(TestClassWithInitMethod.class.getName());
		refBean1.setName("refBean1");
		
		mapping.addBeanRef(refBean0);
		mapping.addBeanRef(refBean1);
		
		Argument listElement0 = new Argument(null, "refBean0", RefType.BEAN);
		Argument listElement1 = new Argument(null, "refBean1", RefType.BEAN);
		Argument list = new Argument(null, null, RefType.SET);
		
		list.addChild(listElement0);
		list.addChild(listElement1);
		
		list.setCollectionType(collType);
		
		ref.addConstructorArgument(list);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithSetConstructorArg.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithSetConstructorArg typedBean = container.getBean(beanName);
		
		Set<TestClassWithInitMethod> beanSet = typedBean.getSet();
		
		assertNotNull(beanSet);
		assertTrue(collectionClass.isAssignableFrom(beanSet.getClass()));
		assertEquals(2, beanSet.size());
		
		int inited = 0;
		int notInited = 0;
		
		for (TestClassWithInitMethod bean0 : beanSet) {
			if(bean0.isInitialized()) {
				inited++;
			}
			else {
				notInited++;
			}
		}
		
		assertEquals(1, inited);
		assertEquals(1, notInited);
	}
	
	public void testContainerBuilderBeanWithBeanDefaultMapConstructorArgs() throws Exception {
		testContainerBuilderBeanWithBeanMapConstructorArgs(null, HashMap.class);
	}
	
	public void testContainerBuilderBeanWithBeanTreeMapConstructorArgs() throws Exception {
		testContainerBuilderBeanWithBeanMapConstructorArgs(CollectionType.TREEMAP, TreeMap.class);
	}
	
	
	public void testContainerBuilderBeanWithBeanHashMapConstructorArgs() throws Exception {
		testContainerBuilderBeanWithBeanMapConstructorArgs(CollectionType.HASHMAP, HashMap.class);
	}
	
	private void testContainerBuilderBeanWithBeanMapConstructorArgs(CollectionType collType, Class<?> collectionClass) throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithDualMapConstructorArg.class.getName());
		ref.setName(beanName);
		
		BeanRef refBean0 = new BeanRef();
		refBean0.setClassName(TestClassWithInitMethod.class.getName());
		refBean0.setName("refBean0");
		refBean0.setInitMethod(new MethodRef("doInit"));

		mapping.addBeanRef(refBean0);
		
		Argument key = new Argument("key", "foo", RefType.STRING); // This is the key
		Argument value = new Argument(null, "refBean0", RefType.BEAN);
	
		Argument entryElement = new Argument(null, null, RefType.MAPENTRY);
		
		entryElement.addChild(key);
		entryElement.addChild(value);
		
		Argument mapElement = new Argument(null, null, RefType.MAP);
		
		mapElement.addChild(entryElement);
		mapElement.setCollectionType(collType);
		
		ref.addConstructorArgument(mapElement);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithDualMapConstructorArg.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithDualMapConstructorArg typedBean = container.getBean(beanName);
		
		Map<String, TestClassWithInitMethod> beanMap = typedBean.getBeanMap();
		
		assertNotNull(beanMap);
		assertTrue(collectionClass.isAssignableFrom(beanMap.getClass()));
		assertEquals(1, beanMap.size());
		
		TestClassWithInitMethod entry = beanMap.get("foo");
		
		assertNotNull(entry);
		
		assertTrue(entry.isInitialized());
	}
	
	public void testContainerBuilderBeanWithBeanMapProperty() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithMapProperty.class.getName());
		ref.setName(beanName);
		
		BeanRef refBean0 = new BeanRef();
		refBean0.setClassName(TestClassWithInitMethod.class.getName());
		refBean0.setName("refBean0");
		refBean0.setInitMethod(new MethodRef("doInit"));

		mapping.addBeanRef(refBean0);
		
		Argument key = new Argument("key", "foo", RefType.STRING); // This is the key
		Argument value = new Argument(null, "refBean0", RefType.BEAN);
	
		Argument entryElement = new Argument(null, null, RefType.MAPENTRY);
		
		entryElement.addChild(key);
		entryElement.addChild(value);
		
		Argument mapElement = new Argument("beanMap", null, RefType.MAP);
		
		mapElement.addChild(entryElement);
		
		ref.addProperty(mapElement);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithMapProperty.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithMapProperty typedBean = container.getBean(beanName);
		
		Map<String, TestClassWithInitMethod> beanMap = typedBean.getBeanMap();
		
		assertNotNull(beanMap);
		assertEquals(1, beanMap.size());
		
		TestClassWithInitMethod entry = beanMap.get("foo");
		
		assertNotNull(entry);
		
		assertTrue(entry.isInitialized());
	}
	
	
	public void testContainerBuilderBeanWithContextConstructorArgs() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithContextConstuctorArg.class.getName());
		ref.setName(beanName);
		ref.addConstructorArgument(new Argument(null, null, RefType.CONTEXT));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
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
		
		ContainerBuilder builder = new ContainerBuilder();
		
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
		
		ContainerBuilder builder = new ContainerBuilder();
		
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
		byte bt = 5;
		
		float fl = 61.5f;
		double db = 81.345345;
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithMultipleProperties.class.getName());
		ref.setName(beanName);
		ref.addProperty(new Argument("integer", String.valueOf(i), RefType.INTEGER));
		ref.addProperty(new Argument("lng", String.valueOf(l), RefType.LONG));
		ref.addProperty(new Argument("shrt", String.valueOf(s), RefType.SHORT));
		ref.addProperty(new Argument("chr", String.valueOf(c), RefType.CHAR));
		ref.addProperty(new Argument("bool", String.valueOf(b), RefType.BOOLEAN));
		ref.addProperty(new Argument("bte", String.valueOf(bt), RefType.BYTE));
		ref.addProperty(new Argument("dbl", String.valueOf(db), RefType.DOUBLE));
		ref.addProperty(new Argument("flt", String.valueOf(fl), RefType.FLOAT));
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
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
		assertEquals(bt, multi.getBte());
		assertEquals(fl, multi.getFlt());	
		assertEquals(db, multi.getDbl());	
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
		
		ContainerBuilder builder = new ContainerBuilder();
		
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
		
		ContainerBuilder builder = new ContainerBuilder();
		
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
		
		ContainerBuilder builder = new ContainerBuilder();
		
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
	
	public void testContainerBuilderSingletonBean()  throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(true);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object beanA = container.getBean(beanName);
		Object beanB = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanA.getClass()));
		
		assertNotNull(beanB);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanB.getClass()));
		
		assertTrue(beanA == beanB);
	}
	
	public void testContainerBuilderNonSingletonBean() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object beanA = container.getBean(beanName);
		Object beanB = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanA.getClass()));
		
		assertNotNull(beanB);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanB.getClass()));
		
		assertFalse(beanA == beanB);
	}
	
	public void testPropertySetCounts() throws Exception {
		
		String propertyBeanName = "propertyBean";
		String nonSingletonBeanName = "nonSingletonBean";
		String singletonBeanName = "singletonBeanName";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef propertyBeanRef = new BeanRef();
		propertyBeanRef.setClassName(TestClassWithConstructorCounter.class.getName());
		propertyBeanRef.setName(propertyBeanName);
		
		BeanRef singletonBeanRef = new BeanRef();
		singletonBeanRef.setClassName(TestClassWithBeanProperty.class.getName());
		singletonBeanRef.setName(singletonBeanName);
		singletonBeanRef.addProperty(new Argument("bean", propertyBeanName, RefType.BEAN));
		
		BeanRef nonSingletonBeanRef = new BeanRef();
		nonSingletonBeanRef.setClassName(TestClassWithBeanProperty.class.getName());
		nonSingletonBeanRef.setName(nonSingletonBeanName);
		nonSingletonBeanRef.setSingleton(false);
		nonSingletonBeanRef.addProperty(new Argument("bean", propertyBeanName, RefType.BEAN));

		mapping.addBeanRef(propertyBeanRef);
		mapping.addBeanRef(nonSingletonBeanRef);
		mapping.addBeanRef(singletonBeanRef);
				
		ContainerBuilder builder = new ContainerBuilder();
		Container container = builder.build(getContext(), mapping);
		
		// Get the singleton
		TestClassWithBeanProperty sing0 = container.getBean(singletonBeanName);
		TestClassWithBeanProperty bean0 = container.getBean(nonSingletonBeanName);
		
		assertFalse(sing0 == bean0);
		assertNotNull(sing0.getBean());
		assertNotNull(bean0.getBean());
		assertTrue(sing0.getBean() == bean0.getBean());
		
		assertEquals(1, sing0.PROPERTY_SET_CALLS);
		assertEquals(1, bean0.PROPERTY_SET_CALLS);
		
		// Now get again
		TestClassWithBeanProperty sing1 = container.getBean(singletonBeanName);
		TestClassWithBeanProperty bean1 = container.getBean(nonSingletonBeanName);
		
		assertFalse(sing1 == bean1);
		assertFalse(bean0 == bean1);
		
		assertTrue(sing0 == sing1);
		
		assertNotNull(sing1.getBean());
		assertNotNull(bean1.getBean());
		assertTrue(sing0.getBean() == sing1.getBean());		
		assertTrue(sing0.getBean() == bean1.getBean());		
		assertTrue(sing1.getBean() == bean1.getBean());
		assertTrue(bean0.getBean() == bean1.getBean());	
		
		assertEquals(1, sing1.PROPERTY_SET_CALLS);
		assertEquals(1, bean1.PROPERTY_SET_CALLS);
	}
	
	public void testContainerBuilderLazyBean() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithConstructorCounter.class.getName());
		ref.setName(beanName);
		ref.setLazy(true);
		mapping.addBeanRef(ref);
		
		TestClassWithConstructorCounter.CONSTRUCTOR_COUNT = 0;
		
		ContainerBuilder builder = new ContainerBuilder();
		Container container = builder.build(getContext(), mapping);
		
		Object beanA = container.getBean(beanName);
		Object beanB = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertNotNull(beanB);
		assertTrue(beanA == beanB);
		assertEquals(1, TestClassWithConstructorCounter.CONSTRUCTOR_COUNT);
	}
	
	public void testContainerBuilderWithLazyBeanProperty() throws Exception {
		String lazyName = "lazyBean";
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithConstructorCounter.class.getName());
		ref.setName(lazyName);
		ref.setLazy(true);
		
		BeanRef bean = new BeanRef();
		bean.setClassName(TestClassWithBeanProperty.class.getName());
		bean.setName(beanName);
		bean.addProperty(new Argument("bean", lazyName, RefType.BEAN));
				
		mapping.addBeanRef(ref);
		mapping.addBeanRef(bean);
		
		TestClassWithConstructorCounter.CONSTRUCTOR_COUNT = 0;
		
		ContainerBuilder builder = new ContainerBuilder();
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithBeanProperty beanA = container.getBean(beanName);
		TestClassWithConstructorCounter beanB = beanA.getBean();
		TestClassWithConstructorCounter beanC = container.getBean(lazyName);
		
		assertNotNull(beanA);
		assertNotNull(beanB);
		assertNotNull(beanC);
		assertTrue(beanB == beanC);
		
		assertEquals(1, TestClassWithConstructorCounter.CONSTRUCTOR_COUNT);
	}	
	

	public void testContainerBuilderWithLazyBeanMakerProperty() throws Exception {
		String lazyName = "lazyBean";
		String makerName = "makerBean";
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithConstructorCounter.class.getName());
		ref.setName(lazyName);
		ref.setLazy(true);
		
		BeanRef maker = new BeanRef();
		maker.setClassName("com.socialize.android.ioc.sample.TestBeanMaker");
		maker.setName(makerName);
		maker.addProperty(new Argument("beanName", lazyName, RefType.STRING));		
		
		BeanRef bean = new BeanRef();
		bean.setClassName(TestClassWithBeanProperty.class.getName());
		bean.setName(beanName);
		bean.addProperty(new Argument("bean", makerName, RefType.BEAN));
				
		mapping.addBeanRef(ref);
		mapping.addBeanRef(bean);
		mapping.addBeanRef(maker);
		
		TestClassWithConstructorCounter.CONSTRUCTOR_COUNT = 0;
		
		ContainerBuilder builder = new ContainerBuilder();
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithBeanProperty beanA = container.getBean(beanName);
		TestClassWithConstructorCounter beanB = beanA.getBean();
		TestClassWithConstructorCounter beanC = container.getBean(lazyName);
		
		assertNotNull(beanA);
		assertNotNull(beanB);
		assertNotNull(beanC);
		assertTrue(beanB == beanC);
		
		assertEquals(1, TestClassWithConstructorCounter.CONSTRUCTOR_COUNT);
	}		
	
	public void testContainerBuilderFactoryBean() throws Exception {
		String beanName = "bean";
		String factoryName = "factory";
		
		BeanMapping mapping = new BeanMapping();
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		mapping.addBeanRef(ref);
		
		FactoryRef fRef = new FactoryRef();
		fRef.setName(factoryName);
		fRef.setMakes(beanName);
		
		mapping.addFactoryRef(fRef);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object beanA = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanA.getClass()));
		
		IBeanFactory<Object> factory = container.getBean(factoryName);
		
		assertNotNull(factory);
		
		Object beanB = factory.getBean();
		
		assertNotNull(beanA);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanB.getClass()));
		
		assertFalse(beanA == beanB);
	}
	
	public void testContainerBuilderFactoryBeanProperty() throws Exception {
		String beanName = "bean";
		String factoryName = "factory";
		
		BeanMapping mapping = new BeanMapping();
		
		FactoryRef fRef = new FactoryRef();
		fRef.setName(factoryName);
		fRef.setMakes(beanName);
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithBeanFactoryParam.class.getName());
		ref.setName(beanName);
		ref.addProperty(new Argument("factory", "factory", RefType.BEAN));
		
		mapping.addBeanRef(ref);
		mapping.addFactoryRef(fRef);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithBeanFactoryParam beanA = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertNotNull(beanA.getFactory());
		assertTrue(IBeanFactory.class.isAssignableFrom(beanA.getFactory().getClass()));

	}
	
	public void testContainerBuilderFactoryBeanConstructorArg() throws Exception {
		String beanName = "bean";
		String factoryName = "factory";
		
		BeanMapping mapping = new BeanMapping();
		
		FactoryRef fRef = new FactoryRef();
		fRef.setName(factoryName);
		fRef.setMakes(beanName);
		
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithBeanFactoryParam.class.getName());
		ref.setName(beanName);
		ref.addConstructorArgument(new Argument("factory", "factory", RefType.BEAN));
		
		mapping.addBeanRef(ref);
		mapping.addFactoryRef(fRef);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithBeanFactoryParam beanA = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertNotNull(beanA.getFactory());
		assertTrue(IBeanFactory.class.isAssignableFrom(beanA.getFactory().getClass()));

	}
	
	
	
	public void testContainerBuilderAbstractBean() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setAbstractBean(true);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		Object beanA = container.getBean(beanName);
		
		assertNull(beanA);
	}
	
	public void testContainerBuilderSingletonBeanInitMethod() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(true);
		ref.setInitMethod(new MethodRef("doInit"));
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithInitMethod beanA = container.getBean(beanName);
		
		assertTrue(beanA.isInitialized());
		
	}
	
	public void testContainerBuilderBeanDestroyMethod() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitAndDestroy.class.getName());
		ref.setName(beanName);
		ref.setDestroyMethod(new MethodRef("destroy"));
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithInitAndDestroy beanA = container.getBean(beanName);
		
		container.destroy();
		
		assertTrue(beanA.isDestroy());
		
	}
	
	public void testContainerBuilderNonSingletonBeanInitMethod() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		ref.setInitMethod(new MethodRef("doInit"));
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithInitMethod beanA = container.getBean(beanName);
		
		assertTrue(beanA.isInitialized());
		
	}
	
	public void testContainerBuilderBeanInitMethodWithArgs() throws Exception {
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
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithInitMethodTakingBean beanA = container.getBean(beanName2);
		
		assertNotNull(beanA.getContext());
		assertNotNull(beanA.getObject());
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanA.getObject().getClass()));
	}
	
	public void testContainerBuilderBeanInitMethodWithArgsReversed() throws Exception {
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
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithInitMethodTakingBean beanA = container.getBean(beanName2);
		
		assertNotNull(beanA.getContext());
		assertNotNull(beanA.getObject());
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanA.getObject().getClass()));
	}
	
	@UsesMocks ({BeanMappingParser.class})
	public void testContainerBuilderBuildWithFilename() throws Exception {
		final Set<String> set = new HashSet<String>();
		
		BeanMappingParser parser = AndroidMock.createMock(BeanMappingParser.class);
		
		String filename = "foobar";
		
		AndroidMock.expect(parser.parse(getContext(), filename)).andReturn(null);
		AndroidMock.replay(parser);
		
		ContainerBuilder builder = new ContainerBuilder(parser) {
			@Override
			public Container build(Context context, BeanMapping mapping) {
				set.add("ContainerBuilt");
				return null;
			}
		};
		
		builder.build(getContext(), filename);
		
		assertEquals(1, set.size());
		assertEquals("ContainerBuilt", set.iterator().next());
		
		AndroidMock.verify(parser);
		
	}
	
	public void testImportBean() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"26-import-bean.xml");
		
		assertNotNull(mapping);
		
		ContainerBuilder builder = new ContainerBuilder(parser);
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean("bean2");
		Object bean2 = container.getBean("bean26");
		
		assertNotNull(bean);
		assertNotNull(bean2);
	}	
	
	public void testImportMappingDependent() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"27-import-mapping-dependent.xml");
		
		assertNotNull(mapping);
		
		ContainerBuilder builder = new ContainerBuilder(parser);
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean("bean2");
		Object bean2 = container.getBean("bean27");
		Object bean4 = container.getBean("bean4");
		
		assertNotNull(bean);
		assertNotNull(bean2);
		assertNull(bean4);
	}	
	
	public void testImportMappingDependentMultipleImports() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"29-import-mapping-dependent-multiple.xml");
		
		assertNotNull(mapping);
		
		ContainerBuilder builder = new ContainerBuilder(parser);
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean("bean2");
		Object bean2 = container.getBean("bean29");
		Object bean4 = container.getBean("bean4");
		
		assertNotNull(bean);
		assertNotNull(bean2);
		assertNull(bean4);
	}	
		
	public void testImportMappingAll() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"28-import-mapping-all.xml");
		
		assertNotNull(mapping);
		
		ContainerBuilder builder = new ContainerBuilder(parser);
		
		Container container = builder.build(getContext(), mapping);
		
		Object bean = container.getBean("bean2");
		Object bean2 = container.getBean("bean28");
		Object bean4 = container.getBean("bean4");
		
		assertNotNull(bean);
		assertNotNull(bean2);
		assertNotNull(bean4);
	}	

	public void testInitMethodDependency() throws Exception {
		
		String beanName = "initWithCount";
		String beanName2 = "initWithCount2";
		
		BeanMapping mapping = new BeanMapping();
		BeanRefWithMethodCount ref = new BeanRefWithMethodCount();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setInitMethod(new MethodRef("doInit"));
		
		BeanRefWithMethodCount ref2 = new BeanRefWithMethodCount();
		ref2.setClassName(TestClassWithInitMethod.class.getName());
		ref2.setName(beanName2);
		
		MethodRef initMethod = new MethodRef("doInitDepends");
		initMethod.addArgument(new Argument(null, beanName, RefType.BEAN));
		
		ref2.setInitMethod(initMethod);
		
		mapping.addBeanRef(ref2);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder();
		
		Container container = builder.build(getContext(), mapping);
		
		TestClassWithInitMethod beanA = container.getBean(beanName);
		TestClassWithInitMethod beanB = container.getBean(beanName2);
		
		assertTrue(beanA.isInitialized());
		assertTrue(beanB.isInitialized());
		
		assertNotNull(beanB.getOther());
		assertSame(beanA, beanB.getOther());
	}
	
	public class BeanRefWithMethodCount extends BeanRef {
		public int callCount = 0;
		@Override
		public MethodRef getInitMethod() {
			callCount++;
			return super.getInitMethod();
		}
	}
}
