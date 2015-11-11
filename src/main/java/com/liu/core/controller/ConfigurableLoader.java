package com.liu.core.controller;

public interface ConfigurableLoader<T extends Configurable> {
	public T load(String id);
}
