package com.liu.util.exp;

import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.exception.ExprException;

import java.util.Date;
import java.util.List;

public class ExpressionUtils {

	public static Number toNumber(Object lso, ExpressionProcessor processor) throws ExprException {
		try {
			Number v = null;
			if (lso instanceof List) {
				v = ConversionUtils.convert(processor.run((List<?>) lso), Number.class);
			} else {
				v = ConversionUtils.convert(lso, Number.class);
			}
			return v;
		} catch (Exception e) {
			throw new ExprException(e);
		}
	}

	public static String toString(Object lso, ExpressionProcessor processor) throws ExprException {
		try {
			String s = null;
			if (lso instanceof List) {
				s = processor.toString((List<?>) lso);
			} else {
				if (lso instanceof String) {
					s = "'" + lso + "'";
				} else {
					s = ConversionUtils.convert(lso, String.class);
					if (lso instanceof Date) {
						return "'" + s + "'";
					}
				}
			}
			return s;
		} catch (Exception e) {
			throw new ExprException(e);
		}
	}
}
