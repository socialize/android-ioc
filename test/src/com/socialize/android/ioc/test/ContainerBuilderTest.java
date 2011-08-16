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
package com.socialize.android.ioc.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import com.google.android.testing.mocking.AndroidMock;
import com.google.android.testing.mocking.UsesMocks;
import com.socialize.android.ioc.Argument;
import com.socialize.android.ioc.Argument.CollectionType;
import com.socialize.android.ioc.Argument.RefType;
import com.socialize.android.ioc.BeanMapping;
import com.socialize.android.ioc.BeanMappingParser;
import com.socialize.android.ioc.BeanMappingParserHandler;
import com.socialize.android.ioc.BeanRef;
import com.socialize.android.ioc.Container;
import com.socialize.android.ioc.ContainerBuilder;
import com.socialize.android.ioc.FactoryRef;
import com.socialize.android.ioc.IBeanFactory;
import com.socialize.android.ioc.MethodRef;
import com.socialize.android.ioc.ParserHandlerFactory;
import com.socialize.android.ioc.sample.SubClassOfTestClassWithInitMethod;
import com.socialize.android.ioc.sample.TestClassContainerAware;
import com.socialize.android.ioc.sample.TestClassWithBeanConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithBeanFactoryParam;
import com.socialize.android.ioc.sample.TestClassWithContextConstuctorArg;
import com.socialize.android.ioc.sample.TestClassWithDualListConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithDualMapConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithInitAndDestroy;
import com.socialize.android.ioc.sample.TestClassWithInitMethod;
import com.socialize.android.ioc.sample.TestClassWithInitMethodTakingBean;
import com.socialize.android.ioc.sample.TestClassWithIntConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithMapProperty;
import com.socialize.android.ioc.sample.TestClassWithMultipleConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithMultipleProperties;
import com.socialize.android.ioc.sample.TestClassWithSetConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithStringListConstructorArg;


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
		
		ContainerBuilder builder = new ContainerBuilder(context, parser) {
			@Override
			public Container build(BeanMapping mapping) {
				// Don't want this to be tested in this test.
				// We will verify the actual build process in a more detailed integration test.
				return null;
			}
		};
		
		builder.build(streams);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(bean.getClass()));
	}
	
	public void testContainerAware() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassContainerAware.class.getName());
		ref.setName(beanName);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassContainerAware.class.isAssignableFrom(bean.getClass()));
		
		TestClassContainerAware aware = (TestClassContainerAware) bean;
		
		assertNotNull(aware.getContainer());
		assertSame(container, aware.getContainer());
	}
	
	public void testContainerSize() {
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
		Object bean = container.getBean(beanName);
		
		assertNotNull(bean);
		assertTrue(TestClassWithIntConstructorArg.class.isAssignableFrom(bean.getClass()));
		
		TestClassWithIntConstructorArg bc2 = container.getBean(beanName);
		
		assertEquals(69, bc2.getParam());
	}
	
	public void testExtraConstructorArgsInt() throws Exception {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithMultipleConstructorArg.class.getName());
		ref.setName(beanName);
		ref.setSingleton(false);
		
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		Container container = builder.build(mapping);
		
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
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
		
		Container container = builder.build(mapping);
		
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
		
		Container container = builder.build(mapping);
		
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
		
		Container container = builder.build(mapping);
		
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
		
		Container container = builder.build(mapping);
		
		Object beanA = container.getBean(beanName);
		Object beanB = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanA.getClass()));
		
		assertNotNull(beanB);
		assertTrue(TestClassWithInitMethod.class.isAssignableFrom(beanB.getClass()));
		
		assertFalse(beanA == beanB);
	}
	
	public void testContainerBuilderFactoryBean() {
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
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
	
	public void testContainerBuilderFactoryBeanProperty() {
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
		TestClassWithBeanFactoryParam beanA = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertNotNull(beanA.getFactory());
		assertTrue(IBeanFactory.class.isAssignableFrom(beanA.getFactory().getClass()));

	}
	
	public void testContainerBuilderFactoryBeanConstructorArg() {
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
		TestClassWithBeanFactoryParam beanA = container.getBean(beanName);
		
		assertNotNull(beanA);
		assertNotNull(beanA.getFactory());
		assertTrue(IBeanFactory.class.isAssignableFrom(beanA.getFactory().getClass()));

	}
	
	
	
	public void testContainerBuilderAbstractBean() {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitMethod.class.getName());
		ref.setName(beanName);
		ref.setAbstractBean(true);
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
		Object beanA = container.getBean(beanName);
		
		assertNull(beanA);
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
		
		Container container = builder.build(mapping);
		
		TestClassWithInitMethod beanA = container.getBean(beanName);
		
		assertTrue(beanA.isInitialized());
		
	}
	
	public void testContainerBuilderBeanDestroyMethod() {
		String beanName = "bean";
		
		BeanMapping mapping = new BeanMapping();
		BeanRef ref = new BeanRef();
		ref.setClassName(TestClassWithInitAndDestroy.class.getName());
		ref.setName(beanName);
		ref.setDestroyMethod(new MethodRef("destroy"));
		mapping.addBeanRef(ref);
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
		TestClassWithInitAndDestroy beanA = container.getBean(beanName);
		
		container.destroy();
		
		assertTrue(beanA.isDestroy());
		
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
		
		Container container = builder.build(mapping);
		
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
		
		Container container = builder.build(mapping);
		
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
		
		Container container = builder.build(mapping);
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext(), parser) {
			@Override
			public Container build(BeanMapping mapping) {
				set.add("ContainerBuilt");
				return null;
			}
		};
		
		builder.build(filename);
		
		assertEquals(1, set.size());
		assertEquals("ContainerBuilt", set.iterator().next());
		
		AndroidMock.verify(parser);
		
	}
	

	public void testInitMethodDependency() {
		
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
		
		ContainerBuilder builder = new ContainerBuilder(getContext());
		
		Container container = builder.build(mapping);
		
		TestClassWithInitMethod beanA = container.getBean(beanName);
		TestClassWithInitMethod beanB = container.getBean(beanName2);
		
		assertTrue(beanA.isInitialized());
		assertTrue(beanB.isInitialized());
		
		assertNotNull(beanB.getOther());
		assertSame(beanA, beanB.getOther());
	}
	
	
	private class BeanRefWithMethodCount extends BeanRef {

		int callCount = 0;
		@Override
		public MethodRef getInitMethod() {
			callCount++;
			return super.getInitMethod();
		}
	}
}
