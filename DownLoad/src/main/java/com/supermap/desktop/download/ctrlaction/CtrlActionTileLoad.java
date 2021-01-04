package com.supermap.desktop.download.ctrlaction;

import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.download.ui.DialogTileLoad;

/**
 * @author SuperMap
 */
public class CtrlActionTileLoad extends CtrlAction {

    public CtrlActionTileLoad(IBaseItem caller) {
        super(caller);
    }

    @Override
    public void run() {
        new DialogTileLoad().showDialog();
    }
}
