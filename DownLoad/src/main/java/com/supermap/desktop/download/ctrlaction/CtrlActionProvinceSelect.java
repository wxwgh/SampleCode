package com.supermap.desktop.download.ctrlaction;

import com.supermap.data.*;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.core.implement.SmRibbonComboBox;
import com.supermap.desktop.download.DownLoadProperties;
import com.supermap.desktop.download.util.GlobalFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CtrlActionProvinceSelect extends CtrlAction {
    private SmRibbonComboBox smRibbonComboBox;
    public CtrlActionProvinceSelect(IBaseItem caller) {
        super(caller);
        this.smRibbonComboBox = (SmRibbonComboBox)caller;
    }



    @Override
    protected void run() {
        if(!smRibbonComboBox.getSelectedItem().toString().equals("选择省份")){
            GlobalFactory.getInstance().getCityComboBox().removeAllItems();
            GlobalFactory.getInstance().getCityComboBox().addItem("选择地市");
            Datasource memory_ds = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_MemoryDsAlias"));
            DatasetVector memory_dv = (DatasetVector)memory_ds.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
            Recordset memory_rs = memory_dv.getRecordset(false, CursorType.DYNAMIC);
            memory_rs.deleteAll();

            Datasource datasource = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_RegionDsAlias"));
            DatasetVector province_dv = (DatasetVector)datasource.getDatasets().get(DownLoadProperties.getString("String_ProvinceRegionName"));
            Recordset province_rs = province_dv.query("Name = '" + smRibbonComboBox.getSelectedItem().toString() + "'", CursorType.STATIC);

            GeoRegion geo = null;
            while (!province_rs.isEOF()){
                geo = (GeoRegion)province_rs.getGeometry();
                CoordSysTranslator.convert(geo,province_dv.getPrjCoordSys(),memory_dv.getPrjCoordSys(),new CoordSysTransParameter(),CoordSysTransMethod.MTH_COORDINATE_FRAME);
                memory_rs.addNew(geo);
                memory_rs.update();
                province_rs.moveNext();
            }

            IFormMap formMap = (IFormMap)Application.getActiveApplication().getActiveForm();
            formMap.getMapControl().getMap().ensureVisible(memory_rs);
            formMap.getMapControl().getMap().refresh();
            if(memory_rs != null){ memory_rs.close(); memory_rs.dispose(); }
            if(memory_dv != null) { memory_dv.close(); }
            if(province_rs != null){ province_rs.close(); province_rs.dispose(); }
            if(province_dv != null) { province_dv.close(); }

            DatasetVector dv = (DatasetVector)datasource.getDatasets().get(DownLoadProperties.getString("String_CityRegionName"));
            Recordset rs = dv.query("Province = '" + smRibbonComboBox.getSelectedItem().toString() + "'",CursorType.STATIC);
            while (!rs.isEOF()){
                String name = rs.getFieldValue("Name").toString();
                GlobalFactory.getInstance().getCityComboBox().addItem(name);
                rs.moveNext();
            }
            if(rs != null){ rs.close(); rs.dispose(); }
            if(dv != null) { dv.close(); }
        }
    }

    @Override
    public boolean enable() {
        if(GlobalFactory.getInstance().getProvinceComboBox() == null){
            this.smRibbonComboBox.removeAllItems();
            this.smRibbonComboBox.addItem("选择省份");
            Datasource datasource = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_RegionDsAlias"));
            DatasetVector dv = (DatasetVector)datasource.getDatasets().get(DownLoadProperties.getString("String_ProvinceRegionName"));
            QueryParameter queryParameter = new QueryParameter();
            queryParameter.setAttributeFilter("SmID > 0");
            queryParameter.setOrderBy(new String[] { "Code ASC"});
            queryParameter.setCursorType(CursorType.STATIC);
            Recordset rs = dv.query(queryParameter);
            rs.moveFirst();
            while (!rs.isEOF()){
                String name = rs.getFieldValue("Name").toString();
                this.smRibbonComboBox.addItem(name);
                rs.moveNext();
            }
            if(rs != null){ rs.close(); rs.dispose(); }
            if(dv != null) { dv.close(); }
            GlobalFactory.getInstance().setProvinceComboBox(this.smRibbonComboBox);
        }
        return true;
    }
}
