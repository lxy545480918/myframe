package com.liu.util.exp.exception;

import com.liu.util.exception.CodedBaseRuntimeException;

public class ExprException extends CodedBaseRuntimeException {
	private static final long serialVersionUID = -1L;

	public ExprException(String msg) {
		super(msg);
	}

	public ExprException(Throwable e) {
		super(e);
	}
}
