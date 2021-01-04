package com.supermap.desktop.download.ctrlaction;

import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.download.ui.DialogDownLoadTask;

public class CtrlActionDownLoadTask extends CtrlAction {

    public CtrlActionDownLoadTask(IBaseItem caller) {
        super(caller);
    }

    @Override
    public void run() {
        new DialogDownLoadTask().showDialog();
    }
}

