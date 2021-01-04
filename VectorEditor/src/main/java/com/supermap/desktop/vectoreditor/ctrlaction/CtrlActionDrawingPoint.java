package com.supermap.desktop.vectoreditor.ctrlaction;

import com.supermap.data.DatasetType;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.geometryDrawing.GeometryDrawing;
import com.supermap.desktop.mapeditor.ctrlAction.geometryDrawing.CtrlActionDrawingBase;
import com.supermap.desktop.mapeditor.geometryOperation.drawingImplement.GeometryDrawingPoint;

public class CtrlActionDrawingPoint extends CtrlActionDrawingBase {
    public CtrlActionDrawingPoint(IBaseItem caller) {
        super(caller);
    }

    protected DatasetType[] getTargetDatasetType() {
        return new DatasetType[]{DatasetType.CAD, DatasetType.POINT, DatasetType.POINT3D};
    }

    public GeometryDrawing getGeometryDrawing() {
        return (GeometryDrawing) new GeometryDrawingPoint();
    }
}
