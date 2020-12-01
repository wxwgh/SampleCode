package com.supermap.desktop.download.ui;

import javax.swing.table.AbstractTableModel;
import java.util.Vector;

public class ColorTableModel extends AbstractTableModel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Vector<Object> rowData;

    public Vector<Object> getRowData() {
        return rowData;
    }

    public void setRowData(Vector<Object> rowData) {
        this.rowData = rowData;
    }

    String columnNames[] = { "操作","级别", "比例尺","像素尺寸","瓦片总数","理论下载大小" };

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
        Object[] obj = (Object[]) rowData.get(row);
        return obj[column];
    }

    @SuppressWarnings("rawtypes")
    public Class getColumnClass(int column) {
        return (getValueAt(0, column).getClass());
    }

    public void setValueAt(Object value, int row, int column) {
        Object[] obj = (Object[]) rowData.get(row);
        obj[column] = value;
    }

    public boolean isCellEditable(int row, int column) {
        return (column == 0);
    }
}
