package com.supermap.desktop.maptool.ctrlaction;


import com.supermap.data.EngineType;
import com.supermap.desktop.controls.ui.controls.prjcoordsys.JDialogBatchPrjTranslator;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.core.utilties.DatasetUtilities;

/**
 * @author Li Jian
 * @create 2020-11-25 17:26
 */
public class CtrlActionBatchPrjTranslator extends CtrlAction {
    public CtrlActionBatchPrjTranslator(IBaseItem caller) {
        super(caller);
    }

    public void run() {
        JDialogBatchPrjTranslator dialogBatchPrjTranslator = new JDialogBatchPrjTranslator();
        dialogBatchPrjTranslator.showDialog();
    }

    public boolean enable() {
        return (null != DatasetUtilities.getDefaultDataset() && !DatasetUtilities.getDefaultDataset().getDatasource().getEngineType().equals(EngineType.ES));
    }
}