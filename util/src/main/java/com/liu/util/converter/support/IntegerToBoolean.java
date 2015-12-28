package com.liu.util.converter.support;

import org.springframework.core.convert.converter.Converter;

public class IntegerToBoolean implements Converter<Integer,Boolean> {

	@Override
	public Boolean convert(Integer source) {
		if(source == 0){
			return false;
		}
		return true;
	}



}
