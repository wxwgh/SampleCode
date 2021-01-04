package com.supermap.desktop.download.ctrlaction;

import com.supermap.data.EngineType;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.download.util.DatasourceOpenOnlineMapUtilties;
//import com.supermap.desktop.controls.utilities.DatasourceOpenOnlineMapUtilties;

public class CtrlActionMapBrowseView extends CtrlAction {
    public CtrlActionMapBrowseView(IBaseItem caller) {
        super(caller);
    }

    @Override
    protected void run() {
        try {
            DatasourceOpenOnlineMapUtilties.openGoogelMap(this.getCaller().getID(), this.getCaller().getText());
//            DatasourceOpenOnlineMapUtilties.openGoogelMap("Baidu", this.getCaller().getText());
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }
}
