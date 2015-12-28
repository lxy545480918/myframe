package com.liu.util.converter.support;


import org.springframework.core.convert.converter.Converter;

import java.sql.Time;


public class StringToSQLTime implements Converter<String,Time> {
	
	
	@Override
	public Time convert(String source) {
		return Time.valueOf(source);
	}

}
