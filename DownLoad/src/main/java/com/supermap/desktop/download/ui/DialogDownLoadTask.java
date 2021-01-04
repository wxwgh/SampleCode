package com.supermap.desktop.download.ui;

import com.alibaba.fastjson.JSONObject;
import com.supermap.desktop.controls.ui.controls.SmDialog;
import com.supermap.desktop.download.bean.DownTask;
import com.supermap.desktop.download.bean.Task;
import redis.clients.jedis.Jedis;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DialogDownLoadTask extends SmDialog {

    public static Jedis jedis = new Jedis("localhost");
    public static JTable table;
    public static TaskTableModel model;

    public DialogDownLoadTask() {
        super();
        //设置弹窗宽度和高度
        this.setSize(new Dimension(800, 500));
        //初始化任务表格
        initTable();
    }

    //初始化任务表格
    private void initTable() {
        //初始化对话框布局管理器
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setTitle("下载任务");
        //获取缓存数据
        //创建可变数组
        ArrayList listParent = new ArrayList();
        //获取数据长度
        Long num = jedis.llen("taskList");
        //遍历
        for (int i = 0; i < num; i++) {
            List<String> values = jedis.lrange("taskList", i, i);
            ArrayList list = new ArrayList();
            //json字符串转对象
            DownTask downTask = JSONObject.parseObject(values.get(0), DownTask.class);
            Task task = downTask.getTask();
            list.add(task.getTaskName());
            list.add(task.getMapType() + "-" + task.getMapName());
            int progress = task.getTileCurrentNum() / task.getTileNum() * 100;
            list.add(progress);
            list.add(task.getCreateTime());
            list.add(downTask.getId());
            list.add(i);
            list.add(task.getFilePath());
            listParent.add(list);
        }
        model = new TaskTableModel();
        //设置数据
        model.setRowData(listParent);
        //通过model创建表格
        table = new JTable(model);
        //设置行高
        table.setRowHeight(40);
        // 设置不允许拖动重新排序各列
        table.getTableHeader().setReorderingAllowed(false);
        // 设置不允许手动改变列宽
        table.getTableHeader().setResizingAllowed(false);
        // 设置滚动面板视口大小（超过该大小的行数据，需要拖动滚动条才能看到）
        table.setPreferredScrollableViewportSize(new Dimension(this.getWidth(), this.getHeight()));

        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(SwingConstants.CENTER);
        //设置表头居中
        table.getTableHeader().setDefaultRenderer(render);
        //内容居中
        table.getColumnModel().getColumn(0).setCellRenderer(render);
        table.getColumnModel().getColumn(1).setCellRenderer(render);
        table.getColumnModel().getColumn(2).setCellRenderer(render);
        table.getColumnModel().getColumn(3).setCellRenderer(render);
        //设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(200);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(200);
        table.getColumnModel().getColumn(4).setPreferredWidth(24);
        table.getColumnModel().getColumn(5).setPreferredWidth(24);
        table.getColumnModel().getColumn(6).setPreferredWidth(24);
        //设置下载进度单元格
        table.getColumnModel().getColumn(2).setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            // 创建一个进度条
            JProgressBar progressBar = new JProgressBar();
            // 设置进度的 最小值 和 最大值
            progressBar.setMinimum(0);
            progressBar.setMaximum(100);
            // 设置当前进度值
            Object progress = table.getValueAt(row, 2);
            progressBar.setValue((int) progress);
            // 绘制百分比文本（进度条中间显示的百分数）
            progressBar.setStringPainted(true);
            return progressBar;
        });
        //设置完成标识单元格
        table.getColumnModel().getColumn(4).setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            int progress = (int) table.getValueAt(row, 2);
            ImageIcon imageIcon;
            if (progress == 100) {
                imageIcon = new ImageIcon("./templates/SampleCode/DownLoad/images/DownLoadTrue.png");
            } else {
                imageIcon = new ImageIcon("./templates/SampleCode/DownLoad/images/DownLoadFalse.png");
            }
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
            JLabel jLabel = new JLabel(imageIcon);
            jLabel.setPreferredSize(new Dimension(24, 30));
            return jLabel;
        });
        //设置文件位置单元格
        table.getColumnModel().getColumn(5).setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            ImageIcon imageIcon = new ImageIcon("./templates/SampleCode/DownLoad/images/file.png");
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
            JLabel jLabel = new JLabel(imageIcon);
            jLabel.setPreferredSize(new Dimension(24, 30));
            return jLabel;
        });
        //设置删除单元格
        table.getColumnModel().getColumn(6).setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
            ImageIcon imageIcon = new ImageIcon("./templates/SampleCode/DownLoad/images/delete.png");
            imageIcon.setImage(imageIcon.getImage().getScaledInstance(24, 24, Image.SCALE_DEFAULT));
            JLabel jLabel = new JLabel(imageIcon);
            jLabel.setPreferredSize(new Dimension(24, 30));
            return jLabel;
        });
        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane);
        //单元格绑定点击事件
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if (column == 6) {
                    new DialogAffirm().showDialog();
                } else if (column == 5) {
                    try {
                        String path = model.getPath(row);
                        Desktop.getDesktop().open(new File(path));
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });
    }

}
