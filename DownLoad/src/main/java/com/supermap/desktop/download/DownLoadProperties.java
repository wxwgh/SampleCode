package com.supermap.desktop.download;

import com.supermap.desktop.core.Application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * @author SuperMap
 * @date 2018/11/6 0006、上午 9:21
 * @description 获取词条
 */
public class DownLoadProperties {

    private static final String DOWNLOAD = "DownLoad";

    public static String getString(String key) {

        return getString(DOWNLOAD, key);
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

    public static void setString(String key, String value, String path) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(path));
            OutputStream fos = new FileOutputStream(path);
            props.setProperty(key, value);
            props.store(fos, "Update '" + key + "' value");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    ;
}
