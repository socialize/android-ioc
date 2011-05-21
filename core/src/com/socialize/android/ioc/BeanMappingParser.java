package com.socialize.android.ioc;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import android.util.Log;

public class BeanMappingParser {
	
	public static final String DEFAULT_FILENAME = "android-beans.xml";
	
	public BeanMapping parseFromAsset(Context context) throws IOException {
		return parseFromAsset(context, DEFAULT_FILENAME);
	}
	
	public BeanMapping parseFromClassPath(Context context) throws IOException {
		return parseFromClassPath(context, DEFAULT_FILENAME);
	}
	
	public BeanMapping parseFromAsset(Context context, String filename) throws IOException {
		InputStream in = null;
		
		try {
			in = context.getAssets().open(filename);
			return parse(context, in);
		}
		finally {
			if(in != null) {
				in.close();
			}
		}
	}
	
	public BeanMapping parseFromClassPath(Context context, String filename) throws IOException {
		InputStream in = null;
		
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
			return parse(context, in);
		}
		finally {
			if(in != null) {
				in.close();
			}
		}
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
