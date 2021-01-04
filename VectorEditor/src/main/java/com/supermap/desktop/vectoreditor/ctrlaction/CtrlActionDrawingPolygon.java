package com.supermap.desktop.vectoreditor.ctrlaction;

import com.supermap.data.DatasetType;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.geometryDrawing.GeometryDrawing;
import com.supermap.desktop.mapeditor.ctrlAction.geometryDrawing.CtrlActionDrawingBase;
import com.supermap.desktop.mapeditor.geometryOperation.drawingImplement.Polygon.GeometryDrawingPolygon;

public class CtrlActionDrawingPolygon extends CtrlActionDrawingBase {
    public CtrlActionDrawingPolygon(IBaseItem caller) {
        super(caller);
    }

    protected DatasetType[] getTargetDatasetType() {
        return new DatasetType[]{DatasetType.CAD, DatasetType.LINE, DatasetType.LINEM, DatasetType.LINE3D, DatasetType.NETWORK, DatasetType.NETWORK3D, DatasetType.REGION, DatasetType.REGION3D, DatasetType.MOSAIC};
    }

    public GeometryDrawing getGeometryDrawing() {
        return (GeometryDrawing) new GeometryDrawingPolygon();
    }
}
