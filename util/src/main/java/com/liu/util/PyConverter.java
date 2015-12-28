package com.liu.util;

import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.apache.commons.lang3.StringUtils;

public class PyConverter {

	public static String getFirstLetter(String s) {
		if (StringUtils.isEmpty(s)) {
			return null;
		}
		return PinyinHelper.getShortPinyin(s);
	}

	public static String getPinYinWithoutTone(String s) {
		return PinyinHelper.convertToPinyinString(s, "", PinyinFormat.WITHOUT_TONE);
	}

	public static String getPinYin(String s) {
		return PinyinHelper.convertToPinyinString(s, "");
	}

}
