package com.supermap.desktop.download.ctrlaction;

import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.download.util.OpenTdtMapUtilties;

public class CtrlActionTdtMapBrowse extends CtrlAction {
    public CtrlActionTdtMapBrowse(IBaseItem caller) {
        super(caller);
    }
    @Override
    protected void run() {
        try {
            OpenTdtMapUtilties.openOnlineMap(this.getCaller().getID(), this.getCaller().getIndex());
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }
}
