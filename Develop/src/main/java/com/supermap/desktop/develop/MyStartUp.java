package com.supermap.desktop.develop;

import com.supermap.desktop.core.Application;

/**
 * @author SuperMap
 */
public class MyStartUp {
	public static void main(String[] args) {
		if (!Application.getActiveApplication().initialize()) {
			System.exit(0);
		}
	}
}
