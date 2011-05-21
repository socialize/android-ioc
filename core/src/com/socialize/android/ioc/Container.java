package com.socialize.android.ioc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;

public class Container {

	private Map<String, Object> beans;
	private Context context;
	private BeanMapping mapping;
	private ContainerBuilder builder;
	
	protected Container(Context context, BeanMapping mapping, ContainerBuilder builder) {
		super();
		beans = new TreeMap<String, Object>();
		this.context = context;
		this.mapping = mapping;
	}

	@SuppressWarnings("unchecked")
	public final <T extends Object> T getBean(String name) {
		Object bean = beans.get(name);
		if(bean == null) {
			BeanRef beanRef = mapping.getBeanRef(name);
			if(!beanRef.isSingleton()) {
				bean = builder.buildBean(this, beanRef);
			}
		}
		return (T) bean;
	}
	
	public final boolean containsBean(String name) {
		return beans.containsKey(name);
	}
	
	protected void putBean(String name, Object bean) {
		beans.put(name, bean);
	}
	
	protected Map<String, Object> getBeans() {
		return beans;
	}
	
	public BeanMapping getMapping() {
		return mapping;
	}

	protected void setMapping(BeanMapping mapping) {
		this.mapping = mapping;
	}

	public void loadFromAsset(String assetpath) throws IOException {
		InputStream in = null;
		
		try {
			in = context.getAssets().open(assetpath);
			loadFromStream(in);
		}
		finally {
			if(in != null) {
				in.close();
			}
		}
	}
	
	public void loadFromClasspath(String classpath) throws IOException {
		InputStream in = null;
		
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(classpath);
			loadFromStream(in);
		}
		finally {
			if(in != null) {
				in.close();
			}
		}
		
	}
	
	public void loadFromStream(InputStream in) throws IOException {
		
	}
}
