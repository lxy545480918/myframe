package com.liu.util.exception;

public interface CodedBase {
	public int getCode();

	public String getMessage();

	public Throwable getCause();

	public StackTraceElement[] getStackTrace();

	void throwThis() throws Exception;
}
