package com.supermap.desktop.maptool;

import com.supermap.desktop.core.AbstractPlugin;
import com.supermap.desktop.core.PluginInfo;
import com.supermap.desktop.core.license.LicenseException;

/**
 * @author SuperMap
 * @time 2018/4/16 0016 上午 10:05
 */
public class MapToolPlugin extends AbstractPlugin {

    public MapToolPlugin(String name, PluginInfo pluginInfo) throws LicenseException {
        super(name, pluginInfo);
    }

    @Override
    public boolean isGranted() {
        return true;
    }

    @Override
    public String getPluginTitle() {
        return "MapTool";
    }

    @Override
    public String getPluginName() {
        return "SuperMap.Desktop.MapTool";
    }
}

