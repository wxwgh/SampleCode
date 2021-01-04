package com.supermap.desktop.vectoreditor.ctrlaction;

import com.supermap.data.EngineType;
import com.supermap.desktop.controls.utilities.DatasourceOpenOnlineMapUtilties;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;

public class CtrlActionOpenGoolgeMaps extends CtrlAction {
    public CtrlActionOpenGoolgeMaps(IBaseItem caller) {
        super(caller);
    }

    public void run() {
        try {
            DatasourceOpenOnlineMapUtilties.openOnlineMap(EngineType.GOOGLEMAPS, Boolean.valueOf(true));
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    public boolean enable() {
        return true;
    }
}
