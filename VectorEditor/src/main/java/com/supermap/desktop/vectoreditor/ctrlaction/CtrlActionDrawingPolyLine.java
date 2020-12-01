package com.supermap.desktop.vectoreditor.ctrlaction;

import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.geometryDrawing.GeometryDrawing;
import com.supermap.desktop.mapeditor.ctrlAction.geometryDrawing.Line.CtrlActionDrawingLineBase;
import com.supermap.desktop.mapeditor.geometryOperation.drawingImplement.Line.GeometryDrawingPolyLine;
import com.supermap.ui.Action;

public class CtrlActionDrawingPolyLine extends CtrlActionDrawingLineBase {
    public CtrlActionDrawingPolyLine(IBaseItem caller) {
        super(caller);
    }

    protected void run() {
        IFormMap formMap = (IFormMap)Application.getActiveApplication().getActiveForm();
        if (formMap != null && formMap.getMapControl().getDrawState().isDrawPolyLines()) {
            formMap.getMapControl().setAction(Action.CREATE_POLYLINE2_POLYLINE);
        } else {
            super.run();
        }
    }

    public GeometryDrawing getGeometryDrawing() {
        return (GeometryDrawing)new GeometryDrawingPolyLine();
    }

    public boolean check() {
        boolean isCheck = false;
        IFormMap formMap = (IFormMap)Application.getActiveApplication().getActiveForm();
        if (formMap != null)
            if (formMap.getMapControl().getDrawState().isDrawPolyLines()) {
                if (formMap.getMapControl().getAction() == Action.CREATE_POLYLINE2_POLYLINE) {
                    isCheck = true;
                } else {
                    isCheck = false;
                }
            } else {
                isCheck = super.check();
            }
        return isCheck;
    }

    public boolean enable() {
        Boolean result = Boolean.valueOf(false);
        IFormMap formMap = (IFormMap)Application.getActiveApplication().getActiveForm();
        if (formMap != null)
            if (formMap.getMapControl().getDrawState().isLineOptionEnable() && formMap.getMapControl().getDrawState().isDrawPolyLines()) {
                result = Boolean.valueOf(true);
            } else {
                result = Boolean.valueOf(super.enable());
            }
        return result.booleanValue();
    }
}
