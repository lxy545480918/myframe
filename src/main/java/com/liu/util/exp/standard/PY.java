package com.liu.util.exp.standard;

import com.liu.util.PyConverter;
import com.liu.util.converter.ConversionUtils;
import com.liu.util.exp.Expression;
import com.liu.util.exp.ExpressionProcessor;
import com.liu.util.exp.exception.ExprException;

import java.util.List;

public class PY extends Expression {

	public PY() {
		symbol = "pingyin";
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor) throws ExprException {

		try {
			Object lso = ls.get(1);
			String str = null;
			if (lso instanceof List) {
				str = (String) processor.run((List<?>) lso);
			} else {
				str = ConversionUtils.convert(lso, String.class);
			}
			return PyConverter.getFirstLetter(str);
		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}

	}

	@Override
	public String toString(List<?> ls, ExpressionProcessor processor) throws ExprException {
		return symbol + "(" + processor.toString((List<?>) ls.get(1)) + ")";
	}

}
