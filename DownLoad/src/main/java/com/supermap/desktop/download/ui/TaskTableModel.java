package com.supermap.desktop.download.ui;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class TaskTableModel extends AbstractTableModel {

    private String columnNames[] = {"任务名称", "数据类型", "下载进度", "创建时间", "标识", "位置", "删除"};
    private ArrayList<ArrayList> rowData;

    public ArrayList<ArrayList> getRowData() {
        return rowData;
    }

    public void setRowData(ArrayList<ArrayList> rowData) {
        this.rowData = rowData;
    }


    public int getColumnCount() {
        return columnNames.length;
    }

    public String getColumnName(int column) {
        return columnNames[column];
    }

    public int getRowCount() {
        return rowData.size();
    }

    public Object getValueAt(int row, int column) {
        ArrayList obj = rowData.get(row);
        return obj.get(column);
    }

    public Class getColumnClass(int column) {
        return (getValueAt(0, column).getClass());
    }

    public void setValueAt(Object value, int row, int column) {
        ArrayList obj = rowData.get(row);
        obj.set(column, value);
    }

    public void removeRow(int row) {
        this.rowData.remove(row);
    }

    public String getPath(int row) {
        //获取行
        ArrayList arrayList = this.rowData.get(row);
        //获取任务名称
        String name = (String) arrayList.get(0);
        //获取文件路径
        String path = (String) arrayList.get(6);
        return path + "/" + name;
    }

    public void setProgress(int row, int progress) {

        //获取行
        ArrayList arrayList = this.rowData.get(row);
        //设置进度条
        arrayList.set(2, progress);
    }

    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
