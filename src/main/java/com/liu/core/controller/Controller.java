package com.liu.core.controller;

public interface Controller<T extends Configurable> {
	public T get(String id);

	public void add(T t);

	public void reload(String id);

	public boolean isLoaded(String id);

	public void reloadAll();

	public void setLoader(ConfigurableLoader<T> loader);

	public ConfigurableLoader<T> getLoader();
}
