package com.supermap.desktop.download.ui;

import com.supermap.desktop.controls.ui.controls.SmDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import static com.supermap.desktop.download.ui.DialogDownLoadTask.jedis;

public class DialogAffirm extends SmDialog {

    public DialogAffirm() {
        super();
        //设置弹窗宽度和高度
        this.setSize(new Dimension(200, 100));
        //初始化
        initAffirm();
    }

    private void initAffirm() {
        //初始化对话框布局管理器
        this.setLayout(new BorderLayout());
        this.setResizable(false);
        this.setTitle("是否删除");
        JPanel jPanel = new JPanel();
        jPanel.setLayout(new FlowLayout(1, 10, 40));
        //创建一个确定按钮
        JButton buttonOK = new JButton("确认");
        //创建一个取消按钮
        JButton buttonCancel = new JButton("取消");
        jPanel.add(buttonOK);
        jPanel.add(buttonCancel);
        this.add(jPanel, BorderLayout.CENTER);
        //确认按钮绑定点击事件
        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //获取表选中行
                int row = DialogDownLoadTask.table.getSelectedRow();
                //删除缓存表格行
                List<String> values = jedis.lrange("taskList", row, row);
                jedis.lrem("taskList", 0, values.get(0));
                //获取文件路径
                String path = DialogDownLoadTask.model.getPath(row);
                //删除磁盘文件
                File file = new File(path);
                deleteFile(file);
                //删除model
                DialogDownLoadTask.model.removeRow(row);
                //更新UI
                DialogDownLoadTask.table.updateUI();
                dispose();
            }
        });

        //取消按钮绑定点击事件
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void deleteFile(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    this.deleteFile(files[i]);
                }
            }
        }
        file.delete();
    }
}
