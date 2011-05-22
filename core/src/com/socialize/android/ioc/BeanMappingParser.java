package com.socialize.android.ioc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import android.util.Log;

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
