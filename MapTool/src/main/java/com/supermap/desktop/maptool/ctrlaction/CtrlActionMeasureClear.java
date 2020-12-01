package com.supermap.desktop.maptool.ctrlaction;


import com.supermap.desktop.controls.ui.measure.MeasureUtilties;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IForm;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.core.utilties.ToolbarUIUtilities;
import com.supermap.desktop.mapview.FormMap;
import com.supermap.mapping.TrackingLayer;
import com.supermap.ui.Action;
import com.supermap.ui.TrackMode;

/**
 * @author Li Jian
 * @create 2020-11-25 16:27
 */
public class CtrlActionMeasureClear extends CtrlAction {
    public CtrlActionMeasureClear(IBaseItem caller) {
        super(caller);
    }

    public void run() {
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        if (activeForm instanceof FormMap) {
            FormMap formMap = (FormMap)activeForm;
            MeasureUtilties.endMeasure((IFormMap)formMap);
            formMap.getMapControl().setTrackMode(TrackMode.TRACK);
            TrackingLayer trackingLayer = formMap.getMapControl().getMap().getTrackingLayer();
            for (int i = trackingLayer.getCount() - 1; i >= 0; i--) {
                if (trackingLayer.getTag(i).startsWith("MapMeasureTrackingObject"))
                    trackingLayer.remove(i);
            }
            formMap.getMapControl().setAction(Action.SELECT2);
            formMap.getMapControl().getMap().refreshTrackingLayer();
            formMap.getMapControl().setTrackMode(TrackMode.EDIT);
            ToolbarUIUtilities.updataToolbarsState();
        }
    }

    public boolean enable() {
        boolean enable = false;
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        if (activeForm instanceof IFormMap) {
            IFormMap formMap = (IFormMap)activeForm;
            TrackingLayer trackingLayer = formMap.getMapControl().getMap().getTrackingLayer();
            for (int i = trackingLayer.getCount() - 1; i >= 0; i--) {
                if (trackingLayer.getTag(i).startsWith("MapMeasureTrackingObject")) {
                    enable = true;
                    break;
                }
            }
        }
        return enable;
    }
}