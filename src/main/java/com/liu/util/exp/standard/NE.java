package com.liu.util.exp.standard;

import com.liu.util.exp.Expression;
import com.liu.util.exp.ExpressionProcessor;
import com.liu.util.exp.exception.ExprException;
import org.springframework.util.ObjectUtils;

import java.util.List;

public class NE extends Expression {

	public NE() {
		symbol = "!=";
	}

	@Override
	public Object run(List<?> ls, ExpressionProcessor processor) throws ExprException {
		try {
			Object v1 = ls.get(1);
			if (v1 instanceof List) {
				v1 = processor.run((List<?>) v1);
			}
			for (int i = 2, size = ls.size(); i < size; i++) {
				Object v2 = ls.get(i);
				if (v2 instanceof List) {
					v2 = processor.run((List<?>) v2);
				}
				if (ObjectUtils.nullSafeEquals(v1, v2)) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			throw new ExprException(e.getMessage());
		}
	}

}
