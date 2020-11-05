package com.supermap.desktop.develop;

import com.supermap.desktop.core.Application;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author SuperMap
 * @date 2018/11/6 0006、上午 9:21
 * @description 获取词条
 */
public class DevelopProperties {

	private static final String DEVELOP = "Develop";

	public static String getString(String key) {
		return getString(DEVELOP, key);
	}

	private static String getString(String baseName, String key) {
		String result = "";

		ResourceBundle resourceBundle = ResourceBundle.getBundle(baseName, Locale.getDefault());
		if (resourceBundle != null) {
			try {
				result = resourceBundle.getString(key);
			} catch (Exception e) {
				Application.getActiveApplication().getOutput().output(e);
			}
		}
		return result;
	}
}
