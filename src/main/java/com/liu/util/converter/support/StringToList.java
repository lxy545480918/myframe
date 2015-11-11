package com.liu.util.converter.support;


import nw.util.JSONUtils;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

@SuppressWarnings("rawtypes")
public class StringToList implements Converter<String,List> {
	
	@Override
	public List convert(String source) {
		return JSONUtils.parse(source, List.class);
	}

}
