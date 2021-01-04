package com.supermap.desktop.download.util;

import com.supermap.data.*;
import com.supermap.desktop.controls.ControlsProperties;
import com.supermap.desktop.controls.ui.UICommonToolkit;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IForm;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.enums.EngineTypeExtend;
import com.supermap.desktop.core.utilties.Convert;
import com.supermap.desktop.core.utilties.CursorUtilities;
import com.supermap.desktop.download.DownLoadProperties;
import org.opengis.metadata.spatial.Georectified;
import thredds.inventory.TimedCollection;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

public class OpenTdtMapUtilties {
    private static WebMapInfo showMaps = new WebMapInfo();

    public static boolean openOnlineMap(String webMapName, int mapIndex){
        boolean result = false;
        try {
            CursorUtilities.setWaitCursor();
            if(webMapName.equals(DownLoadProperties.getString("String_TdtWgsMaps"))){
                result = initDatasource(DownLoadProperties.getString("String_TdtWgsMaps"),EngineType.OGC,mapIndex);
            }else if(webMapName.equals(DownLoadProperties.getString("String_TdtWebMaps"))){
                result = initDatasource(DownLoadProperties.getString("String_TdtWebMaps"),EngineType.OGC,mapIndex);
            }
        } catch (Exception var1){
            Application.getActiveApplication().getOutput().output(var1);
        } finally {
            CursorUtilities.setDefaultCursor();
        }
        return result;
    }

    public static boolean initDatasource(String alias, EngineType enginType, int mapIndex){
        boolean result = false;
        showMaps.setBaseDatasourceAlias(alias + mapIndex);
        showMaps.setMarkDatasourceAlias(alias + (mapIndex + 1));
        try {
            Workspace workspace = Application.getActiveApplication().getWorkspace();
            DatasourceConnectionInfo base_conn = null;
            DatasourceConnectionInfo mark_conn = null;
            Datasource base_ds = null;
            Datasource mark_ds = null;
            boolean bb = GlobalFactory.getInstance().getCurrentShowWebMap().getBaseDatasourceAlias().contains("Tdt");
            if(!GlobalFactory.getInstance().getCurrentShowWebMap().getBaseDatasourceAlias().contains("Tdt")){
                if(!GlobalFactory.getInstance().getCurrentShowWebMap().getBaseDatasourceAlias().equals(showMaps.getBaseDatasourceAlias())){
                    String currentBaseAlias = GlobalFactory.getInstance().getCurrentShowWebMap().getBaseDatasourceAlias();
                    if (workspace.getDatasources().contains(currentBaseAlias))
                        workspace.getDatasources().close(currentBaseAlias);
                    String currentMarkAlias = GlobalFactory.getInstance().getCurrentShowWebMap().getMarkDatasourceAlias();
                    if (workspace.getDatasources().contains(currentMarkAlias))
                        workspace.getDatasources().close(currentMarkAlias);
                }
            }
            if(workspace.getDatasources().contains(showMaps.getBaseDatasourceAlias())){
                base_ds = workspace.getDatasources().get(showMaps.getBaseDatasourceAlias());
                mark_ds = workspace.getDatasources().get(showMaps.getMarkDatasourceAlias());
            }else {
                base_conn = new DatasourceConnectionInfo();
                base_conn.setAlias(showMaps.getBaseDatasourceAlias());
                base_conn.setReadOnly(true);
                base_conn.setEngineType(enginType);
                mark_conn = new DatasourceConnectionInfo();
                mark_conn.setAlias(showMaps.getMarkDatasourceAlias());
                mark_conn.setReadOnly(true);
                mark_conn.setEngineType(enginType);
                if (enginType == EngineType.OGC) {
                    if(alias.equals("TdtWebMaps")){
                        if(mapIndex == 0) {
                            base_conn.setServer("http://t0.tianditu.com/vec_w/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                            mark_conn.setServer("http://t0.tianditu.com/cva_w/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                        }else if (mapIndex == 2) {
                            base_conn.setServer("http://t0.tianditu.com/img_w/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                            mark_conn.setServer("http://t0.tianditu.com/cia_w/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                        }else if(mapIndex == 4) {
                            base_conn.setServer("http://t0.tianditu.com/ter_w/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                            mark_conn.setServer("http://t0.tianditu.com/cta_w/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                        }
                        base_conn.setDriver("WMTS");
                        mark_conn.setDriver("WMTS");
                    }else if(alias.equals("TdtWgsMaps")){
                        if(mapIndex == 0) {
                            base_conn.setServer("http://t0.tianditu.com/vec_c/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                            mark_conn.setServer("http://t0.tianditu.com/cva_c/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                        }else if (mapIndex == 2) {
                            base_conn.setServer("http://t0.tianditu.com/img_c/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                            mark_conn.setServer("http://t0.tianditu.com/cia_c/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                        }else if(mapIndex == 4) {
                            base_conn.setServer("http://t0.tianditu.com/ter_c/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                            mark_conn.setServer("http://t0.tianditu.com/cta_c/wmts?DPI=96&tk=4b69da65541e7ba49088d2478fa08ae2");
                        }
                        base_conn.setDriver("WMTS");
                        mark_conn.setDriver("WMTS");
                    }
                }
                base_ds = workspace.getDatasources().open(base_conn);
                mark_ds = workspace.getDatasources().open(mark_conn);
            }
            List<Datasource> ds_list = new ArrayList<>();
            ds_list.add(base_ds);
            ds_list.add(mark_ds);
            result = LoadMap(ds_list, alias);
        }catch (Exception var9) {
            var9.printStackTrace();
            Application.getActiveApplication().getOutput().output(MessageFormat.format(ControlsProperties.getString("String_OpenDatasourceFaildByCheckAndError"), var9.getMessage()));
        }
        return result;
    }

    public static boolean LoadMap(List<Datasource> ds_list, String alias){
        boolean result = false;
        try {
            if(ds_list.get(0)!=null || ds_list.get(1)!=null){
                Dataset base_dv = ds_list.get(0).getDatasets().get(0);
                showMaps.setBaseDatasetName(base_dv.getName());
                showMaps.setMapPrj(GlobalFactory.getInstance().getWebMapPrj(alias));
                Dataset mark_dv = ds_list.get(1).getDatasets().get(0);
                showMaps.setMarkDatasetName(mark_dv.getName());
                if (null != base_dv){
                    if(getFormIndex("在线地图") == -1){
                        MapBrowseViewUIUtilities.addDatasetsToNewWindow(new Dataset[]{mark_dv,base_dv});
                        IFormMap formMap = (IFormMap)Application.getActiveApplication().getMainFrame().getFormManager().get(getFormIndex("在线地图"));
                        formMap.getMapControl().getMap().setPrjCoordSys(showMaps.getMapPrj());
                    }else {
                        IFormMap formMap = (IFormMap)Application.getActiveApplication().getMainFrame().getFormManager().get(getFormIndex("在线地图"));
                        if(formMap.getMapControl().getMap().getLayers().contains(GlobalFactory.getInstance().getCurrentShowWebMap().getBaseLayerName())) {
                            int index = formMap.getMapControl().getMap().getLayers().indexOf(GlobalFactory.getInstance().getCurrentShowWebMap().getBaseLayerName());
                            formMap.getMapControl().getMap().getLayers().remove(index);
                        }
                        if(!GlobalFactory.getInstance().getCurrentShowWebMap().getMarkLayerName().equals("@")) {
                            if (formMap.getMapControl().getMap().getLayers().contains(GlobalFactory.getInstance().getCurrentShowWebMap().getMarkLayerName())) {
                                formMap.getMapControl().getMap().getLayers().remove(GlobalFactory.getInstance().getCurrentShowWebMap().getMarkLayerName());
                            }
                        }
                        formMap.getMapControl().getMap().setPrjCoordSys(showMaps.getMapPrj());
                        formMap.getMapControl().getMap().getLayers().add(base_dv,true);
                        formMap.getMapControl().getMap().getLayers().add(mark_dv,true);
                    }
                    loadDownRegionLayer();
                    if(GlobalFactory.getInstance().getCurrentShowWebMap().getBaseDatasourceAlias().contains("Tdt")){
                        if(!GlobalFactory.getInstance().getCurrentShowWebMap().getBaseDatasourceAlias().equals(showMaps.getBaseDatasourceAlias())){
                            String currentBaseAlias = GlobalFactory.getInstance().getCurrentShowWebMap().getBaseDatasourceAlias();
                            if (Application.getActiveApplication().getWorkspace().getDatasources().contains(currentBaseAlias))
                                Application.getActiveApplication().getWorkspace().getDatasources().close(currentBaseAlias);
                            String currentMarkAlias = GlobalFactory.getInstance().getCurrentShowWebMap().getMarkDatasourceAlias();
                            if (Application.getActiveApplication().getWorkspace().getDatasources().contains(currentMarkAlias))
                                Application.getActiveApplication().getWorkspace().getDatasources().close(currentMarkAlias);
                        }
                    }
                    setFactory(showMaps);
                    IFormMap formMap =(IFormMap)Application.getActiveApplication().getMainFrame().getFormManager().get(getFormIndex("在线地图"));
                    Datasource memory_ds = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_MemoryDsAlias"));
                    DatasetVector memory_dv = (DatasetVector)memory_ds.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
                    Recordset memory_rs = memory_dv.getRecordset(false,CursorType.STATIC);
                    if(memory_rs.getRecordCount() > 0){
                        formMap.getMapControl().getMap().ensureVisible(memory_rs);
                        if(memory_rs != null) { memory_rs.close(); memory_rs.dispose();}
                        if(memory_dv != null) {memory_dv.close();}
                    }else {
                        formMap.getMapControl().getMap().viewEntire();
                    }
                    formMap.getMapControl().getMap().refresh();
                    UICommonToolkit.getWorkspaceManager().getWorkspaceTree().setSelectedDataset(memory_ds.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName")));
                    if(!formMap.getMapControl().getMap().getLayers().get(DownLoadProperties.getString("String_TempLayerName")).isEditable())
                        formMap.getMapControl().getMap().getLayers().get(DownLoadProperties.getString("String_TempLayerName")).setEditable(true);
                    result = true;
                }
            }
        } catch (Exception var2){
            Application.getActiveApplication().getOutput().output(var2);
        }
        return result;
    }

    private static void loadDownRegionLayer(){
        if(getFormIndex("在线地图") != -1){
            IFormMap formMap =(IFormMap)Application.getActiveApplication().getMainFrame().getFormManager().get(getFormIndex("在线地图"));
            Workspace workspace = Application.getActiveApplication().getWorkspace();
            Datasource memory_ds = null;
            if(workspace.getDatasources().contains(DownLoadProperties.getString("String_MemoryDsAlias"))){
                memory_ds = workspace.getDatasources().get(DownLoadProperties.getString("String_MemoryDsAlias"));
            }else {
                DatasourceConnectionInfo memory_conn = new DatasourceConnectionInfo();
                memory_conn.setEngineType(EngineType.UDB);
                memory_conn.setServer(":memory:");
                memory_conn.setAlias(DownLoadProperties.getString("String_MemoryDsAlias"));
                memory_ds = workspace.getDatasources().create(memory_conn);
            }
            Dataset region_dv = null;
            if(memory_ds.getDatasets().contains(DownLoadProperties.getString("String_TempDatasetName"))){
                region_dv = memory_ds.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
            }else {
                DatasetVectorInfo dv_info = new DatasetVectorInfo();
                dv_info.setType(DatasetType.REGION);
                dv_info.setName(DownLoadProperties.getString("String_TempDatasetName"));
                dv_info.setFileCache(true);
                region_dv = memory_ds.getDatasets().create(dv_info);
                region_dv.setPrjCoordSys(showMaps.getMapPrj());
            }
            if(formMap.getMapControl().getMap().getLayers().contains(DownLoadProperties.getString("String_TempLayerName"))){
                if(region_dv.getPrjCoordSys().equals(formMap.getMapControl().getMap().getPrjCoordSys())){
                    int layerIndex = formMap.getMapControl().getMap().getLayers().indexOf(DownLoadProperties.getString("String_TempLayerName"));
                    formMap.getMapControl().getMap().getLayers().moveToTop(layerIndex);
                }else {
                    formMap.getMapControl().getMap().getLayers().remove(DownLoadProperties.getString("String_TempLayerName"));
                    formMap.getMapControl().getMap().getLayers().add(updateRegionLayerPrj(memory_ds,showMaps.getMapPrj()),true);
                }
            }else {
                formMap.getMapControl().getMap().getLayers().add(region_dv,true);
            }
        }
    }

    private static void setFactory(WebMapInfo webMapInfo){
        GlobalFactory.getInstance().getCurrentShowWebMap().setBaseDatasourceAlias(webMapInfo.getBaseDatasourceAlias());
        GlobalFactory.getInstance().getCurrentShowWebMap().setBaseDatasetName(webMapInfo.getBaseDatasetName());
        GlobalFactory.getInstance().getCurrentShowWebMap().setBaseMapUrl(webMapInfo.getBaseMapUrl());
        GlobalFactory.getInstance().getCurrentShowWebMap().setMarkDatasourceAlias(webMapInfo.getMarkDatasourceAlias());
        GlobalFactory.getInstance().getCurrentShowWebMap().setMarkDatasetName(webMapInfo.getMarkDatasetName());
        GlobalFactory.getInstance().getCurrentShowWebMap().setMarkMapUrl(webMapInfo.getMarkMapUrl());
        GlobalFactory.getInstance().getCurrentShowWebMap().setMapPrj(webMapInfo.getMapPrj());
        GlobalFactory.getInstance().getCurrentShowWebMap().setMapKey(webMapInfo.getMapKey());
    }

    private static Dataset updateRegionLayerPrj(Datasource srcDs , PrjCoordSys tarPrj){
        DatasetVector dv_src = (DatasetVector)srcDs.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
        Recordset rs_src = dv_src.getRecordset(false,CursorType.STATIC);
        List<GeoRegion> geo_list = new ArrayList<>();
        while (!rs_src.isEOF()){
            GeoRegion geo = (GeoRegion)rs_src.getGeometry();
            CoordSysTranslator.convert(geo,GlobalFactory.getInstance().getCurrentShowWebMap().getMapPrj(),tarPrj,new CoordSysTransParameter(),CoordSysTransMethod.MTH_COORDINATE_FRAME);
            geo_list.add(geo);
            rs_src.moveNext();
        }
        if(rs_src != null){ rs_src.close(); rs_src.dispose(); }
        if(dv_src != null){ dv_src.close(); }
        srcDs.getDatasets().delete(DownLoadProperties.getString("String_TempDatasetName"));
        DatasetVectorInfo dv_info = new DatasetVectorInfo();
        dv_info.setType(DatasetType.REGION);
        dv_info.setName(DownLoadProperties.getString("String_TempDatasetName"));
        dv_info.setFileCache(true);
        DatasetVector dv_tar =  srcDs.getDatasets().create(dv_info);
        dv_tar.setPrjCoordSys(tarPrj);
        Recordset rs_tar = dv_tar.getRecordset(false,CursorType.DYNAMIC);
        for(GeoRegion geo : geo_list){
            rs_tar.addNew(geo);
            rs_tar.update();
        }
        if(rs_tar != null){ rs_tar.close(); rs_tar.dispose(); }
        if(dv_tar != null){ dv_tar.close(); }
        return srcDs.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
    }

    private static int getFormIndex(String formTitle){
        int formCount = Application.getActiveApplication().getMainFrame().getFormManager().getCount();
        int formIndex = -1;
        for(int i=0;i<formCount;i++){
            IForm form = Application.getActiveApplication().getMainFrame().getFormManager().get(i);
            if(form.getTitle().equals(formTitle)){
                formIndex = i;
            }
        }
        return formIndex;
    }
}
