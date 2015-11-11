package com.liu.util.converter.support;

import com.liu.util.converter.ConversionUtils;
import org.springframework.core.convert.converter.Converter;

import java.sql.Timestamp;
import java.util.Date;

public class StringToTimestamp implements Converter<String,Timestamp> {
	
	@Override
	public Timestamp convert(String source) {
		Date dt = ConversionUtils.convert(source, Date.class);
		return new Timestamp(dt.getTime());
	}

}
