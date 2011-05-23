package com.socialize.android.ioc.test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.test.AndroidTestCase;

import com.socialize.android.ioc.Argument;
import com.socialize.android.ioc.Argument.CollectionType;
import com.socialize.android.ioc.Argument.RefType;
import com.socialize.android.ioc.BeanMapping;
import com.socialize.android.ioc.BeanMappingParser;
import com.socialize.android.ioc.BeanRef;
import com.socialize.android.ioc.MethodRef;
import com.socialize.android.ioc.sample.SubClassOfTestClassWithInitMethod;
import com.socialize.android.ioc.sample.TestClassWithBeanConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithContextConstuctorArg;
import com.socialize.android.ioc.sample.TestClassWithInitAndDestroy;
import com.socialize.android.ioc.sample.TestClassWithInitMethod;
import com.socialize.android.ioc.sample.TestClassWithInitMethodTakingBean;
import com.socialize.android.ioc.sample.TestClassWithIntConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithListConstructorArg;
import com.socialize.android.ioc.sample.TestClassWithListInit;
import com.socialize.android.ioc.sample.TestClassWithListParam;
import com.socialize.android.ioc.sample.TestClassWithMultipleProperties;

public class BeanMapperParserTest extends AndroidTestCase {
	
	public void testSimpleBean() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"0-simple-bean.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean0");
		assertNotNull(ref);
		assertEquals(TestClassWithInitMethod.class.getName(), ref.getClassName());
	}
	
	public void testPrimitiveConstructor() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"1-primitive-constructor-arg.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean1");
		assertNotNull(ref);
		assertEquals(TestClassWithIntConstructorArg.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getInitMethod());
		assertNull(ref.getProperties());
		
		List<Argument> constructorArgs = ref.getConstructorArgs();
		assertNotNull(constructorArgs);
		assertEquals(1, constructorArgs.size());
		
		Argument argument = constructorArgs.get(0);
		
		assertEquals(RefType.INTEGER, argument.getType());
		assertEquals("22", argument.getValue());
		assertNull(argument.getKey());
		assertNull(argument.getChildren());
		assertNull(argument.getCollectionType());
	}
	
	public void testStringConstructor() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"2-string-constructor-arg.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean2");
		assertNotNull(ref);
		assertEquals(SubClassOfTestClassWithInitMethod.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getInitMethod());
		assertNull(ref.getProperties());
		
		List<Argument> constructorArgs = ref.getConstructorArgs();
		assertNotNull(constructorArgs);
		assertEquals(1, constructorArgs.size());
		
		Argument argument = constructorArgs.get(0);
		
		assertEquals(RefType.STRING, argument.getType());
		assertEquals("foobar", argument.getValue());
		assertNull(argument.getKey());
		assertNull(argument.getChildren());
		assertNull(argument.getCollectionType());
	}
	
	public void testStringProperty() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"3-string-property.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean3");
		assertNotNull(ref);
		assertEquals(SubClassOfTestClassWithInitMethod.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getInitMethod());
		assertNull(ref.getConstructorArgs());
		
		List<Argument> props = ref.getProperties();
		assertNotNull(props);
		assertEquals(1, props.size());
		
		Argument argument = props.get(0);
		
		assertEquals(RefType.STRING, argument.getType());
		assertEquals("foobar", argument.getValue());
		assertEquals("param", argument.getKey());
		assertNull(argument.getChildren());
		assertNull(argument.getCollectionType());
	}
	
	public void testBeanConstructor() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"4-bean-constructor-arg.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean4");
		assertNotNull(ref);
		assertEquals(TestClassWithBeanConstructorArg.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getInitMethod());
		assertNull(ref.getProperties());
		
		List<Argument> constructorArgs = ref.getConstructorArgs();
		assertNotNull(constructorArgs);
		assertEquals(1, constructorArgs.size());
		
		Argument argument = constructorArgs.get(0);
		
		assertEquals(RefType.BEAN, argument.getType());
		assertEquals("bean2", argument.getValue());
		assertNull(argument.getKey());
		assertNull(argument.getChildren());
		assertNull(argument.getCollectionType());
	}
	
	public void testContextConstructor() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"5-context-constructor-arg.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean5");
		assertNotNull(ref);
		assertEquals(TestClassWithContextConstuctorArg.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getInitMethod());
		assertNull(ref.getProperties());
		
		List<Argument> constructorArgs = ref.getConstructorArgs();
		assertNotNull(constructorArgs);
		assertEquals(1, constructorArgs.size());
		
		Argument argument = constructorArgs.get(0);
		
		assertEquals(RefType.CONTEXT, argument.getType());
		assertNull(argument.getValue());
		assertNull(argument.getKey());
		assertNull(argument.getChildren());
		assertNull(argument.getCollectionType());
	}
	
	//6-context-property.xml
	public void testContextProperty() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"6-context-property.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean6");
		assertNotNull(ref);
		assertEquals(TestClassWithContextConstuctorArg.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getInitMethod());
		assertNull(ref.getConstructorArgs());
		
		List<Argument> props = ref.getProperties();
		assertNotNull(props);
		assertEquals(1, props.size());
		
		Argument argument = props.get(0);
		
		assertEquals(RefType.CONTEXT, argument.getType());
		assertEquals("context", argument.getKey());
		assertNull(argument.getValue());
		assertNull(argument.getChildren());
		assertNull(argument.getCollectionType());
	}
	
	// 7-multiple-properties.xml
	public void testMultiProperty() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"7-multiple-properties.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean7");
		assertNotNull(ref);
		assertEquals(TestClassWithMultipleProperties.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getInitMethod());
		assertNull(ref.getConstructorArgs());
		
		List<Argument> props = ref.getProperties();
		assertNotNull(props);
		assertEquals(7, props.size());
		
//		<property name="string" value="foobar" type="string"/>
		Argument argument0 = props.get(0);
		assertEquals(RefType.STRING, argument0.getType());
		assertEquals("string", argument0.getKey());
		assertEquals("foobar", argument0.getValue());
		assertNull(argument0.getChildren());
		assertNull(argument0.getCollectionType());
		
//		<property name="integer" value="22" type="integer"/>
		Argument argument1 = props.get(1);
		assertEquals(RefType.INTEGER, argument1.getType());
		assertEquals("integer", argument1.getKey());
		assertEquals("22", argument1.getValue());
		assertNull(argument1.getChildren());
		assertNull(argument1.getCollectionType());
		
//		<property name="lng" value="333333" type="long"/>
		Argument argument2 = props.get(2);
		assertEquals(RefType.LONG, argument2.getType());
		assertEquals("lng", argument2.getKey());
		assertEquals("333333", argument2.getValue());
		assertNull(argument2.getChildren());
		assertNull(argument2.getCollectionType());
		
//		<property name="shrt" value="4" type="short"/>
		Argument argument3 = props.get(3);
		assertEquals(RefType.SHORT, argument3.getType());
		assertEquals("shrt", argument3.getKey());
		assertEquals("4", argument3.getValue());
		assertNull(argument3.getChildren());
		assertNull(argument3.getCollectionType());
		
//		<property name="chr" value="d" type="char"/>
		Argument argument4 = props.get(4);
		assertEquals(RefType.CHAR, argument4.getType());
		assertEquals("chr", argument4.getKey());
		assertEquals("d", argument4.getValue());
		assertNull(argument4.getChildren());
		assertNull(argument4.getCollectionType());
		
//		<property name="bool" value="true" type="boolean"/>
		Argument argument5 = props.get(5);
		assertEquals(RefType.BOOLEAN, argument5.getType());
		assertEquals("bool", argument5.getKey());
		assertEquals("true", argument5.getValue());
		assertNull(argument5.getChildren());
		assertNull(argument5.getCollectionType());
		
// 		<property name="btw" value="12" type="byte"/>
		Argument argument6 = props.get(6);
		assertEquals(RefType.BYTE, argument6.getType());
		assertEquals("btw", argument6.getKey());
		assertEquals("12", argument6.getValue());
		assertNull(argument6.getChildren());
		assertNull(argument6.getCollectionType());
	}
	
	// 8-simple-init-method.xml
	public void testSimpleInit() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"8-simple-init-method.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean8");
		assertNotNull(ref);
		assertEquals(TestClassWithInitMethod.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getConstructorArgs());
		assertNull(ref.getProperties());
		
		assertNotNull(ref.getInitMethod());
		assertNotNull(ref.getInitMethod().getName());
		assertEquals("doInit", ref.getInitMethod().getName());
		assertNull(ref.getInitMethod().getArguments());
	}
	
	// 9-init-method-with-args.xml
	public void testInitWithArgs() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"9-init-method-with-args.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean9");
		assertNotNull(ref);
		assertEquals(TestClassWithInitMethodTakingBean.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getConstructorArgs());
		assertNull(ref.getProperties());
		
		assertNotNull(ref.getInitMethod());
		assertNotNull(ref.getInitMethod().getName());
		assertEquals("init", ref.getInitMethod().getName());
		
		List<Argument> arguments = ref.getInitMethod().getArguments();
		
		assertNotNull(arguments);
		assertEquals(2, arguments.size());
		
		Argument argument0 = arguments.get(0);
		Argument argument1 = arguments.get(1);
		
		assertEquals(RefType.CONTEXT, argument0.getType());
		assertNull(argument0.getKey());
		assertNull(argument0.getValue());
		assertNull(argument0.getChildren());
		assertNull(argument0.getCollectionType());
		
		assertEquals(RefType.BEAN, argument1.getType());
		assertNull(argument1.getKey());
		assertEquals("bean0", argument1.getValue());
		assertNull(argument1.getChildren());
		assertNull(argument1.getCollectionType());
	}
	
	// 10-init-and-destroy.xml
	public void setInitAndDestroy() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"10-init-and-destroy.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean10");
		assertNotNull(ref);
		assertEquals(TestClassWithInitAndDestroy.class.getName(), ref.getClassName());
		
		assertNull(ref.getConstructorArgs());
		assertNull(ref.getProperties());
		
		assertNotNull(ref.getInitMethod());
		assertNotNull(ref.getInitMethod().getName());
		assertEquals("init", ref.getInitMethod().getName());
		assertNull(ref.getInitMethod().getArguments());
		
		assertNotNull(ref.getDestroyMethod());
		assertNotNull(ref.getDestroyMethod().getName());
		assertEquals("destroy", ref.getDestroyMethod().getName());
		assertNull(ref.getDestroyMethod().getArguments());
	}
	
	// 11-list-constructor-arg.xml
	public void testListConstructor() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"11-list-constructor-arg.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean11");
		assertNotNull(ref);
		assertEquals(TestClassWithListConstructorArg.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getInitMethod());
		assertNull(ref.getProperties());
		
		List<Argument> constructorArgs = ref.getConstructorArgs();
		assertNotNull(constructorArgs);
		assertEquals(1, constructorArgs.size());
		
		Argument argument = constructorArgs.get(0);
		
		assertEquals(RefType.LIST, argument.getType());
		assertNull(argument.getValue());
		assertNull(argument.getKey());
		
		assertNotNull(argument.getChildren());
		assertNotNull(argument.getCollectionType());
		
		assertEquals(2, argument.getChildren().size());
		assertEquals(CollectionType.LINKEDLIST, argument.getCollectionType());
		
		
		Argument child0 = argument.getChildren().get(0);
		Argument child1 = argument.getChildren().get(1);
		
		assertEquals(RefType.BEAN, child0.getType());
		assertEquals("bean8", child0.getValue());
	
		
		assertEquals(RefType.BEAN, child1.getType());
		assertEquals("bean9", child1.getValue());
	}
	
	// 12-list-property.xml
	public void testListProperty() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"12-list-property.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean12");
		assertNotNull(ref);
		assertEquals(TestClassWithListParam.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getInitMethod());
		assertNull(ref.getConstructorArgs());
		
		List<Argument> props = ref.getProperties();
		assertNotNull(props);
		assertEquals(1, props.size());
		
		Argument argument = props.get(0);
		
		assertEquals(RefType.LIST, argument.getType());
		assertNotNull(argument.getKey());
		assertEquals("list", argument.getKey());
		assertNull(argument.getValue());
		
		assertNotNull(argument.getChildren());
		assertNotNull(argument.getCollectionType());
		
		assertEquals(2, argument.getChildren().size());
		assertEquals(CollectionType.LINKEDLIST, argument.getCollectionType());
		
		
		Argument child0 = argument.getChildren().get(0);
		Argument child1 = argument.getChildren().get(1);
		
		assertEquals(RefType.BEAN, child0.getType());
		assertEquals("bean8", child0.getValue());
	
		
		assertEquals(RefType.BEAN, child1.getType());
		assertEquals("bean9", child1.getValue());
	}
	
	// 13-init-method-with-list-arg.xml
	public void testInitMethodWithListArg() throws Exception {
		BeanMappingParser parser = new BeanMappingParser();
		BeanMapping mapping = parser.parse(getContext(),"13-init-method-with-list-arg.xml");
		assertNotNull(mapping);
		BeanRef ref = mapping.getBeanRef("bean13");
		assertNotNull(ref);
		assertEquals(TestClassWithListInit.class.getName(), ref.getClassName());
		
		assertNull(ref.getDestroyMethod());
		assertNull(ref.getProperties());
		assertNull(ref.getConstructorArgs());
		
		MethodRef initMethod = ref.getInitMethod();
		
		assertNotNull(initMethod);
		
		List<Argument> props = initMethod.getArguments();
		assertNotNull(props);
		assertEquals(1, props.size());
		
		Argument argument = props.get(0);
		
		assertEquals(RefType.LIST, argument.getType());
		assertNull(argument.getKey());
		assertNull(argument.getValue());
		
		assertNotNull(argument.getChildren());
		assertNotNull(argument.getCollectionType());
		
		assertEquals(2, argument.getChildren().size());
		assertEquals(CollectionType.LINKEDLIST, argument.getCollectionType());
		
		
		Argument child0 = argument.getChildren().get(0);
		Argument child1 = argument.getChildren().get(1);
		
		assertEquals(RefType.BEAN, child0.getType());
		assertEquals("bean8", child0.getValue());
	
		
		assertEquals(RefType.BEAN, child1.getType());
		assertEquals("bean9", child1.getValue());
	}
	
	public void testBeanMapperParser() throws IOException {
		
		BeanMappingParser parser = new BeanMappingParser();
		
		BeanMapping mapping = parser.parse(getContext());
		
		assertNotNull(mapping);
		
		Collection<BeanRef> beanRefs = mapping.getBeanRefs();
		
		assertNotNull(beanRefs);
		assertEquals(14, beanRefs.size());
		
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
		names.add("bean8");
		names.add("bean9");
		names.add("bean10");
		names.add("bean11");
		names.add("bean12");
		names.add("bean13");
		
		for (BeanRef beanRef : beanRefs) {
			assertTrue(names.contains(beanRef.getName()));
			
			// Remove otherwise multiple names of the same will pass the test
			names.remove(beanRef.getName());
		}
	}
	
}
