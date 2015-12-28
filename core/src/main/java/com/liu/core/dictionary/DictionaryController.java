package com.liu.core.dictionary;

import com.liu.core.controller.support.AbstractController;

public class DictionaryController extends AbstractController<Dictionary> {
	private static DictionaryController instance;

	private DictionaryController() {
		setLoader(new DictionaryLocalLoader());
		instance = this;
	}

	public static DictionaryController instance() {
		if (instance == null) {
			instance = new DictionaryController();
		}
		return instance;
	}
}
