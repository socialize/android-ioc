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

import java.util.Collection;
import java.util.List;

import android.test.AndroidTestCase;

import com.socialize.android.ioc.Argument;
import com.socialize.android.ioc.BeanMapping;
import com.socialize.android.ioc.BeanRef;
import com.socialize.android.ioc.MethodRef;
import com.socialize.android.ioc.Argument.RefType;

public class BeanMappingTest extends AndroidTestCase {
	
	public void testBeanMappingMerge() {
		BeanRef ref0 = new BeanRef();
		MethodRef ref0Init = new MethodRef("ref0Init");
		MethodRef ref0Destroy = new MethodRef("ref0Destroy");
		Argument ref0Arg0 = new Argument("ref0Arg0", "ref0Arg0Val", RefType.STRING);
		
		ref0.setName("ref0");
		ref0.setClassName("ref0ClassName");
		ref0.setAbstractBean(true);
		ref0.setDestroyMethod(ref0Destroy);
		ref0.setInitMethod(ref0Init);
		ref0.addProperty(ref0Arg0);
		
		BeanRef ref1 = new BeanRef();
		MethodRef ref1Init = new MethodRef("ref1Init");
		MethodRef ref1Destroy = new MethodRef("ref1Destroy");
		Argument ref1Arg1 = new Argument("ref1Arg1", "ref1Arg1Val", RefType.STRING);
		
		ref1.setName("ref1");
		ref1.setClassName("ref1ClassName");
		ref1.setAbstractBean(true);
		ref1.setDestroyMethod(ref1Destroy);
		ref1.setInitMethod(ref1Init);
		ref1.addProperty(ref1Arg1);
		
		BeanRef ref2 = new BeanRef();
		MethodRef ref2Init = new MethodRef("ref2Init");
		MethodRef ref2Destroy = new MethodRef("ref2Destroy");
		Argument ref2Arg2 = new Argument("ref2Arg2", "ref2Arg2Val", RefType.STRING);
		
		ref2.setName("ref1"); // Should overwrite
		ref2.setClassName("ref2ClassName");
		ref2.setAbstractBean(false);
		ref2.setDestroyMethod(ref2Destroy);
		ref2.setInitMethod(ref2Init);
		ref2.addProperty(ref2Arg2);
		
		BeanRef ref3 = new BeanRef();
		MethodRef ref3Init = new MethodRef("ref3Init");
		MethodRef ref3Destroy = new MethodRef("ref3Destroy");
		Argument ref3Arg3 = new Argument("ref3Arg3", "ref3Arg3Val", RefType.STRING);
		
		ref3.setName("ref3");
		ref3.setClassName("ref3ClassName");
		ref3.setAbstractBean(true);
		ref3.setDestroyMethod(ref3Destroy);
		ref3.setInitMethod(ref3Init);
		ref3.addProperty(ref3Arg3);
		
		BeanMapping mapping0 = new BeanMapping();
		BeanMapping mapping1 = new BeanMapping();
		
		mapping0.addBeanRef(ref0);
		mapping0.addBeanRef(ref1);
		
		mapping1.addBeanRef(ref2);
		mapping1.addBeanRef(ref3);
		
		mapping0.merge(mapping1);
		
		Collection<BeanRef> beanRefs = mapping0.getBeanRefs();
		
		assertNotNull(beanRefs);
		assertEquals(3, beanRefs.size());
		
		BeanRef beanRef0 = mapping0.getBeanRef("ref0");
		BeanRef beanRef1 = mapping0.getBeanRef("ref1");
		BeanRef beanRef3 = mapping0.getBeanRef("ref3");
		
		assertNotNull(beanRef0);
		assertNotNull(beanRef1);
		assertNotNull(beanRef3);
		
		assertSame(ref0, beanRef0);
		
		assertEquals(ref2.getName(), beanRef1.getName());
		assertEquals(ref2.getDestroyMethod(), beanRef1.getDestroyMethod());
		assertEquals(ref2.getInitMethod(), beanRef1.getInitMethod());
		assertEquals(ref2.getClassName(), beanRef1.getClassName());
	
		List<Argument> ref2Args = ref2.getProperties();
		List<Argument> beanRef1Args = beanRef1.getProperties();
		
		assertNotNull(ref2Args);
		assertNotNull(beanRef1Args);
		
		assertEquals(ref2Args.size(), beanRef1Args.size());
		
		int index = 0;
		
		for (Argument argument : ref2Args) {
			assertEquals(argument.getKey(), beanRef1Args.get(index).getKey());
			assertEquals(argument.getValue(), beanRef1Args.get(index).getValue());
			index++;
		}
	}
}
