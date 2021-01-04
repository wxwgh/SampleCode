package com.supermap.desktop.download.ctrlaction;

import com.supermap.data.*;
import com.supermap.desktop.core.Application;
import com.supermap.desktop.core.Interface.IBaseItem;
import com.supermap.desktop.core.Interface.IFormMap;
import com.supermap.desktop.core.implement.CtrlAction;
import com.supermap.desktop.download.DownLoadProperties;
import com.supermap.desktop.download.ui.ShapeOpenUtilities;

public class CtrlActionReadShpFile extends CtrlAction {
    public CtrlActionReadShpFile(IBaseItem caller) {
        super(caller);
    }

    @Override
    protected void run() {
        try {
            IFormMap formMap = (IFormMap) Application.getActiveApplication().getActiveForm();
            Datasource ds = Application.getActiveApplication().getWorkspace().getDatasources().get(DownLoadProperties.getString("String_MemoryDsAlias"));
            DatasetVector dv = (DatasetVector)ds.getDatasets().get(DownLoadProperties.getString("String_TempDatasetName"));
            Recordset rs = dv.getRecordset(false, CursorType.DYNAMIC);
            rs.deleteAll();
            if(rs != null) {rs.close();rs.dispose();}
            formMap.getMapControl().getMap().refresh();
            ShapeOpenUtilities.OpenFile(formMap, ds);
        } catch (Exception ex) {
            Application.getActiveApplication().getOutput().output(ex);
        }
    }
}
