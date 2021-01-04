package com.supermap.desktop.download.ctrlaction;

import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.download.util.OpenOnlineMapUtilties;

public class CtrlActionOnLineMapBrowse extends CtrlAction {
    public CtrlActionOnLineMapBrowse(IBaseItem caller) {
        super(caller);
    }

    @Override
    protected void run() {
        try {
            OpenOnlineMapUtilties.openOnlineMap(this.getCaller().getID(), this.getCaller().getIndex());
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }
}
