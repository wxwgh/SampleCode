package com.supermap.desktop.maptool.ctrlaction;


import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IFormManager;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.layoutview.LayoutWindow.FormCreateLayout;

/**
 * @author Li Jian
 * @create 2020-11-30 17:00
 */
public class CtrlActionCreateNewLayout extends CtrlAction {
    private FormCreateLayout a;

    public CtrlActionCreateNewLayout(IBaseItem caller) {
        super(caller);
    }

    public void run() {
        try {
            if (this.a == null) {
                this.a = new FormCreateLayout();
            }

            IFormManager formManager = Application.getActiveApplication().getMainFrame().getFormManager();
            if (!this.a.isShowing()) {
                formManager.setActiveForm(this.a);
            }
        } catch (Exception var2) {
            Application.getActiveApplication().getOutput().output(var2);
        }

    }

    public boolean enable() {
        return true;
    }
}