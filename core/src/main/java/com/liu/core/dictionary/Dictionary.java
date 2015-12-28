package com.liu.core.dictionary;

import com.liu.core.controller.support.AbstractConfigurable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public abstract class Dictionary extends AbstractConfigurable {
	private static final long serialVersionUID = 5186888641454350567L;
	protected HashMap<String, DictionaryItem> items = new LinkedHashMap<String, DictionaryItem>();
	protected String clazz = "XMLDictionary";
	protected String searchField = "mCode";
	protected String searchFieldEx = "text";
	protected String alias;
	protected boolean isPrivate = false;
	protected boolean queryOnly = false;

	protected char searchExSymbol = '.';
	protected char searchKeySymbol = '/';

	public Dictionary() {

	}

	public void setClass(String clazz) {
		this.clazz = clazz;
	}

	public String getCls() {
		return clazz;
	}

	public Dictionary(String id) {
		this.id = id;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public void addItem(DictionaryItem item) {
		items.put(item.getKey(), item);
	}

	public void removeItem(String key) {
		items.remove(key);
	}

	public DictionaryItem getItem(String key) {
		return items.get(key);
	}

	public boolean keyExist(String key) {
		return items.containsKey(key);
	}

	public String getText(String key) {
		if (items.containsKey(key)) {
			return items.get(key).getText();
		}
		return "";
	}

	public List<String> getKey(String text) {
		List<String> list = new ArrayList<String>();
		for (String key : items.keySet()) {
			if (text.equals(items.get(key).getText())) {
				list.add(key);
			}
		}
		return list;
	}

	public List<DictionaryItem> itemsList() {
		List<DictionaryItem> ls = new ArrayList<DictionaryItem>();
		for (DictionaryItem di : items.values()) {
			ls.add(di);
		}
		return ls;
	}

	public boolean isPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(boolean isPrivate) {
		this.isPrivate = isPrivate;
	}

	public String getSearchField() {
		return searchField;
	}

	public void setSearchField(String searchField) {
		this.searchField = searchField;
	}

	public String getSearchFieldEx() {
		return searchFieldEx;
	}

	public boolean isQueryOnly() {
		return queryOnly;
	}

	public void setQueryOnly(boolean queryOnly) {
		this.queryOnly = queryOnly;
	}

	public void setSearchFieldEx(String searchFieldEx) {
		this.searchFieldEx = searchFieldEx;
	}

	public char getSearchExSymbol() {
		return searchExSymbol;
	}

	public void setSearchExSymbol(char searchExSymbol) {
		this.searchExSymbol = searchExSymbol;
	}

	public char getSearchKeySymbol() {
		return searchKeySymbol;
	}

	public void setSearchKeySymbol(char searchKeySymbol) {
		this.searchKeySymbol = searchKeySymbol;
	}

	public abstract List<DictionaryItem> getSlice(String parentKey, int sliceType, String query);

	/**
	 * execute after set properties
	 */
	public void init() {
		// do something custom
	}

	public HashMap<String, DictionaryItem> getItems() {
		return items;
	}

	public void setItems(HashMap<String, DictionaryItem> items) {
		this.items = items;
	}

}
