package com.supermap.desktop.download.util;

import com.supermap.data.*;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.enums.EngineTypeExtend;
import com.supermap.desktop.core.utilties.CursorUtilities;

import java.text.MessageFormat;

public class DatasourceOpenOnlineMapUtilties {
    public DatasourceOpenOnlineMapUtilties() {
    }

    public static boolean openGoogelMap(String webMapName, String mapType) {
        boolean result = false;
        clearMapLayer();
        try {
            CursorUtilities.setWaitCursor();
            if (webMapName.equals("Google")) {
                if (mapType.equals("路网地图")) {
                    result = LoadMap(ControlsProperties.getString("String_GoogleMaps"), EngineType.GOOGLEMAPS, 0);
                } else if (mapType.equals("卫星地图")) {
                    result = LoadMap(ControlsProperties.getString("String_GoogleMaps"), EngineType.GOOGLEMAPS, 1);
                } else if (mapType.equals("地形地图")) {
                    result = LoadMap(ControlsProperties.getString("String_GoogleMaps"), EngineType.GOOGLEMAPS, 2);
                }
            } else if (webMapName.equals("Baidu")) {
                if (mapType.equals("路网地图")) {
                    result = LoadMap(ControlsProperties.getString("String_BaiduMap"), EngineType.BAIDUMAPS, 0);
                } else if (mapType.equals("卫星地图")) {
                    result = LoadMap(ControlsProperties.getString("String_BaiduMap"), EngineType.BAIDUMAPS, 1);
                }
            }

        } catch (Exception var1) {
            Application.getActiveApplication().getOutput().output(var1);
        } finally {
            CursorUtilities.setDefaultCursor();
        }
        return result;
    }

    public static boolean LoadMap(String alias, EngineType enginType, int mapIndex) {
        boolean result = false;
        try {
            Workspace workspace = Application.getActiveApplication().getWorkspace();
            for (int index = 1; workspace.getDatasources().contains(alias); ++index) {
                alias = alias + "_" + index;
            }
            DatasourceConnectionInfo datasourceConnectionInfo = new DatasourceConnectionInfo();
            datasourceConnectionInfo.setAlias(alias);
            datasourceConnectionInfo.setReadOnly(true);
            datasourceConnectionInfo.setEngineType(enginType);
            if (enginType == EngineType.SUPERMAPCLOUD) {
                datasourceConnectionInfo.setServer(ControlsProperties.getString("String_SuperMapCloudServer"));
            } else if (enginType == EngineType.BAIDUMAPS) {
                datasourceConnectionInfo.setServer(ControlsProperties.getString("String_BaiduMapServer"));
            } else if (enginType == EngineType.GOOGLEMAPS) {
                datasourceConnectionInfo.setServer(ControlsProperties.getString("String_GoogleMapsForChinaServer"));
            } else if (enginType == EngineType.OPENSTREETMAPS) {
                datasourceConnectionInfo.setServer(ControlsProperties.getString("String_OpenStreetMapsServer"));
            } else if (enginType == EngineType.OGC) {
                datasourceConnectionInfo.setServer(ControlsProperties.getString("String_MapWorld_VECCName_Service"));
                datasourceConnectionInfo.setDriver("WMTS");
            } else if (enginType == EngineTypeExtend.ChinaRS) {
                datasourceConnectionInfo.setEngineType(EngineType.ISERVERREST);
                datasourceConnectionInfo.setServer(ControlsProperties.getString("String_ChinaRS_gf1_Service"));
            } else if (enginType == EngineTypeExtend.WordTerrainBase) {
                datasourceConnectionInfo.setEngineType(EngineType.ISERVERREST);
                datasourceConnectionInfo.setServer(ControlsProperties.getString("String_WordTerrainBase_Service"));
            } else if (enginType == EngineTypeExtend.WordTerrainBlueDark) {
                datasourceConnectionInfo.setEngineType(EngineType.ISERVERREST);
                datasourceConnectionInfo.setServer(ControlsProperties.getString("String_WordTerrainBlueDark_Service"));
            }
            Datasource datasource = null;
            try {
                datasource = workspace.getDatasources().open(datasourceConnectionInfo);
            } catch (Exception var9) {
                var9.printStackTrace();
                Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_OpenDatasourceFaildByCheckAndError"), var9.getMessage()));
            }
            int iii = Application.getActiveApplication().getMainFrame().getFormManager().getCount();
            ;
            if (datasource != null) {
                result = true;
                if (datasource.getDatasets().getCount() > 0) {
                    Dataset dataset = datasource.getDatasets().get(mapIndex);
                    if (null != dataset) {
                        if (Application.getActiveApplication().getMainFrame().getFormManager().getCount() > 0) {
                            IFormMap formMap = (IFormMap) Application.getActiveApplication().getMainFrame().getFormManager().get(0);
                            if (!formMap.getMapControl().getMap().getPrjCoordSys().equals(dataset.getPrjCoordSys())) {
                                formMap.getMapControl().getMap().setPrjCoordSys(dataset.getPrjCoordSys());
                            }
                            MapBrowseViewUIUtilities.addDatasetsToMap(formMap.getMapControl().getMap(), 0, new Dataset[]{dataset});
                        } else {
                            MapBrowseViewUIUtilities.addDatasetsToNewWindow(new Dataset[]{dataset});
                        }
                    }
                }
            }

        } catch (Exception var2) {
            Application.getActiveApplication().getOutput().output(var2);
        }
        return result;
    }

    public static void clearMapLayer() {
        if (Application.getActiveApplication().getMainFrame().getFormManager().getCount() > 0) {
            IFormMap formMap = (IFormMap) Application.getActiveApplication().getMainFrame().getFormManager().get(0);
            formMap.getMapControl().getMap().getLayers().clear();
        }
    }
}
