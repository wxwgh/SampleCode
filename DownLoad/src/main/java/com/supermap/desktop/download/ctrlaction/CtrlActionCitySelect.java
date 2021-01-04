package com.supermap.desktop.download.ctrlaction;

import com.supermap.data.*;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.core.implement.SmRibbonComboBox;
import com.supermap.desktop.download.DownLoadProperties;
import com.supermap.desktop.download.util.GlobalFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CtrlActionCitySelect extends CtrlAction {
    private SmRibbonComboBox smRibbonComboBox;

    public CtrlActionCitySelect(IBaseItem caller) {
        super(caller);
        this.smRibbonComboBox = (SmRibbonComboBox)caller;
    }

    @Override
    protected void run() {
        if(!smRibbonComboBox.getSelectedItem().toString().equals("选择地市")){
            GlobalFactory.getInstance().getCountyComboBox().removeAllItems();
            GlobalFactory.getInstance().getCountyComboBox().addItem("选择区县");

            Datasource memory_ds = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_MemoryDsAlias"));
            DatasetVector memory_dv = (DatasetVector)memory_ds.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
            Recordset memory_rs = memory_dv.getRecordset(false, CursorType.DYNAMIC);
            memory_rs.deleteAll();

            Datasource datasource = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_RegionDsAlias"));
            DatasetVector city_dv = (DatasetVector)datasource.getDatasets().get(DownLoadProperties.getString("String_CityRegionName"));
            Recordset city_rs = city_dv.query("Name = '" + smRibbonComboBox.getSelectedItem().toString() + "' And Province = '" + GlobalFactory.getInstance().getProvinceComboBox().getSelectedItem().toString() + "'", CursorType.STATIC);

            while (!city_rs.isEOF()){
                GeoRegion geo = (GeoRegion)city_rs.getGeometry();
                CoordSysTranslator.convert(geo,city_dv.getPrjCoordSys(),memory_dv.getPrjCoordSys(),new CoordSysTransParameter(),CoordSysTransMethod.MTH_COORDINATE_FRAME);
                memory_rs.addNew(geo);
                memory_rs.update();
                city_rs.moveNext();
            }

            IFormMap formMap = (IFormMap)Application.getActiveApplication().getActiveForm();
            formMap.getMapControl().getMap().ensureVisible(memory_rs);
            formMap.getMapControl().getMap().refresh();
            if(memory_rs != null){ memory_rs.close(); memory_rs.dispose(); }
            if(memory_dv != null) { memory_dv.close(); }
            if(city_rs != null){ city_rs.close(); city_rs.dispose(); }
            if(city_dv != null) { city_dv.close(); }

            DatasetVector dv = (DatasetVector)datasource.getDatasets().get(DownLoadProperties.getString("String_CountyRegionName"));
            QueryParameter queryParameter = new QueryParameter();
            queryParameter.setAttributeFilter("City = '" + smRibbonComboBox.getSelectedItem().toString() + "' And Province = '" + GlobalFactory.getInstance().getProvinceComboBox().getSelectedItem().toString() + "'");
            queryParameter.setOrderBy(new String[] { "Code ASC"});
            queryParameter.setCursorType(CursorType.STATIC);
            Recordset rs = dv.query(queryParameter);
            while (!rs.isEOF()){
                String name = rs.getFieldValue("Name").toString();
                GlobalFactory.getInstance().getCountyComboBox().addItem(name);
                rs.moveNext();
            }
            if(rs != null){ rs.close(); rs.dispose(); }
            if(dv != null) { dv.close(); }
        }
    }

    @Override
    public boolean enable() {
        if(GlobalFactory.getInstance().getCityComboBox() == null){
            this.smRibbonComboBox.removeAllItems();
            this.smRibbonComboBox.addItem("选择地市");
            GlobalFactory.getInstance().setCityComboBox(this.smRibbonComboBox);
        }
        return true;
    }
}
