package com.supermap.desktop.download.ctrlaction;

import com.supermap.data.*;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.*;
import com.supermap.desktop.core.geometryDrawing.DrawingParameterDimension;
import com.supermap.desktop.core.geometryDrawing.DrawingTrackMode;
import com.supermap.desktop.core.geometryDrawing.GeometryDrawing;
import com.supermap.desktop.core.geometryDrawing.GeometryDrawingType;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.core.implement.SmRibbonComboBox;
import com.supermap.desktop.core.utilties.CtrlActionUtilities;
import com.supermap.desktop.download.DownLoadProperties;
import com.supermap.desktop.mapeditor.PluginEnvironment;
import com.supermap.desktop.mapeditor.geometryOperation.drawingImplement.Rectangle.GeometryDrawingRectangle;
import com.supermap.desktop.mapeditor.geometryOperation.editor.IEditor;
import com.supermap.desktop.mapeditor.geometryOperation.editor.NullEditor;
import com.supermap.mapping.Layer;

public class CtrlActionDrawingRectangle extends CtrlAction {
    protected GeometryDrawing geometryDrawing;
    private GeometryDrawingType a;

    public CtrlActionDrawingRectangle(IBaseItem caller) {
        super(caller);
        this.a = GeometryDrawingType.NULL;
    }

    protected void run() {
        try {
            IFormMap formMap = (IFormMap)Application.getActiveApplication().getActiveForm();
            Datasource ds = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_MemoryDsAlias"));
            DatasetVector dv = (DatasetVector)ds.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
            Recordset rs = dv.getRecordset(false, CursorType.DYNAMIC);
            rs.deleteAll();
            if(rs != null) {rs.close();rs.dispose();}
            formMap.getMapControl().getMap().refresh();
            this.geometryDrawing = getGeometryDrawing();
            if (this.geometryDrawing != null && this.geometryDrawing
                    .equals(formMap.getGeometryDrawingManager().getCurrentGeometryDrawing())) {
                formMap.getGeometryDrawingManager().setCurrentGeometryDrawing(null);
                this.geometryDrawing = null;
            } else {
                formMap.getGeometryDrawingManager().setDrawingTrackMode(DrawingTrackMode.Edit);
                this.a = this.geometryDrawing.getDrawingType();
                PluginEnvironment.getGeometryEditManager().instance().activateEditor((IEditor) NullEditor.INSTANCE);
                formMap.getGeometryDrawingManager().setCurrentGeometryDrawing(this.geometryDrawing);
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }

    protected GeometryDrawing getGeometryDrawing() {
        return (GeometryDrawing)new GeometryDrawingRectangle();
    }

    protected DatasetType[] getTargetDatasetType() {
        return new DatasetType[] { DatasetType.CAD, DatasetType.LINE, DatasetType.LINEM, DatasetType.LINE3D, DatasetType.REGION, DatasetType.REGION3D };
    }

    protected DrawingParameterDimension getDimension() {
        DrawingParameterDimension dimension = DrawingParameterDimension.TwoDimension;
        try {
            IFormMap formMap = (IFormMap)Application.getActiveApplication().getActiveForm();
            Layer layer = formMap.getMapControl().getActiveEditableLayer();
            DatasetType[] targetDatasetType = getTargetDatasetType();
            if (layer != null && targetDatasetType != null)
                for (DatasetType type : targetDatasetType) {
                    if (layer.getDataset().getType() == type && (
                            type == DatasetType.POINT3D || type == DatasetType.LINE3D || type == DatasetType.NETWORK3D || type == DatasetType.REGION3D)) {
                        dimension = DrawingParameterDimension.ThreeDimension;
                        break;
                    }
                }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
        return dimension;
    }

    public boolean enable() {
        Boolean result = Boolean.valueOf(true);
        try {
            IForm formMap = Application.getActiveApplication().getActiveForm();
            if (formMap instanceof IFormMap) {
                Layer[] activeLayers = ((IFormMap)formMap).getActiveLayers();
                if (null != activeLayers && activeLayers.length > 0) {
                    Layer layer = activeLayers[0];
                    DatasetType[] targetDatasetType = getTargetDatasetType();
                    if (layer != null && layer.isEditable() && targetDatasetType != null)
                        for (DatasetType type : targetDatasetType) {
                            if (layer instanceof com.supermap.mapping.LayerMosaicGroup && type == DatasetType.MOSAIC) {
                                result = Boolean.valueOf(true);
                                break;
                            }
                            if (layer.getDataset() != null && layer.getDataset().getType() == type) {
                                result = Boolean.valueOf(true);
                                break;
                            }
                        }
                }
            }
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
        return result.booleanValue();
    }

    public boolean check() {
        boolean result = false;
        IForm formMap = Application.getActiveApplication().getActiveForm();
        if (formMap instanceof IFormMap) {
            this.geometryDrawing = ((IFormMap)formMap).getGeometryDrawingManager().getCurrentGeometryDrawing();
            if (this.geometryDrawing != null && this.geometryDrawing
                    .getDrawingType().equals(this.a) && ((IFormMap)formMap)
                    .getGeometryDrawingManager().getDrawingTrackMode() == DrawingTrackMode.Edit)
                result = true;
        }
        return result;
    }
}
