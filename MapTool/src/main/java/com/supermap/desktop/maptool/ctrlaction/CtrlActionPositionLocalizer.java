package com.supermap.desktop.maptool.ctrlaction;


import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IDockbar;
import com.supermap.desktop.core.Interface.IForm;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.mapview.PositionLocalizerPanel;
/**
 * @author Li Jian
 * @create 2020-11-30 11:07
 */
public class CtrlActionPositionLocalizer extends CtrlAction {
    public CtrlActionPositionLocalizer(IBaseItem caller) {
        super(caller);
    }

    protected void run() {
        try {
            IDockbar dockbarPropertyContainer = Application.getActiveApplication().getMainFrame().getDockbarManager().get(Class.forName("com.supermap.desktop.mapview.PositionLocalizerPanel"));
            if (dockbarPropertyContainer != null) {
                PositionLocalizerPanel panel = (PositionLocalizerPanel)dockbarPropertyContainer.getInnerComponent();
                panel.dockbar = dockbarPropertyContainer;
                panel.initPanel();
                dockbarPropertyContainer.setVisible(true);
                dockbarPropertyContainer.active();
            }
        } catch (ClassNotFoundException var3) {
            Application.getActiveApplication().getOutput().output(var3);
        }

    }

    public boolean enable() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        return activeForm != null && activeForm instanceof IFormMap;
    }
}