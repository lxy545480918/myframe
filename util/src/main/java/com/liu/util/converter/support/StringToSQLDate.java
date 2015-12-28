package com.liu.util.converter.support;


import org.joda.time.format.DateTimeFormat;
import org.springframework.core.convert.converter.Converter;

import java.sql.Date;


public class StringToSQLDate implements Converter<String,Date> {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	
	@Override
	public Date convert(String source) {
		return  new Date(DateTimeFormat.forPattern(DATE_FORMAT).parseMillis(source));
	}

}
