package com.supermap.desktop.download.util;

import com.supermap.data.CursorType;
import com.supermap.data.Dataset;
import com.supermap.data.DatasetMosaic;
import com.supermap.data.DatasetType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Point2D;
import com.supermap.data.PrjCoordSysType;
import com.supermap.data.QueryParameter;
import com.supermap.data.Recordset;
import com.supermap.data.Rectangle2D;
import com.supermap.data.SpatialIndexType;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.dialog.SmDialogConfirm;
import com.supermap.desktop.controls.dialog.SmOptionPane;
import com.supermap.desktop.controls.ui.UICommonToolkit;
import com.supermap.desktop.controls.ui.controls.DialogResult;
import com.supermap.desktop.controls.ui.controls.progress.BuildSpatialIndexCallable;
import com.supermap.desktop.controls.ui.controls.progress.SmDialogProgress;
import com.supermap.desktop.controls.ui.controls.progress.SmDialogProgressTotal;
import com.supermap.desktop.controls.ui.controls.progress.SpatialIndexTableModelBean;
import com.supermap.desktop.controls.ui.controls.video.ARMapUtilities;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.GlobalParameters;
import com.supermap.desktop.core.Interface.IForm;
import com.supermap.desktop.core.Interface.IFormManager;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.datasetused.DatasetUsedMonitor;
import com.supermap.desktop.core.enums.WindowType;
import com.supermap.desktop.core.progress.callable.CreateImagePyramidCallable;
import com.supermap.desktop.core.properties.CoreProperties;
import com.supermap.desktop.core.uicolorscheme.MapLineLayerScheme;
import com.supermap.desktop.core.uicolorscheme.MapPointLayerScheme;
import com.supermap.desktop.core.uicolorscheme.MapRegionLayerScheme;
import com.supermap.desktop.core.utilties.FileUtilities;
import com.supermap.desktop.core.utilties.FormUtilities;
import com.supermap.desktop.core.utilties.ImagePyramidUtilities;
import com.supermap.desktop.core.utilties.MapUtilities;
import com.supermap.desktop.core.utilties.PathUtilities;
import com.supermap.desktop.core.utilties.SortUtilities;
import com.supermap.desktop.core.utilties.SpatialIndexTypeUtilities;
import com.supermap.desktop.core.utilties.StringUtilities;
import com.supermap.desktop.core.utilties.SymbolUtilties;
import com.supermap.desktop.core.utilties.TabularUtilities;
import com.supermap.desktop.core.utilties.ThreadUtilties;
import com.supermap.desktop.core.video.LayerVideo;
import com.supermap.mapping.Layer;
import com.supermap.mapping.LayerMosaicGroup;
import com.supermap.mapping.LayerSettingGrid;
import com.supermap.mapping.LayerSettingImage;
import com.supermap.mapping.LayerSettingVector;
import com.supermap.mapping.Map;
import com.supermap.ui.Action;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.SwingUtilities;

public class MapBrowseViewUIUtilities {
    public static int CHINACENTERRANGEVALUEX = 107;
    public static int CHINACENTERRANGEVALUEY = 35;
    public static double INITSCALE = 2.0E-8D;

    public MapBrowseViewUIUtilities() {}

    public static Layer[] addDatasetsToMap(Map map, int index, Dataset... datasets) {
        Vector layers = new Vector();
        if (datasets != null && datasets.length != 0) {
            ArrayList needCreateImagePyramid = new ArrayList();
            boolean isUsedAsDefault = false;
            SmDialogConfirm dialogConfirm = new SmDialogConfirm();
            dialogConfirm.setShowCheckBox(datasets.length > 1);
            ArrayList needCreateSpatialIndex = new ArrayList();
            boolean isUsedAsDefaultForSpatialIndex = false;
            SmDialogConfirm dialogConfirmForSpatialIndex = new SmDialogConfirm();
            dialogConfirmForSpatialIndex.setShowCheckBox(datasets.length > 1);
            Dataset[] var10 = datasets;
            int var11 = datasets.length;

            int var12;
            Dataset dataset;
            for(var12 = 0; var12 < var11; ++var12) {
                dataset = var10[var12];
                if (ImagePyramidUtilities.isNeedBuildPyramid(dataset)) {
                    dialogConfirm.setMessage(MessageFormat.format(ControlsProperties.getString("String_IsBuildPyramid"), dataset.getName()));
                    if (!isUsedAsDefault) {
                        dialogConfirm.showDialog();
                        isUsedAsDefault = dialogConfirm.isUsedAsDefault();
                    }

                    if (dialogConfirm.getDialogResult() == DialogResult.OK) {
                        needCreateImagePyramid.add(dataset);
                    }
                } else if (isNeedCreateSpatialIndex(dataset)) {
                    dialogConfirmForSpatialIndex.setMessage(MessageFormat.format(ControlsProperties.getString("String_IsBuildSpatialIndex"), dataset.getName()));
                    if (!isUsedAsDefaultForSpatialIndex) {
                        dialogConfirmForSpatialIndex.showDialog();
                        isUsedAsDefaultForSpatialIndex = dialogConfirmForSpatialIndex.isUsedAsDefault();
                    }

                    if (dialogConfirmForSpatialIndex.getDialogResult() == DialogResult.OK) {
                        needCreateSpatialIndex.add(dataset);
                    }
                }
            }

            if (!needCreateImagePyramid.isEmpty()) {
                SmDialogProgressTotal smDialogProgressTotal = new SmDialogProgressTotal(ControlsProperties.getString("String_BuildDatasetPyramid"));
                smDialogProgressTotal.doWork(new CreateImagePyramidCallable((Dataset[])needCreateImagePyramid.toArray(new Dataset[0])));
            }

            createSpatialIndex(needCreateSpatialIndex);
            SortUtilities.sortList(datasets, new Comparator<Dataset>() {
                public int compare(Dataset o1, Dataset o2) {
                    return SortUtilities.compareDatasetByTypeAndName(o1, o2);
                }
            });
            var10 = datasets;
            var11 = datasets.length;

            for(var12 = 0; var12 < var11; ++var12) {
                dataset = var10[var12];
                if (dataset.getType() != DatasetType.TABULAR && dataset.getType() != DatasetType.TOPOLOGY) {
                    layers.add(insertDatasetToMap(map, dataset, index));
                }
            }

            MapUtilities.setDynamic(datasets, map);
            Application.getActiveApplication().resetActiveForm();
            map.refresh();
            UICommonToolkit.getLayersManager().setMap(map);
            UICommonToolkit.getLayersManager().getLayersTree().setExpandLayerGroup(map, UICommonToolkit.getLayersManager().getLayersTree().getExpandLayerGroup(map));
            DatasetUsedMonitor.addUsing(map, (Object)null, datasets);
            return (Layer[])layers.toArray(new Layer[0]);
        } else {
            return null;
        }
    }

    public static boolean isNeedCreateSpatialIndex(Dataset dataset) {
        boolean result = false;
        if (dataset instanceof DatasetVector) {
            DatasetVector datasetVector = (DatasetVector)dataset;
            result = !datasetVector.getDatasource().isReadOnly() && datasetVector.getSpatialIndexType() == SpatialIndexType.NONE && datasetVector.getRecordCount() > 2000;
        }

        return result;
    }

    public static Layer insertDatasetToMap(Map map, Dataset dataset, int index) {
        Object layer = null;

        try {
            if (dataset != null) {
                int i;
                if (dataset.getType() == DatasetType.MOSAIC) {
                    if (-1 != index) {
                        layer = map.getLayers().insertMosaicGroup(index, (DatasetMosaic)dataset);
                    } else {
                        layer = map.getLayers().addMosaicGroup((DatasetMosaic)dataset, true);
                    }

                    LayerMosaicGroup layerMosaicGroup = (LayerMosaicGroup)layer;

                    for(i = 0; i < layerMosaicGroup.getCount(); ++i) {
                        layerMosaicGroup.get(i).setSelectable(false);
                    }
                } else if (0 <= index) {
                    if (ARMapUtilities.isVideoDataset(dataset)) {
                        layer = new LayerVideo(0L, (DatasetVector)dataset, MapRegionLayerScheme.getRegionColor(), MapRegionLayerScheme.getBorderLineColor());
                        map.getLayers().insert(0, (Layer)layer);
                    } else {
                        layer = map.getLayers().insert(index, dataset);
                    }
                } else if (!StringUtilities.isNullOrEmpty(dataset.getExtInfo()) && !dataset.getExtInfo().equalsIgnoreCase("null")) {
                    layer = new LayerVideo(0L, (DatasetVector)dataset, MapRegionLayerScheme.getRegionColor(), MapRegionLayerScheme.getBorderLineColor());
                    map.getLayers().add((Layer)layer);
                } else {
                    layer = map.getLayers().add(dataset, true);
                }

                if (layer != null && !((Layer)layer).isDisposed()) {
                    if (!ARMapUtilities.isVideoDataset(dataset)) {
                        LayerSettingVector setting;
                        if (dataset.getType() != DatasetType.REGION && dataset.getType() != DatasetType.REGION3D && dataset.getType() != DatasetType.PARAMETRICREGION && dataset.getType() != DatasetType.MOSAIC) {
                            if (dataset.getType() == DatasetType.LINE || dataset.getType() == DatasetType.NETWORK || dataset.getType() == DatasetType.NETWORK3D || dataset.getType() == DatasetType.PARAMETRICLINE || dataset.getType() == DatasetType.LINEM || dataset.getType() == DatasetType.LINE3D) {
                                if (dataset.getType() != DatasetType.NETWORK && dataset.getType() != DatasetType.NETWORK3D) {
                                    setting = (LayerSettingVector)((Layer)layer).getAdditionalSetting();
                                    setting.getStyle().setLineColor(MapLineLayerScheme.getLineColor());
                                } else {
                                    setting = (LayerSettingVector)((Layer)layer).getAdditionalSetting();
                                    setting.getStyle().setLineColor(MapLineLayerScheme.getNetworkLineColor());
                                    map.getLayers().add(((DatasetVector)dataset).getChildDataset(), true);

                                    for(i = 0; i < 2; ++i) {
                                        Layer tempLayer = map.getLayers().get(i);
                                        if (tempLayer.getDataset() == ((DatasetVector)dataset).getChildDataset()) {
                                            LayerSettingVector nodeLayerSetting = (LayerSettingVector)tempLayer.getAdditionalSetting();
                                            nodeLayerSetting.getStyle().setLineColor(MapLineLayerScheme.getLineColor());
                                        }
                                    }
                                }
                            } else if (dataset.getType() != DatasetType.POINT && dataset.getType() != DatasetType.POINT3D) {
                                if (dataset.getType() == DatasetType.GRID) {
                                    ((LayerSettingGrid)((Layer)layer).getAdditionalSetting()).setSpecialValueTransparent(true);
                                } else if (dataset.getType() == DatasetType.IMAGE) {
                                    ((LayerSettingImage)((Layer)layer).getAdditionalSetting()).setSpecialValueTransparent(true);
                                }
                            } else {
                                setting = (LayerSettingVector)((Layer)layer).getAdditionalSetting();
                                setting.getStyle().setLineColor(MapPointLayerScheme.getPointColor());
                            }
                        } else {
                            setting = (LayerSettingVector)((Layer)layer).getAdditionalSetting();
                            setting.getStyle().setFillForeColor(MapRegionLayerScheme.getRegionColor());
                            setting.getStyle().setLineColor(MapRegionLayerScheme.getBorderLineColor());
                        }
                    }
                } else {
                    Application.getActiveApplication().getOutput().output(String.format(CoreProperties.getString("String_DatasetOpenFaild"), dataset.getName()));
                }
            }
        } catch (Exception var8) {
            Application.getActiveApplication().getOutput().output(var8);
        }

        if (GlobalParameters.isAntialiasing() && layer != null) {
            ((Layer)layer).setAntialias(true);
        }

        return (Layer)layer;
    }

    public static void addDatasetsToNewWindow(Dataset... datasets) {
        ArrayList datasetsToMap = new ArrayList();
        Dataset[] var2 = datasets;
        int var3 = datasets.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Dataset dataset = var2[var4];
            if (dataset.getType() == DatasetType.TABULAR) {
                TabularUtilities.openTabularInNewForm(dataset);
            } else if (dataset.getType() != DatasetType.LINKTABLE && dataset.getType() != DatasetType.TOPOLOGY) {
                datasetsToMap.add(dataset);
            }
        }

        if (!datasetsToMap.isEmpty()) {
            String name = MapUtilities.getAvailableMapName(MessageFormat.format("{0}@{1}", ((Dataset)datasetsToMap.get(0)).getName(), ((Dataset)datasetsToMap.get(0)).getDatasource().getAlias()), true);
            IFormMap formMap = (IFormMap)FormUtilities.fireNewWindowEvent(WindowType.MAP, "在线地图");
            Map map = formMap.getMapControl().getMap();
            map.setFullLabel(true);
            map.setCompatibleFontHeight(true);
            addDatasetsToMap(map, 0, (Dataset[])datasetsToMap.toArray(new Dataset[datasetsToMap.size()]));
            boolean isResetRange = isResetRange((Dataset[])datasetsToMap.toArray(new Dataset[datasetsToMap.size()]));
            if (isResetRange) {
                setMapRangeToChina(map);
            }
        }

    }

    public static void setMapRangeToChina(Map map) {
        map.setScale(INITSCALE);
        map.setCenter(new Point2D((double)CHINACENTERRANGEVALUEX, (double)CHINACENTERRANGEVALUEY));
    }

    public static Rectangle2D getDatasetsBounds(Dataset[] datasets) {
        Rectangle2D bounds = null;
        try {
            Dataset[] var2 = datasets;
            int var3 = datasets.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Dataset dataset = var2[var4];
                if (bounds == null) {
                    bounds = dataset.getBounds().clone();
                } else {
                    bounds.union(dataset.getBounds());
                }
            }
        } catch (Exception var6) {
            Application.getActiveApplication().getOutput().output(var6);
        }

        return bounds;
    }

    public static boolean isResetRange(Dataset... datasets) {
        if (datasets != null && datasets.length > 0) {
            Dataset[] var1 = datasets;
            int var2 = datasets.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                Dataset dataset = var1[var3];
                if (dataset.getPrjCoordSys() == null || dataset.getPrjCoordSys().getType() != PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE || Double.compare(dataset.getBounds().getTop(), 0.0D) != 0 || Double.compare(dataset.getBounds().getBottom(), 0.0D) != 0 || Double.compare(dataset.getBounds().getLeft(), 0.0D) != 0 || Double.compare(dataset.getBounds().getRight(), 0.0D) != 0) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static int selectAllGeometry(IFormMap formMap) {
        int count = 0;

        try {
            ArrayList layers = MapUtilities.getLayers(formMap.getMapControl().getMap());
            Iterator var3 = layers.iterator();

            while(var3.hasNext()) {
                Layer layer = (Layer)var3.next();
                if (layer.isVisible() && layer.isSelectable()) {
                    DatasetVector dataset = (DatasetVector)layer.getDataset();
                    if (dataset != null) {
                        Recordset recordset = dataset.getRecordset(false, CursorType.STATIC);
                        layer.getSelection().fromRecordset(recordset);
                        count += dataset.getRecordCount();
                        recordset.dispose();
                    }
                }
            }

            formMap.getMapControl().getMap().refresh();
        } catch (Exception var7) {
            Application.getActiveApplication().getOutput().output(var7);
        }

        return count;
    }

    public static int getReverseSelectionCount(IFormMap formMap) {
        int count = 0;

        try {
            ArrayList layers = MapUtilities.getLayers(formMap.getMapControl().getMap());
            Iterator var3 = layers.iterator();

            while(true) {
                Layer layer;
                do {
                    do {
                        do {
                            if (!var3.hasNext()) {
                                formMap.getMapControl().getMap().refresh();
                                return count;
                            }

                            layer = (Layer)var3.next();
                        } while(!layer.isVisible());
                    } while(!layer.isSelectable());
                } while(layer.getSelection() == null);

                Recordset preRecordset = layer.getSelection().toRecordset();
                preRecordset.moveFirst();
                StringBuilder stringBuilder = new StringBuilder();

                while(!preRecordset.isEOF()) {
                    stringBuilder.append(preRecordset.getID()).append(",");
                    preRecordset.moveNext();
                }

                if (stringBuilder.length() > 0) {
                    stringBuilder.setLength(stringBuilder.length() - 1);
                }

                DatasetVector dataset = (DatasetVector)layer.getDataset();
                QueryParameter queryParameter = new QueryParameter();
                queryParameter.setAttributeFilter(MessageFormat.format("SmID not in ({0})", stringBuilder.toString()));
                Recordset recordset = dataset.query(queryParameter);
                if (recordset.getRecordCount() == 0) {
                    clearAllSelection(formMap);
                } else {
                    layer.getSelection().fromRecordset(recordset);
                }

                layer.getSelection().fromRecordset(recordset);
                count += recordset.getRecordCount();
                preRecordset.dispose();
                recordset.dispose();
            }
        } catch (Exception var10) {
            Application.getActiveApplication().getOutput().output(var10);
            return count;
        }
    }

    public static int clearAllSelection(IFormMap formMap) {
        if (formMap != null && formMap.getMapControl() != null && formMap.getMapControl().getMap() != null) {
            try {
                ArrayList layers = MapUtilities.getLayers(formMap.getMapControl().getMap());
                Iterator var2 = layers.iterator();

                while(var2.hasNext()) {
                    Layer layer = (Layer)var2.next();
                    if (layer.getSelection() != null && layer.getSelection().getCount() > 0) {
                        layer.getSelection().clear();
                    }
                }
            } catch (Exception var4) {
                Application.getActiveApplication().getOutput().output(var4);
            }

            formMap.getMapControl().getMap().refresh();
            return 0;
        } else {
            return 0;
        }
    }

    public static int calculateSelectNumber(IFormMap formMap) {
        ArrayList layers = MapUtilities.getLayers(formMap.getMapControl().getMap());
        int count = 0;
        Iterator var3 = layers.iterator();

        while(var3.hasNext()) {
            Layer layer = (Layer)var3.next();
            if (layer.getSelection() != null && layer.getSelection().getCount() > 0) {
                count += layer.getSelection().getCount();
            }
        }

        return count;
    }

    public static boolean openMap(String mapName) {
        return openMapAndReturn(mapName) != null;
    }

    public static IFormMap openMapAndReturn(String mapName) {
        if (!SwingUtilities.isEventDispatchThread()) {
            return (IFormMap)ThreadUtilties.doInEDTThread(() -> {
                return openMapAndReturn(mapName);
            });
        } else {
            int index = -1;
            IFormManager formManager = Application.getActiveApplication().getMainFrame().getFormManager();

            for(int i = 0; i < formManager.getCount(); ++i) {
                if (!StringUtilities.isNullOrEmpty(formManager.get(i).getTitle()) && formManager.get(i).getTitle().equals(mapName) && formManager.get(i).getWindowType() == WindowType.MAP) {
                    index = i;
                    break;
                }
            }

            if (index != -1) {
                IForm form = formManager.get(index);
                formManager.setActiveForm(form);
                return (IFormMap)form;
            } else {
                IFormMap formMap = (IFormMap)FormUtilities.fireNewWindowEvent(WindowType.MAP, mapName, true);
                if (formMap != null && formMap.getMapControl().getMap().getLayers().getCount() > 0) {
                    formMap.setActiveLayers(new Layer[]{formMap.getMapControl().getMap().getLayers().get(0)});
                }

                return formMap;
            }
        }
    }

    public static boolean refreshCurrentMap() {
        boolean issuccss = false;
        IForm activeForm = Application.getActiveApplication().getActiveForm();
        if (activeForm instanceof IFormMap) {
            try {
                ((IFormMap)activeForm).getMapControl().getMap().refresh();
                issuccss = true;
            } catch (Exception var3) {
                return issuccss;
            }
        }

        return issuccss;
    }

    private static void a(String datasourceName) {
        BufferedReader br = null;

        try {
            String symPath = PathUtilities.getFullPathName("../templates/MapTemplate/GJB.sym", false);
            if (FileUtilities.exists(symPath)) {
                SymbolUtilties.ImportMarkerLibraryGroup(symPath, true);
            }

            String mapTempPath = PathUtilities.getFullPathName("../templates/MapTemplate/GJBTemplate.xml", false);
            StringBuilder mapTemplateXml = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(new FileInputStream(mapTempPath), StandardCharsets.UTF_8));

            for(String s = ""; (s = br.readLine()) != null; mapTemplateXml.append(s)) {
                if (s.contains("GJB")) {
                    s = s.replace("GJB", datasourceName);
                }
            }

            IFormMap formMap = (IFormMap)FormUtilities.fireNewWindowEvent(WindowType.MAP);
            if (formMap != null) {
                Map map = formMap.getMapControl().getMap();
                map.fromXML(mapTemplateXml.toString());
                map.refresh();
                UICommonToolkit.getLayersManager().setMap(map);
                formMap.getMapControl().setAction(Action.PAN);
                String availableMapName = MapUtilities.getAvailableMapName(map.getName(), true);
                formMap.setTitle(availableMapName);
                formMap.getMapControl().getMap().setName(availableMapName);
                Application.getActiveApplication().getMainFrame().getFormManager().resetActiveForm();
            }
        } catch (Exception var17) {
            Application.getActiveApplication().getOutput().output(var17);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException var16) {
                    Application.getActiveApplication().getOutput().output(var16);
                }
            }

        }

    }

    public static void checkoutSpatialIndex(Map map) {
        ArrayList needCreateSpatialIndex = new ArrayList();
        SmOptionPane smOptionPane = new SmOptionPane();
        ArrayList layers = MapUtilities.getLayers(map);
        Iterator var4 = layers.iterator();

        while(var4.hasNext()) {
            Layer layer = (Layer)var4.next();
            if (isNeedCreateSpatialIndex(layer.getDataset())) {
                needCreateSpatialIndex.add(layer.getDataset());
            }
        }

        if (!needCreateSpatialIndex.isEmpty() && smOptionPane.showConfirmDialog(MessageFormat.format(ControlsProperties.getString("String_IsBuildSpatialIndexForMapCache"), needCreateSpatialIndex.size())) == 0) {
            createSpatialIndex(needCreateSpatialIndex);
        }

    }

    public static void createSpatialIndex(ArrayList<Dataset> needCreateSpatialIndex) {
        if (!needCreateSpatialIndex.isEmpty()) {
            List tableModelBeans = new ArrayList();
            Iterator var2 = needCreateSpatialIndex.iterator();

            while(var2.hasNext()) {
                Dataset dataset = (Dataset)var2.next();
                SpatialIndexTableModelBean spatialIndexTableModelBean = new SpatialIndexTableModelBean(dataset);
                SpatialIndexType spatialIndexType = SpatialIndexTypeUtilities.getSupportSpatialIndexTypeNotNone(dataset);
                if (spatialIndexType != SpatialIndexType.NONE) {
                    spatialIndexTableModelBean.setSpatialIndexType(SpatialIndexTypeUtilities.getSupportSpatialIndexTypeNotNone(dataset));
                    tableModelBeans.add(spatialIndexTableModelBean);
                }
            }

            SmDialogProgress smDialogProgress = new SmDialogProgress(ControlsProperties.getString("String_CreateSpatialIndex"));
            smDialogProgress.doWork(new BuildSpatialIndexCallable(tableModelBeans));
        }

    }

    public static IFormMap openMapTemplate(String mapTemplateXml) {
        if (StringUtilities.isNullOrEmpty(mapTemplateXml)) {
            return null;
        } else {
            IFormMap formMap = (IFormMap)FormUtilities.fireNewWindowEvent(WindowType.MAP);
            if (formMap != null) {
                Map map = formMap.getMapControl().getMap();
                if (map.fromXML(mapTemplateXml)) {
                    map.setModified(true);
                    map.refresh();
                    formMap.getMapControl().setAction(Action.PAN);
                    String availableMapName = MapUtilities.getAvailableMapName(map.getName(), true);
                    formMap.setTitle(availableMapName);
                    formMap.getMapControl().getMap().setName(availableMapName);
                    UICommonToolkit.getLayersManager().setMap(map);
                    Application.getActiveApplication().getMainFrame().getFormManager().resetActiveForm();
                    return formMap;
                }
            }

            return null;
        }
    }
}
