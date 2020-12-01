package com.supermap.desktop.maptool.ctrlaction;


import com.supermap.desktop.controls.ui.measure.JDialogMeasureSetting;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IForm;
import com.supermap.desktop.core.implement.CtrlAction;

/**
 * @author Li Jian
 * @create 2020-11-25 16:29
 */
public class CtrlActionMeasureSetting extends CtrlAction {
    public CtrlActionMeasureSetting(IBaseItem caller) {
        super(caller);
    }

    public void run() {
        JDialogMeasureSetting jDialogMeasureSetting = new JDialogMeasureSetting();
        jDialogMeasureSetting.showDialog();
    }

    public boolean enable() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        return activeForm instanceof com.supermap.desktop.mapview.FormMap;
    }
}