package com.supermap.desktop.usercontrol;

import com.supermap.desktop.core.AbstractPlugin;
import com.supermap.desktop.core.PluginInfo;
import com.supermap.desktop.core.license.LicenseException;

/**
 * @author SuperMap
 * @time 2018/4/16 0016 上午 10:05
 */
public class UserControlPlugin extends AbstractPlugin {

    public UserControlPlugin(String name, PluginInfo pluginInfo) throws LicenseException {
        super(name, pluginInfo);
    }

    @Override
    public boolean isGranted() {
        return true;
    }

    @Override
    public String getPluginTitle() {
        return "UserControl";
    }

    @Override
    public String getPluginName() {
        return "SuperMap.Desktop.UserControl";
    }
}

