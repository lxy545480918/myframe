package com.liu.util.exp.standard;

import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.Expression;
import com.liu.util.exp.ExpressionProcessor;
import com.liu.util.exp.exception.ExprException;

import java.util.List;

public class STR extends Expression {

	public STR() {
		name = "s";
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			return ConversionUtils.convert(ls.get(1), String.class);
		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}
	}

	@Override
	public String toString(List<?> ls, ExpressionProcessor processor) throws ExprException {
		return "'" + run(ls, processor) + "'";
	}

}
