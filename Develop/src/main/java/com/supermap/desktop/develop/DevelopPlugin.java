package com.supermap.desktop.develop;

import com.supermap.ProductType;
import com.supermap.desktop.core.AbstractPlugin;
import com.supermap.desktop.core.PluginInfo;
import com.supermap.desktop.core.license.LicenseException;
import com.supermap.desktop.core.license.LicenseManager;
import com.supermap.desktop.core.search.CtrlActionSearchManager;
import com.supermap.desktop.dashboard.DashboardManger;

/**
 * @author SuperMap
 * @time 2018/4/16 0016 上午 10:05
 */
public class DevelopPlugin extends AbstractPlugin {

	public DevelopPlugin(String name, PluginInfo pluginInfo) throws LicenseException {
		super(name, pluginInfo);
	}

	@Override
	public boolean isGranted() {
		return true;
	}

	@Override
	public String getPluginTitle() {
		return "Develop";
	}

	@Override
	public String getPluginName() {
		return "SuperMap.Desktop.Develop";
	}
}

