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
package com.socialize.android.ioc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import android.util.Log;

/**
 * 
 * @author Jason Polites
 *
 */
public class BeanMappingParser {
	
	public static final String DEFAULT_FILENAME = "android-beans.xml";
	
	public BeanMapping parse(Context context) throws IOException {
		return parse(context, DEFAULT_FILENAME);
	}
	
	public BeanMapping parse(Context context, String filename) throws IOException {
		InputStream in = null;
		
		try {
			in = findFile(context, filename);
			if(in != null) {
				return parse(context, in);
			}
			else {
				throw new FileNotFoundException("Faile to locate bean config [" +
						filename +
						"] in either classpath or asset folder");
			}
			
		}
		finally {
			if(in != null) {
				in.close();
			}
		}
	}
	
	private InputStream findFile(Context context, String filename) {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
		if (in == null) {
			try {
				in = context.getAssets().open(filename);
			}
			catch (IOException ignore) {}
		}
		return in;
	}
	
	public BeanMapping parse(Context context, InputStream in) {
		BeanMapping mapping = null;
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			BeanMappingParserHandler handler = new BeanMappingParserHandler();
			parser.parse(in, handler);
			mapping = handler.getBeanMapping();
		}
		catch (Exception e) {
			Log.e("BeanMappingParser", "IOC Parse error", e);
		}
		return mapping;
	}
	
}
