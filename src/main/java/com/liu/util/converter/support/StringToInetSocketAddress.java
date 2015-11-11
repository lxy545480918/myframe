package com.liu.util.converter.support;

import nw.util.NetUtils;
import org.springframework.core.convert.converter.Converter;

import java.net.InetSocketAddress;

public class StringToInetSocketAddress implements Converter<String,InetSocketAddress> {
	
	@Override
	public InetSocketAddress convert(String source) {
		return NetUtils.toAddress(source);
	}

}
