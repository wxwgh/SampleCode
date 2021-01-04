package com.supermap.desktop.download.ctrlaction;

import com.supermap.data.*;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.core.implement.SmRibbonComboBox;
import com.supermap.desktop.download.DownLoadProperties;
import com.supermap.desktop.download.util.GlobalFactory;

public class CtrlActionCountySelect extends CtrlAction {
    private SmRibbonComboBox smRibbonComboBox;

    public CtrlActionCountySelect(IBaseItem caller) {
        super(caller);
        this.smRibbonComboBox = (SmRibbonComboBox)caller;

    }

    @Override
    protected void run() {
        if(!smRibbonComboBox.getSelectedItem().toString().equals("选择区县")){
            Datasource memory_ds = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_MemoryDsAlias"));
            DatasetVector memory_dv = (DatasetVector)memory_ds.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
            Recordset memory_rs = memory_dv.getRecordset(false, CursorType.DYNAMIC);
            memory_rs.deleteAll();

            Datasource datasource = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_RegionDsAlias"));
            DatasetVector county_dv = (DatasetVector)datasource.getDatasets().get(DownLoadProperties.getString("String_CountyRegionName"));
            QueryParameter queryParameter = new QueryParameter();
            queryParameter.setAttributeFilter("Name = '" + smRibbonComboBox.getSelectedItem().toString() + "' And Province = '" + GlobalFactory.getInstance().getProvinceComboBox().getSelectedItem().toString() + "' And City = '" + GlobalFactory.getInstance().getCityComboBox().getSelectedItem().toString() + "'");
            queryParameter.setOrderBy(new String[] { "Code ASC"});
            queryParameter.setCursorType(CursorType.STATIC);
            Recordset county_rs = county_dv.query(queryParameter);

            while (!county_rs.isEOF()){
                GeoRegion geo = (GeoRegion)county_rs.getGeometry();
                CoordSysTranslator.convert(geo,county_dv.getPrjCoordSys(),memory_dv.getPrjCoordSys(),new CoordSysTransParameter(),CoordSysTransMethod.MTH_COORDINATE_FRAME);
                memory_rs.addNew(geo);
                memory_rs.update();
                county_rs.moveNext();
            }

            IFormMap formMap = (IFormMap)Application.getActiveApplication().getActiveForm();
            formMap.getMapControl().getMap().ensureVisible(memory_rs);
            formMap.getMapControl().getMap().refresh();
            if(memory_rs != null){ memory_rs.close(); memory_rs.dispose(); }
            if(memory_dv != null) { memory_dv.close(); }
            if(county_rs != null){ county_rs.close(); county_rs.dispose(); }
            if(county_dv != null) { county_dv.close(); }
        }
    }

    @Override
    public boolean enable() {
        if(GlobalFactory.getInstance().getCountyComboBox() == null){
            this.smRibbonComboBox.removeAllItems();
            this.smRibbonComboBox.addItem("选择区县");
            GlobalFactory.getInstance().setCountyComboBox(this.smRibbonComboBox);
        }
        return true;
    }
}
