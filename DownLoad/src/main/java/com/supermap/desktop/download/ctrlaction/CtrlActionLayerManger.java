package com.supermap.desktop.download.ctrlaction;

import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IDockbar;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.download.ui.MapAppLayerManager;

public class CtrlActionLayerManger extends CtrlAction {
    private IDockbar dockbarPropertyContainer = null;
    public CtrlActionLayerManger(IBaseItem caller) {
        super(caller);
    }

    @Override
    protected void run() {
        try {
            dockbarPropertyContainer = Application.getActiveApplication().getMainFrame().getDockbarManager().get(Class.forName("com.supermap.desktop.mapapp.ui.MapAppLayerManager"));
            if (dockbarPropertyContainer != null) {
                MapAppLayerManager panel = (MapAppLayerManager)dockbarPropertyContainer.getInnerComponent();
                panel.dockbar = dockbarPropertyContainer;
                panel.initPanel();
                dockbarPropertyContainer.setVisible(true);
                dockbarPropertyContainer.active();
            }
        } catch (ClassNotFoundException e) {
            Application.getActiveApplication().getOutput().output(e);
        }
    }

    @Override
    public boolean enable() {
        Boolean result = Boolean.valueOf(true);
        if (dockbarPropertyContainer != null) {
            if(dockbarPropertyContainer.isVisible())
                result = Boolean.valueOf(false);
        }
        return result;
    }
}
