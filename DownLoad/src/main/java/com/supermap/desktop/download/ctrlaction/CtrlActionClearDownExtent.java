package com.supermap.desktop.download.ctrlaction;

import com.supermap.data.CursorType;
import com.supermap.data.DatasetVector;
import com.supermap.data.Datasource;
import com.supermap.data.Recordset;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.core.utilties.CursorUtilities;
import com.supermap.desktop.download.DownLoadProperties;
import com.supermap.desktop.download.util.GlobalFactory;

public class CtrlActionClearDownExtent extends CtrlAction {
    public CtrlActionClearDownExtent(IBaseItem caller) {
        super(caller);
    }

    @Override
    protected void run() {
        try{
            IFormMap formMap = (IFormMap) Application.getActiveApplication().getMainFrame().getFormManager().get(0);
            Datasource ds = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_MemoryDsAlias"));
            DatasetVector dv = (DatasetVector)ds.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
            Recordset rs = dv.getRecordset(false, CursorType.DYNAMIC);
            rs.deleteAll();
            if(rs != null) {rs.close();rs.dispose();}
            formMap.getMapControl().getMap().refresh();
            GlobalFactory.getInstance().getProvinceComboBox().setSelectedIndex(0);
            GlobalFactory.getInstance().getCityComboBox().removeAllItems();
            GlobalFactory.getInstance().getCityComboBox().addItem("选择地市");
            GlobalFactory.getInstance().getCountyComboBox().removeAllItems();
            GlobalFactory.getInstance().getCountyComboBox().addItem("选择区县");
        } catch (Exception ex){
            Application.getActiveApplication().getOutput().output(ex);
        } finally {
            CursorUtilities.setDefaultCursor();
        }
    }
}
