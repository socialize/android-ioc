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
package com.socialize.android.ioc.sample;

// Must be comparable for set tests.
public class TestClassWithInitMethod implements Comparable<TestClassWithInitMethod> {
	
	private boolean initialized = false;
	private double rnd;
	private TestClassWithInitMethod other;
	
	public TestClassWithInitMethod() {
		super();
		rnd = Math.random() * 100d;
	}
	
	public void doInit() {
		initialized = true;
	}
	
	public void doInitDepends(TestClassWithInitMethod other) {
		doInit();
		this.other = other;
		
		if(!other.isInitialized()) {
			throw new RuntimeException("Other bean not intialized");
		}
	}
	
	public TestClassWithInitMethod getOther() {
		return other;
	}

	public boolean isInitialized() {
		return initialized;
	}

	@Override
	public int compareTo(TestClassWithInitMethod another) {
		return (int)rnd - (int)another.rnd;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(rnd);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestClassWithInitMethod other = (TestClassWithInitMethod) obj;
		if (Double.doubleToLongBits(rnd) != Double.doubleToLongBits(other.rnd))
			return false;
		return true;
	}
	
	
}
