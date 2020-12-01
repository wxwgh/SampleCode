package com.supermap.desktop.vectoreditor.ctrlaction;

import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.mapview.ctrlAction.mapOperator.CtrlActionMapActionBase;
import com.supermap.ui.Action;

public class CtrlActionMapSelect extends CtrlActionMapActionBase {
    public CtrlActionMapSelect(IBaseItem caller) {
        super(caller);
    }

    public Action getAction() {
        return Action.SELECT2;
    }
}
