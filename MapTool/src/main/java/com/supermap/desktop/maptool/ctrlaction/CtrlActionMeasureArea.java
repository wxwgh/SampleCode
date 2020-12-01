package com.supermap.desktop.maptool.ctrlaction;


import com.supermap.desktop.controls.ui.measure.MeasureArea;
import com.supermap.desktop.controls.ui.measure.MeasureUtilties;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IForm;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.enums.MeasureType;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.ui.MapControl;

import java.util.HashMap;

/**
 * @author Li Jian
 * @create 2020-11-25 16:22
 */
public class CtrlActionMeasureArea extends CtrlAction {
    public static HashMap<MapControl, MeasureArea> hashMap;

    public CtrlActionMeasureArea(IBaseItem caller) {
        super(caller);
    }

    public void run() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        if (activeForm instanceof com.supermap.desktop.mapview.FormMap)
            MeasureUtilties.startMeasure((IFormMap)activeForm, MeasureType.Area);
    }

    public boolean enable() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        return activeForm instanceof com.supermap.desktop.mapview.FormMap;
    }
}

