package com.socialize.android.ioc.sample;

public class TestClassWithPrintMethod implements ITestClassWithPrintMethod {

	/* (non-Javadoc)
	 * @see com.socialize.android.ioc.sample.ITestClassWithPrintMethod#print()
	 */
	@Override
	public String print() {
		return "Hello World";
	}
	
}
