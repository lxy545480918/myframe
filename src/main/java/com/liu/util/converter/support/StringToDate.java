package com.liu.util.converter.support;

import com.liu.util.converter.ConversionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.core.convert.converter.Converter;

import java.util.Date;

public class StringToDate implements Converter<String,Date> {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME1_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String DATETIME2_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATETIME3_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

	public static void main(String[] args) {
		String s = "2015-08-02 11:20:33";
		System.out.println(ConversionUtils.convert(s, Date.class));

		s = "2015-08-02T11:20:33";
		System.out.println(ConversionUtils.convert(s, Date.class));
	}

    
    public static Date toDate(String s){
    	return DateTimeFormat.forPattern(DATE_FORMAT).withZoneUTC().parseLocalDate(s).toDate();
    }
    
    public static Date toDatetime(String s){
    	return DateTimeFormat.forPattern(DATETIME1_FORMAT).withZoneUTC().parseDateTime(s).toDate();
    }

	@Override
	public Date convert(String source) {
		if(StringUtils.isEmpty(source)){
			return null;
		}
		if(StringUtils.contains(source, "T")){
			if(StringUtils.contains(source, "Z")){
				if(source.length() == 20){
					return DateTimeFormat.forPattern(DATETIME3_FORMAT).withZoneUTC().parseDateTime(source).toDate();
				}
				else{
					return ISODateTimeFormat.dateTime().parseDateTime(source).toDate();
				}
			}
			return DateTimeFormat.forPattern(DATETIME2_FORMAT).withZoneUTC().parseDateTime(source).toDate();
		}
		else if(StringUtils.contains(source, ":")){
			return DateTimeFormat.forPattern(DATETIME1_FORMAT).parseDateTime(source).toDate();
		}
		else  if(StringUtils.contains(source, "-")){
			return DateTimeFormat.forPattern(DATE_FORMAT).parseLocalDate(source).toDate();
		}
		else if(StringUtils.equals(source.toLowerCase(), "now")){
			return new Date();
		}
		else if(StringUtils.equals(source.toLowerCase(), "today")){
			return (new DateTime()).withTimeAtStartOfDay().toDate();
		}
		else if(StringUtils.equals(source.toLowerCase(), "yesterday")){
			return (new LocalDate().minusDays(1).toDate());
		}
		else if(StringUtils.equals(source.toLowerCase(), "tomorrow")){
			return (new LocalDate().plusDays(1).toDate());
		}
		else{
			throw new IllegalArgumentException("Invalid date string value '" + source + "'");
		}
		
	}

}
