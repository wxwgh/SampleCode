package com.supermap.desktop.download;

import com.supermap.desktop.core.AbstractPlugin;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.PluginInfo;
import com.supermap.desktop.core.event.FormLoadedListener;
import com.supermap.desktop.core.license.LicenseException;
import com.supermap.desktop.download.util.DatasourceOpenOnlineMapUtilties;

import java.util.EventObject;

/**
 * @author SuperMap
 * @time 2018/4/16 0016 上午 10:05
 */
public class DownLoadPlugin extends AbstractPlugin {

    public DownLoadPlugin(String name, PluginInfo pluginInfo) throws LicenseException {
        super(name, pluginInfo);
        Application.getActiveApplication().addFormLoadedListener(new FormLoadedListener() {
            @Override
            public void loadFinish(EventObject eventObject) {
                DatasourceOpenOnlineMapUtilties.openGoogelMap("Google", "路网地图");
            }
        });
    }

    @Override
    public boolean isGranted() {
        return true;
    }

    @Override
    public String getPluginTitle() {
        return "DownLoad";
    }

    @Override
    public String getPluginName() {
        return "SuperMap.Desktop.DownLoad";
    }
}

