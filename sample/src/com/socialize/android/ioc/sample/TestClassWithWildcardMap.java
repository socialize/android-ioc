package com.socialize.android.ioc.sample;

import java.util.Map;

public class TestClassWithWildcardMap {
	
	private Map<String, ? extends Number> wilcardMap = null;
	private Map<String, ?> anythingMap = null;

	public TestClassWithWildcardMap() {
		super();
	}
	
	public TestClassWithWildcardMap(Map<String, ? extends Number> wilcardMap) {
		super();
	}

	public Map<String, ? extends Number> getWilcardMap() {
		return wilcardMap;
	}

	public void setWilcardMap(Map<String, ? extends Number> wilcardMap) {
		this.wilcardMap = wilcardMap;
	}

	public Map<String, ?> getAnythingMap() {
		return anythingMap;
	}

	public void setAnythingMap(Map<String, ?> anythingMap) {
		this.anythingMap = anythingMap;
	}
}