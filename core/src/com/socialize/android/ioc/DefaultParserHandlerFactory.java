package com.socialize.android.ioc;


public class DefaultParserHandlerFactory implements ParserHandlerFactory {
	@Override
	public BeanMappingParserHandler newInstance() {
		return new BeanMappingParserHandler();
	}
}
