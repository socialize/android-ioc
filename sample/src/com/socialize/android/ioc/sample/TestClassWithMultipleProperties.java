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

public class TestClassWithMultipleProperties {
	String string;
	int integer;
	long lng;
	short shrt;
	char chr;
	boolean bool;
	byte bte;
	float flt;
	double dbl;
	
	public TestClassWithMultipleProperties() {
		super();
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public int getInteger() {
		return integer;
	}

	public void setInteger(int integer) {
		this.integer = integer;
	}

	public long getLng() {
		return lng;
	}

	public void setLng(long lng) {
		this.lng = lng;
	}

	public short getShrt() {
		return shrt;
	}

	public void setShrt(short shrt) {
		this.shrt = shrt;
	}

	public char getChr() {
		return chr;
	}

	public void setChr(char chr) {
		this.chr = chr;
	}

	public boolean isBool() {
		return bool;
	}

	public void setBool(boolean bool) {
		this.bool = bool;
	}

	public byte getBte() {
		return bte;
	}

	public void setBte(byte bte) {
		this.bte = bte;
	}

	public float getFlt() {
		return flt;
	}

	public void setFlt(float flt) {
		this.flt = flt;
	}

	public double getDbl() {
		return dbl;
	}

	public void setDbl(double dbl) {
		this.dbl = dbl;
	}
}
