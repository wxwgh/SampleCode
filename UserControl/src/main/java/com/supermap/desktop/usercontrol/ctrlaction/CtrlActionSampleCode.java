package com.supermap.desktop.usercontrol.ctrlaction;

import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.usercontrol.ui.DialogSampleCode;

/**
 * @author SuperMap
 */
public class CtrlActionSampleCode extends CtrlAction {

    public CtrlActionSampleCode(IBaseItem caller) {
        super(caller);
    }

    @Override
    public void run() {
        new DialogSampleCode().showDialog();
    }
}
