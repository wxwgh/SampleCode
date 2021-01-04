package com.supermap.desktop.vectoreditor.ctrlaction;


import com.supermap.data.DatasetType;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.mapeditor.ctrlAction.createGeometry.ActionCreateBase;
import com.supermap.desktop.mapeditor.ctrlAction.createGeometry.CreateTextAction;
import com.supermap.ui.Action;
import com.supermap.ui.MapControl;

public class CtrlActionCreateText extends ActionCreateBase {
    public CtrlActionCreateText(IBaseItem caller) {
        super(caller);
    }

    public Action getAction() {
        return Action.CREATETEXT;
    }

    public void run() {
        try {
            IFormMap formMap = (IFormMap) Application.getActiveApplication().getActiveForm();
            CreateTextAction createTextAction = new CreateTextAction();
            createTextAction.start((MapControl) formMap.getMapControl());
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    public boolean isSupportDatasetType(DatasetType datasetType) {
        return (DatasetType.TEXT == datasetType || DatasetType.CAD == datasetType);
    }
}
