package com.supermap.desktop.download.ui;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Vector;

public class ColorTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    String columnNames[] = {"操作", "级别", "比例尺", "像素尺寸", "瓦片总数", "理论下载大小"};

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

    public boolean isCellEditable(int row, int column) {

//        return (column == 0);
        return false;
    }
}
