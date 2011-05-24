package com.socialize.android.ioc.sample;

// Must be comparable for set tests.
public class TestClassWithInitMethod implements Comparable<TestClassWithInitMethod> {
	
	private boolean initialized = false;
	private double rnd;
	
	public TestClassWithInitMethod() {
		super();
		rnd = Math.random() * 100d;
	}
	
	public void doInit() {
		initialized = true;
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