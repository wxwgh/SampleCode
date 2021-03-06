package com.supermap.desktop.usercontrol;

import com.supermap.desktop.core.Application;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author SuperMap
 * @date 2018/11/6 0006、上午 9:21
 * @description 获取词条
 */
public class UserControlProperties {

    private static final String USERCONTROL = "UserControl";

    public static String getString(String key) {

        return getString(USERCONTROL, key);
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
