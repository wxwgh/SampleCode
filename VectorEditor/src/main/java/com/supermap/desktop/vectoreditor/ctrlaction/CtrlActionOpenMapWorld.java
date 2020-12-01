package com.supermap.desktop.vectoreditor.ctrlaction;

import com.supermap.desktop.controls.ui.controls.DatasourceOperatorType;
import com.supermap.desktop.controls.ui.controls.JDialogDatasourceOpenAndNewDatabase;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;

public class CtrlActionOpenMapWorld extends CtrlAction {
    public CtrlActionOpenMapWorld(IBaseItem caller) {
        super(caller);
    }

    public void run() {
        try {
            JDialogDatasourceOpenAndNewDatabase dialog = new JDialogDatasourceOpenAndNewDatabase(DatasourceOperatorType.OPEN_WEB);
            dialog.setListSelection("MapWorld");
            dialog.showDialog();
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    public boolean enable() {
        return true;
    }
}
