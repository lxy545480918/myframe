package com.liu.util.converter.support;

import nw.util.JSONUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.Map;

@SuppressWarnings("rawtypes")
public class StringToMap implements Converter<String,Map> {
	
	@Override
	public Map convert(String source) {
		return JSONUtils.parse(source, Map.class);
	}

}
