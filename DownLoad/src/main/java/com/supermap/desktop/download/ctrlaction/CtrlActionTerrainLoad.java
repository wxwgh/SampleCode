package com.supermap.desktop.download.ctrlaction;

import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.download.ui.DialogTerrainLoad;

public class CtrlActionTerrainLoad extends CtrlAction {

    public CtrlActionTerrainLoad(IBaseItem caller) {
        super(caller);
    }

    @Override
    public void run() {
        new DialogTerrainLoad().showDialog();
    }
}
