package com.supermap.desktop.download.ui;

import com.supermap.data.*;
import com.supermap.data.conversion.DataImport;
import com.supermap.data.conversion.ImportSetting;
import com.supermap.data.conversion.ImportSettingSHP;
import com.supermap.data.conversion.ImportSettings;
import com.supermap.desktop.controls.ui.controls.SmFileChoose;
import com.supermap.desktop.core.FileChooseMode;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.properties.CoreProperties;
import com.supermap.desktop.download.DownLoadProperties;
import com.supermap.desktop.download.util.GlobalFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ShapeOpenUtilities {
    private ShapeOpenUtilities() { }
    public static void OpenFile(IFormMap formMap, Datasource datasource) {
        SmFileChoose openFileDlg = (new SmFileChoose("ShpFile", FileChooseMode.OPEN_MANY)).setTitle("读取SHP文件").addFileFilter("SHP文件", new String[]{"shp"});
        openFileDlg.setCheckBoxShown(true);
        openFileDlg.setCheckBoxText(CoreProperties.getString("String_ReadOnlyOpen"));
        if (openFileDlg.showDefaultDialog() == 0) {
            File[] files = openFileDlg.getSelectFiles();
            if (files != null) {
                List<String> names = new ArrayList<>();
                DataImport dataImport = new DataImport();
                for(File file : files){
                    String name = file.getName();
                    String[] nameSplit = name.split("\\.");
                    names.add(nameSplit[0]);
                    ImportSetting importSetting = new ImportSettingSHP(file.getPath(), datasource);
                    dataImport.getImportSettings().add(importSetting);
                }
                dataImport.run();
                DatasetVector memory_dv = (DatasetVector)datasource.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
                Recordset memory_rs = memory_dv.getRecordset(false, CursorType.DYNAMIC);
                for(String name : names){
                    DatasetVector dv = (DatasetVector)datasource.getDatasets().get(name);
                    Recordset rs = dv.getRecordset(false, CursorType.STATIC);
                    while (!rs.isEOF()){
                        Geometry geo = rs.getGeometry();
                        CoordSysTranslator.convert(geo, dv.getPrjCoordSys(),formMap.getMapControl().getMap().getPrjCoordSys(),new CoordSysTransParameter(),CoordSysTransMethod.MTH_COORDINATE_FRAME);
                        memory_rs.addNew(geo);
                        memory_rs.update();
                        rs.moveNext();
                    }
                    if(rs != null) { rs.close();rs.dispose(); }
                    if(dv != null) { dv.close(); }
                    datasource.getDatasets().delete(name);
                }
                formMap.getMapControl().getMap().ensureVisible(memory_rs);
                formMap.getMapControl().getMap().refresh();
                if(memory_rs != null) { memory_rs.close();memory_rs.dispose(); }
                if(memory_dv != null) { memory_dv.close(); }
            }
        }
    }

}