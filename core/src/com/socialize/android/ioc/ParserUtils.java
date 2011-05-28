package com.socialize.android.ioc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ParserUtils {

	public void merge(BeanRef source, BeanRef destination) {
		if(source.getProperties() != null) {
			if(destination.getProperties() == null) {
				List<Argument> destList = new ArrayList<Argument>(source.getProperties());
				destination.setProperties(destList);
			}
			else {
				merge(source.getProperties(), destination.getProperties());
			}
		}
		
		if(destination.getClassName() == null) {
			destination.setClassName(source.getClassName());
		}
		
		if(destination.getInitMethod() == null) {
			destination.setInitMethod(source.getInitMethod());
		}
		
		if(destination.getDestroyMethod() == null) {
			destination.setDestroyMethod(source.getDestroyMethod());
		}
	}
	
	public void merge(List<Argument> source, List<Argument> desination) {
		List<Argument> result = new LinkedList<Argument>();
		// Dupe dest
		List<Argument> dupe = new LinkedList<Argument>(desination);
		for (Argument src : source) {
			
			boolean found = false;
			
			for (Argument dest : dupe) {
				if(src.getKey() != null && dest.getKey() != null && src.getKey().equals(dest.getKey())) {
					result.add(dest);
					dupe.remove(dest);
					found = true;
					break;
				}
			}
			
			if(!found) {
				result.add(src);
			}
		}
		
		desination.clear();
		desination.addAll(result);
		desination.addAll(dupe);
	}
}
