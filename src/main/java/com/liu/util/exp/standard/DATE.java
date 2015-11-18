package com.liu.util.exp.standard;

import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.Expression;
import com.liu.util.exp.ExpressionProcessor;
import com.liu.util.exp.exception.ExprException;

import java.util.Date;
import java.util.List;

public class DATE extends Expression {

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			Date result = null;
			Object lso = ls.get(1);
			if (lso instanceof List) {
				result = ConversionUtils.convert(processor.run((List<?>) lso), Date.class);
			} else {
				result = ConversionUtils.convert(ls.get(1), Date.class);
			}
			return result;
		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}
	}

	@Override
	public String toString(List<?> ls, ExpressionProcessor processor) throws ExprException {
		return "'" + ConversionUtils.convert(run(ls, processor), String.class) + "'";
	}
}
