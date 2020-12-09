package com.supermap.desktop.maptool.ctrlaction;


import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IForm;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.mapview.dialog.DialogMapPosition;
/**
 * @author Li Jian
 * @create 2020-11-30 15:02
 */
public class CtrlActionMapPosition extends CtrlAction {
    private DialogMapPosition a;

    public CtrlActionMapPosition(IBaseItem caller) {
        super(caller);
    }

    public void run() {
        try {
            if (this.a == null) {
                this.a = new DialogMapPosition();
                this.a.setModal(false);
            }

            this.a.initStates();
            this.a.registerEvents();
            this.a.showDialog();
        } catch (Exception var2) {
            Application.getActiveApplication().getOutput().output(var2);
        }

    }

    public boolean enable() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        return activeForm instanceof IFormMap;
    }
}
