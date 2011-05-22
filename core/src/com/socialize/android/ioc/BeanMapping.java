package com.socialize.android.ioc;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BeanMapping {

	private Map<String, BeanRef> beanRefs;

	public Collection<BeanRef> getBeanRefs() {
		return beanRefs.values();
	}

	public synchronized void addBeanRef(BeanRef ref) {
		if(beanRefs == null) {
			beanRefs = new HashMap<String, BeanRef>();
		}
		beanRefs.put(ref.getName(), ref);
	}

	public BeanRef getBeanRef(String name) {
		return beanRefs.get(name);
	}
}
