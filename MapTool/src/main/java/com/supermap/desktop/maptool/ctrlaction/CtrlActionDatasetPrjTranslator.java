package com.supermap.desktop.maptool.ctrlaction;


import com.supermap.desktop.controls.ui.controls.prjcoordsys.JDialogDatasetPrjTranslator;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.core.utilties.DatasetUtilities;

/**
 * @author Li Jian
 * @create 2020-11-25 17:24
 */
public class CtrlActionDatasetPrjTranslator extends CtrlAction {
    public CtrlActionDatasetPrjTranslator(IBaseItem caller) {
        super(caller);
    }

    public void run() {
        JDialogDatasetPrjTranslator dialogPrjTransform = new JDialogDatasetPrjTranslator();
        dialogPrjTransform.showDialog();
    }

    public boolean enable() {
        return (null != DatasetUtilities.getDefaultDataset());
    }
}
