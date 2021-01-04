package com.supermap.desktop.download.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.supermap.desktop.controls.ui.controls.SmDialog;
import com.supermap.desktop.download.bean.DownFile;
import com.supermap.desktop.download.bean.DownTask;
import com.supermap.desktop.download.bean.Task;
import com.supermap.desktop.download.bean.Tile;
import com.supermap.desktop.download.config.MapUrl;
import com.supermap.desktop.download.util.CoordinateToolkit;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.HttpClientBuilder;
import redis.clients.jedis.Jedis;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DialogTerrainLoad extends SmDialog {

    private ArrayList rowData = new ArrayList();
    private ArrayList level = new ArrayList();
    public static Jedis jedis = new Jedis("localhost");
    private static String taskName;
    private static String oldTaskName="谷歌地形下载";
    //下载路径
    private static String filePath = "D:\\SuperMap DownLoad";
    private static Boolean isCrop=true;
    //是否拼接大图
    private static boolean isJoin = false;
    //文件类型
    private String type = ".png";
    private String formatName = "png";
    private static int fileNum = 0;
    private static int fileCurrentNum = 0;
    public DialogTerrainLoad() {
        super();
        //设置弹窗宽度和高度
        this.setSize(new Dimension(500, 500));
        //初始化对话框布局管理器
        this.setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
        this.setResizable(false);
        this.setTitle("地形下载");
        //初始化线程池
        initPool();
        //初始化表格
        initTable();
        //初始化
        initPanel();
    }
    private void initTable(){
        Tile tile1 = new Tile();
        Tile tile2 = new Tile();
        for (int i = 0; i < MapUrl.GoogleScale.length; i++) {
            long tileNum = 0;
            // 经纬度转瓦片
            CoordinateToolkit.gcj02_LngLat_To_Tile(MapUrl.northwestGoogle, i, tile1);
            CoordinateToolkit.gcj02_LngLat_To_Tile(MapUrl.southeastGoogle, i, tile2);
            int minX = tile1.getX() < tile2.getX() ? tile1.getX() : tile2.getX();
            int minY = tile1.getY() < tile2.getY() ? tile1.getY() : tile2.getY();
            int maxX = tile1.getX() > tile2.getX() ? tile1.getX() : tile2.getX();
            int maxY = tile1.getY() > tile2.getY() ? tile1.getY() : tile2.getY();
            tileNum += (maxX - minX + 1) * (maxY - minY + 1);
            long temp = tileNum * 30;
            String tileWeight = "";
            if (temp < 1024) {
                tileWeight = temp + "KB";
            } else if (temp >= 1024 && temp < 1048576) {
                float num = (float) temp / 1024;
                DecimalFormat df = new DecimalFormat("0.00");//保留2位小数
                tileWeight = df.format(num) + "MB";
            } else if (temp >= 1048576 && temp < 1073741824) {
                float num = (float) temp / 1024 / 1024;
                DecimalFormat df = new DecimalFormat("0.00");//保留2位小数
                tileWeight = df.format(num) + "GB";
            } else {
                float num = (float) temp / 1024 / 1024 / 1024;
                DecimalFormat df = new DecimalFormat("0.00");//保留2位小数
                tileWeight = df.format(num) + "T";
            }
            ArrayList list = new ArrayList();
            list.add(Boolean.FALSE);
            list.add(i);
            list.add(MapUrl.GoogleScale[i]);
            list.add("256x256");
            list.add(tileNum);
            list.add(tileWeight);
            this.rowData.add(list);
        }
    }
    private void initPanel(){
        Box headerBox6 = Box.createHorizontalBox();
        Component header6 = Box.createVerticalStrut(30);
        headerBox6.add(header6);
        Box bottomBox6 = Box.createHorizontalBox();
        Component bottom6 = Box.createVerticalStrut(16);
        bottomBox6.add(bottom6);
        //创建横向容器 任务名称行
        Box taskNameBox = Box.createHorizontalBox();
        taskNameBox.setSize(0,30);
        JLabel taskNameLabel = new JLabel("任务名称");
        JTextField taskNameField = new JTextField("谷歌地形下载",24);
        //创建固定宽度不可见组件
        Component leftTemp = Box.createHorizontalStrut(10);
        Component centerTemp = Box.createHorizontalStrut(10);
        Component rightTemp = Box.createHorizontalStrut(122);
        taskNameBox.add(leftTemp);
        taskNameBox.add(taskNameLabel);
        taskNameBox.add(centerTemp);
        taskNameBox.add(taskNameField);
        taskNameBox.add(rightTemp);

        this.add(headerBox6);
        this.add(taskNameBox);
        this.add(bottomBox6);
        //创建横向容器 下载目录
        Box catalogBox = Box.createHorizontalBox();
        JLabel catalogLabel = new JLabel("下载目录");
        JTextField catalogField = new JTextField("D:\\SuperMap DownLoad",24);
        JButton catalogButton = new JButton("...");
        //创建固定宽度不可见组件
        Component leftTemp2 = Box.createHorizontalStrut(10);
        Component centerTemp2 = Box.createHorizontalStrut(5);
        Component centerTemp10 = Box.createHorizontalStrut(10);
        Component rightTemp2 = Box.createHorizontalStrut(10);
        catalogBox.add(leftTemp2);
        catalogBox.add(catalogLabel);
        catalogBox.add(centerTemp10);
        catalogBox.add(catalogField);
        catalogBox.add(centerTemp2);
        catalogBox.add(catalogButton);
        catalogBox.add(rightTemp2);
        this.add(catalogBox);
        //创建横向容器 边界剪裁
        Box headerBox7 = Box.createHorizontalBox();
        Component header7 = Box.createVerticalStrut(20);
        headerBox7.add(header7);
        Box bottomBox7 = Box.createHorizontalBox();
        Component bottom7 = Box.createVerticalStrut(20);
        bottomBox7.add(bottom7);
        Box cropBox = Box.createHorizontalBox();
        JCheckBox cropCheck = new JCheckBox("按边界剪裁");
        cropCheck.setSelected(true);
        Component rightTemp3 = Box.createHorizontalGlue();
        Component leftTemp3 = Box.createHorizontalStrut(5);
        cropBox.add(leftTemp3);
        cropBox.add(cropCheck);
        cropBox.add(rightTemp3);
        this.add(headerBox7);
        this.add(cropBox);
        this.add(bottomBox7);
        //创建横向容器 保存格式
        Box saveBox = Box.createHorizontalBox();
        JLabel saveLabel = new JLabel("保存格式");
        ButtonGroup saveButtonGroup = new ButtonGroup();
        JRadioButton saveJRadio = new JRadioButton("瓦片");
        saveJRadio.setSelected(true);
        JRadioButton saveJRadio2 = new JRadioButton("拼接大图");
        saveButtonGroup.add(saveJRadio);
        saveButtonGroup.add(saveJRadio2);
        Component centerTemp7 = Box.createHorizontalStrut(20);
        Component centerTemp8 = Box.createHorizontalStrut(20);
        Component rightTemp8 = Box.createHorizontalGlue();
        Component leftTemp8 = Box.createHorizontalStrut(10);
        saveBox.add(leftTemp8);
        saveBox.add(saveLabel);
        saveBox.add(centerTemp8);
        saveBox.add(saveJRadio);
        saveBox.add(centerTemp7);
        saveBox.add(saveJRadio2);
        saveBox.add(rightTemp8);
        this.add(saveBox);
        //创建横向容器 保存格式按钮组
        Box tileButtonBox = Box.createHorizontalBox();
        ButtonGroup tileButtonGroup = new ButtonGroup();
        JRadioButton tileJRadio = new JRadioButton(".png");
        tileJRadio.setSelected(true);
        JRadioButton tileJRadio2 = new JRadioButton(".jpg");
        Component rightTemp9 = Box.createHorizontalGlue();
        Component leftTemp9 = Box.createHorizontalStrut(5);
        tileButtonGroup.add(tileJRadio);
        tileButtonGroup.add(tileJRadio2);
        tileButtonBox.add(leftTemp9);
        tileButtonBox.add(tileJRadio);
        tileButtonBox.add(tileJRadio2);
        tileButtonBox.add(rightTemp9);
        tileButtonBox.setVisible(true);
        ButtonGroup joinButtonGroup = new ButtonGroup();
        JRadioButton joinJRadio = new JRadioButton(".tif");
        joinJRadio.setSelected(true);
        JRadioButton joinJRadio2 = new JRadioButton(".bmp");
        JRadioButton joinJRadio3 = new JRadioButton(".png");
        JRadioButton joinJRadio4 = new JRadioButton(".jpg");
        Component rightTemp10 = Box.createHorizontalGlue();
        Component leftTemp10 = Box.createHorizontalStrut(5);
        joinButtonGroup.add(joinJRadio);
        joinButtonGroup.add(joinJRadio2);
        joinButtonGroup.add(joinJRadio3);
        joinButtonGroup.add(joinJRadio4);
        Box joinButtonBox = Box.createHorizontalBox();
        joinButtonBox.add(leftTemp10);
        joinButtonBox.add(joinJRadio);
        joinButtonBox.add(joinJRadio2);
        joinButtonBox.add(joinJRadio3);
        joinButtonBox.add(joinJRadio4);
        joinButtonBox.add(rightTemp10);
        joinButtonBox.setVisible(false);
        this.add(tileButtonBox);
        this.add(joinButtonBox);
        //创建横向容器 全选
        Box allBox = Box.createHorizontalBox();
        JLabel allLabel = new JLabel("选取级别");
        JCheckBox allCheck = new JCheckBox("全选");
        Component leftTemp4 = Box.createHorizontalStrut(15);
        Component centerTemp4 = Box.createHorizontalGlue();
        Component rightTemp4 = Box.createHorizontalStrut(5);
        allBox.add(leftTemp4);
        allBox.add(allLabel);
        allBox.add(centerTemp4);
        allBox.add(allCheck);
        allBox.add(rightTemp4);
        this.add(allBox);
        //创建横向容器 表格
        Box tableBox = Box.createHorizontalBox();
        //创建表格
        ColorTableModel model = new ColorTableModel();
        model.setRowData(this.rowData);
        JTable table = new JTable(model);
        // 设置行高
        table.setRowHeight(40);
        // 设置不允许手动改变列宽
        table.getTableHeader().setResizingAllowed(false);
        //内容居中
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(render);
        table.getColumnModel().getColumn(2).setCellRenderer(render);
        table.getColumnModel().getColumn(3).setCellRenderer(render);
        table.getColumnModel().getColumn(4).setCellRenderer(render);
        table.getColumnModel().getColumn(5).setCellRenderer(render);
        //设置列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(10);
        table.getColumnModel().getColumn(1).setPreferredWidth(10);
        //设置 不可以调整此列大小
        table.getColumnModel().getColumn(0).setResizable(false);
        table.getColumnModel().getColumn(1).setResizable(false);
        //设置表头居中
        table.getTableHeader().setDefaultRenderer(render);
        // 设置不允许拖动重新排序各列
        table.getTableHeader().setReorderingAllowed(false);
        // 设置滚动面板视口大小（超过该大小的行数据，需要拖动滚动条才能看到）
        table.setPreferredScrollableViewportSize(new Dimension(500,300));
        // 把 表格 放到 滚动面板 中（表头将自动添加到滚动面板顶部）
        JScrollPane scrollPane = new JScrollPane(table);
        Component leftTemp5 = Box.createHorizontalStrut(10);
        Component rightTemp5 = Box.createHorizontalStrut(10);
        tableBox.add(leftTemp5);
        tableBox.add(scrollPane);
        tableBox.add(rightTemp5);
        Box bottomBox5 = Box.createHorizontalBox();
        Component bottom5 = Box.createVerticalStrut(10);
        bottomBox5.add(bottom5);
        this.add(tableBox);
        this.add(bottomBox5);
        //创建横向容器 确定取消
        Box affirmBox = Box.createHorizontalBox();
        JButton trueButton = new JButton("确认");
        JButton falseButton = new JButton("取消");
        Component leftTemp6 = Box.createHorizontalGlue();
        Component centerTemp6 = Box.createHorizontalStrut(10);
        Component rightTemp6 = Box.createHorizontalStrut(10);
        affirmBox.add(leftTemp6);
        affirmBox.add(trueButton);
        affirmBox.add(centerTemp6);
        affirmBox.add(falseButton);
        affirmBox.add(rightTemp6);
        this.add(affirmBox);

        //底部
        Box bottomBox = Box.createHorizontalBox();
        Component bottom = Box.createVerticalStrut(10);
        bottomBox.add(bottom);
        this.add(bottomBox);

        DialogTerrainLoad thisTemp =this;
        //任务名称输入框事件
        Document document = taskNameField.getDocument();
        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Document document = e.getDocument();
                try {
                    String text = document.getText(0, document.getLength());
                    oldTaskName = text;
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                Document document = e.getDocument();
                try {
                    String text = document.getText(0, document.getLength());
                    oldTaskName = text;
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        //下载目录 输入框事件
        Document document2 = catalogField.getDocument();
        document2.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Document document = e.getDocument();
                try {
                    String text = document.getText(0, document.getLength());
                    filePath = text;
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                Document document = e.getDocument();
                try {
                    String text = document.getText(0, document.getLength());
                    filePath = text;
                } catch (BadLocationException badLocationException) {
                    badLocationException.printStackTrace();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
        //...按钮事件
        catalogButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jf = new JFileChooser();
                jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jf.showOpenDialog(thisTemp);
                //获取选择的文件
                File f = jf.getSelectedFile();
                //返回路径名
                String s = f.getAbsolutePath();
                catalogField.setText(s);
                filePath = s;
                System.out.println(filePath);
            }
        });
        //边界剪裁事件
        cropCheck.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cropCheck.isSelected() == false) {
                    cropCheck.setSelected(false);
                    isCrop = false;
                } else {
                    cropCheck.setSelected(true);
                    isCrop = true;
                }
            }
        });

        //瓦片单选框 事件
        saveJRadio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinButtonBox.setVisible(false);
                tileButtonBox.setVisible(true);
                isJoin = false;
                if (tileJRadio.isSelected() == true) {
                    type = tileJRadio.getActionCommand();
                    formatName = tileJRadio.getActionCommand().substring(1);
                } else if (tileJRadio2.isSelected() == true) {
                    type = tileJRadio2.getActionCommand();
                    formatName = tileJRadio2.getActionCommand().substring(1);
                }
            }
        });
        //拼接大图单选框 事件
        saveJRadio2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                joinButtonBox.setVisible(true);
                tileButtonBox.setVisible(false);
                isJoin = true;
                if (joinJRadio.isSelected() == true) {
                    type = joinJRadio.getActionCommand();
                    formatName = joinJRadio.getActionCommand().substring(1);
                } else if (joinJRadio2.isSelected() == true) {
                    type = joinJRadio2.getActionCommand();
                    formatName = joinJRadio2.getActionCommand().substring(1);
                } else if (joinJRadio3.isSelected() == true) {
                    type = joinJRadio3.getActionCommand();
                    formatName = joinJRadio3.getActionCommand().substring(1);
                } else if (joinJRadio4.isSelected() == true) {
                    type = joinJRadio4.getActionCommand();
                    formatName = joinJRadio4.getActionCommand().substring(1);
                }
            }
        });
        tileJRadio.addActionListener(listenType);
        tileJRadio2.addActionListener(listenType);
        joinJRadio.addActionListener(listenType);
        joinJRadio2.addActionListener(listenType);
        joinJRadio3.addActionListener(listenType);
        joinJRadio4.addActionListener(listenType);
        //全选事件
        allCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (allCheck.isSelected() == false) {
                    allCheck.setSelected(false);
                    int rowCount = table.getRowCount();
                    Object value = Boolean.FALSE;
                    for (int i = 0; i < rowCount; i++) {
                        table.setValueAt(value, i, 0);
                        Object valueAt = table.getValueAt(i, 1);
                        level.remove(valueAt);
                    }
                    table.updateUI();
                } else {
                    allCheck.setSelected(true);
                    int rowCount = table.getRowCount();
                    Object value = Boolean.TRUE;
                    for (int i = 0; i < rowCount; i++) {
                        table.setValueAt(value, i, 0);
                        Object valueAt = table.getValueAt(i, 1);
                        if (!level.contains(valueAt)) {
                            level.add(valueAt);
                        }
                    }
                    table.updateUI();
                }
            }
        });
        //表格单元格 事件
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int row = table.getSelectedRow();
                int column = table.getSelectedColumn();
                if (column == 0) {
                    Object obj = table.getValueAt(row, column);
                    if (obj.equals(Boolean.TRUE)) {
                        table.setValueAt((Object) Boolean.FALSE, row, column);
                        level.remove(table.getValueAt(row, 1));
                        //获取瓦片数量
                        Long num = (Long) table.getValueAt(row, 4);
                        fileNum -= num;
                        System.out.println(fileNum);
                    } else if (obj.equals(Boolean.FALSE)) {
                        table.setValueAt((Object) Boolean.TRUE, row, column);
                        level.add(table.getValueAt(row, 1));
                        //获取瓦片数量
                        Long num = (Long) table.getValueAt(row, 4);
                        fileNum += num;
                        System.out.println(fileNum);
                    }
                }
            }
        });
        //确认按钮事件
        trueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //重命名文件夹
                renameFile();
                String id = UUID.randomUUID().toString().replaceAll("-", "");
                //开启一个线程
                new Thread() {
                    @Override
                    public void run() {
                        addTask(id);
                    }
                }.start();
                //将下载任务加入队列
                addDownLoad(id);
                dispose();
            }
        });
        //取消按钮事件
        falseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fileNum = 0;
                dispose();
            }
        });
    }
    //文件格式 单选框事件
    private final ActionListener listenType = e -> {
        type = e.getActionCommand();
        formatName = e.getActionCommand().substring(1);
        System.out.println(formatName);
    };
    //重命名文件夹
    private void renameFile() {
        File file = new File(this.filePath);
        //如果文件夹存在
        if (file.exists()) {
            File[] files = file.listFiles();
            int index = 0;
            Boolean flag = false;
            for (int i = 0; i < files.length; i++) {
                //如果是文件夹
                if (files[i].isDirectory()) {
                    //判断文件名是否包含 当前设置的任务名称
                    if (files[i].getName().contains(this.oldTaskName)) {
                        index++;
                        flag = true;
                    }
                }
            }
            if (flag) {
                this.taskName = this.oldTaskName + "(" + index + ")";
            } else {
                this.taskName = this.oldTaskName;
            }
        } else {
            this.taskName = this.oldTaskName;
        }
    }
    private static String mapType="google";
    private static String mapName="地形";
    /**
     * 将下载任务信息存入redis中
     */
    private void addTask(String id) {
        Task task = new Task();
        //任务名称
        task.setTaskName(taskName);
        //地图类型
        task.setMapType(mapType);
        //地图名称
        task.setMapName(mapName);
        //当前下载瓦片数量
        task.setTileCurrentNum(fileCurrentNum);
        //瓦片总数
        task.setTileNum(fileNum);
        //创建时间
        //时间格式
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //获取当前时间
        Date date = new Date();
        String timeString = format.format(date);
        task.setCreateTime(timeString);
        //文件所在目录
        task.setFilePath(filePath);
        DownTask downTask = new DownTask();
        downTask.setId(id);
        downTask.setTask(task);
        String downTaskString = JSON.toJSONString(downTask);
        jedis.rpush("taskList", downTaskString);
        fileNum = 0;
        new DialogDownLoadTask().showDialog();
    }
    //瓦片下载线程池
    private static ExecutorService tilePool = null;
    //下载任务线程池
    private static ExecutorService downTaskPool = null;
    private static Executor executor = null;
    /**
     * 初始化线程池 相关内容
     */
    private void initPool() {
        /**
         * 初始化下载任务线程池
         */
        if (downTaskPool == null) {
            downTaskPool = Executors.newFixedThreadPool(MapUrl.downTaskPoolSize);
        }
        /**
         * 初始化瓦片下载线程池
         * newFixedThreadPool为定长线程池,可控制线程最大并发数,超出的线程会在队列中等待
         */
        if (this.tilePool == null) {
            this.tilePool = Executors.newFixedThreadPool(MapUrl.tilePoolSize);
        }
        /**
         * 初始化 apache快速构建HTTP请求 执行器
         * 所有请求会使用一个公共的连接池,共200个连接
         * executor实际上 相当于一个线程池实例
         */
        if (executor == null) {
            executor = Executor.newInstance(
                    HttpClientBuilder.create()
                            .setRetryHandler((exception, executionCount, context) -> {
                                return executionCount < MapUrl.retryNum;
                            })
                            .build());
        }
    }
    /**
     * 将下载任务加入队列
     */
    private void addDownLoad(String id) {
        downTaskPool.execute(() -> {
            // 提前创建对象，重复利用，下面的循环次数可能到达百万次甚至千万次，每次创建对象效率太低
            Tile tile1 = new Tile();
            Tile tile2 = new Tile();
            int minX = 0;
            int minY = 0;
            int maxX = 0;
            int maxY = 0;
            File folder = null;
            String fileName = "";
            String url;
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < level.size(); i++) {
                CoordinateToolkit.gcj02_LngLat_To_Tile(MapUrl.northwestGoogle, (int) level.get(i), tile1);
                CoordinateToolkit.gcj02_LngLat_To_Tile(MapUrl.southeastGoogle, (int) level.get(i), tile2);
                minX = tile1.getX() < tile2.getX() ? tile1.getX() : tile2.getX();
                minY = tile1.getY() < tile2.getY() ? tile1.getY() : tile2.getY();
                maxX = tile1.getX() > tile2.getX() ? tile1.getX() : tile2.getX();
                maxY = tile1.getY() > tile2.getY() ? tile1.getY() : tile2.getY();
                ArrayList imagesY = new ArrayList();
                for (int x = minX; x <= maxX; x++) {
                    ArrayList imagesX = new ArrayList();
                    sb.setLength(0);
                    if (isJoin) {
                        sb.append(this.filePath).append(File.separator).append(this.taskName).append(File.separator).append(level.get(i));
                    } else {
                        sb.append(this.filePath).append(File.separator).append(this.taskName).append(File.separator).append(level.get(i)).append(File.separator).append(x);
                    }
                    folder = new File(sb.toString());
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    for (int y = minY; y <= maxY; y++) {
                        // 允许下载
                        sb.setLength(0);
                        if (isJoin) {
                            fileName = sb.append(folder.getPath()).append(File.separator).append(taskName).append(type).toString();
                        } else {
                            fileName = sb.append(folder.getPath()).append(File.separator).append(y).append(this.type).toString();
                        }
                        url = MapUrl.TerrainUrl.replace("{x}", String.valueOf(x)).replace("{y}", String.valueOf(y)).replace("{z}", String.valueOf(level.get(i)));
                        if (isJoin) {
                            try {
                                //如果文件不存在 则进行瓦片下载
                                if (!new File(fileName).exists()) {
                                    //是否剪裁
                                    if (this.isCrop) {
                                        byte[] content = executor.execute(Request.Get(url).connectTimeout(5000).socketTimeout(5000)).returnContent().asBytes();
                                        //将字节作为输入流
                                        ByteArrayInputStream in = new ByteArrayInputStream(content);
                                        //字节转带缓冲区的图像
                                        BufferedImage image = ImageIO.read(in);
                                        //按边界裁剪
                                        BufferedImage temp = image.getSubimage(0, 0, 256, 256);
                                        imagesX.add(temp);
                                        updateProgress(id);
                                    } else {
                                        byte[] content = executor.execute(Request.Get(url).connectTimeout(5000).socketTimeout(5000)).returnContent().asBytes();
                                        //将字节作为输入流
                                        ByteArrayInputStream in = new ByteArrayInputStream(content);
                                        //字节转带缓冲区的图像
                                        BufferedImage image = ImageIO.read(in);
                                        imagesX.add(image);
                                        updateProgress(id);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            downLoad(new DownFile(url, fileName, id));
                        }

                    }
                    if (isJoin) {
                        BufferedImage imagesPS = tileCrop(true, imagesX);
                        imagesY.add(imagesPS);
                    }
                }
                if (isJoin) {
                    BufferedImage imagesZoom = tileCrop(false, imagesY);
                    try {
                        ImageIO.write(imagesZoom, formatName, new File(fileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    //下载函数
    private void downLoad(DownFile downFile){
        tilePool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //如果文件不存在 则进行瓦片下载
                    if (!new File(downFile.getFileName()).exists()) {
                        //是否剪裁
                        if (isCrop) {
                            byte[] content = executor.execute(Request.Get(downFile.getUrl()).connectTimeout(5000).socketTimeout(5000)).returnContent().asBytes();
                            //将字节作为输入流
                            ByteArrayInputStream in = new ByteArrayInputStream(content);
                            //字节转带缓冲区的图像
                            BufferedImage image = ImageIO.read(in);
                            //按边界裁剪
                            BufferedImage temp = image.getSubimage(0, 0, 256, 256);
                            ImageIO.write(temp, formatName, new File(downFile.getFileName()));
                            updateProgress(downFile.getId());
                        } else {
                            byte[] content = executor.execute(Request.Get(downFile.getUrl()).connectTimeout(5000).socketTimeout(5000)).returnContent().asBytes();
                            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(downFile.getFileName())))) {
                                out.write(content);
                                updateProgress(downFile.getId());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    /**
     * 更新表格进度条
     */
    private synchronized void updateProgress(String id) {
        //获取数据长度
        Long num = (Long) jedis.llen("taskList");
        //遍历
        for (int i = 0; i < num; i++) {
            List<String> values = jedis.lrange("taskList", i, i);
            //json字符串转对象
            DownTask downTask = JSONObject.parseObject(values.get(0), DownTask.class);
            if (id.equals(downTask.getId())) {
                Task task = downTask.getTask();
                int current = task.getTileCurrentNum();
                current++;
                task.setTileCurrentNum(current);
                downTask.setTask(task);
                String downTaskString = JSON.toJSONString(downTask);
                jedis.lset("taskList", i, downTaskString);
                int progress = current/task.getTileNum()*100;
                int row = i;
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DialogDownLoadTask.model.setProgress(row,progress);
                        DialogDownLoadTask.table.updateUI();
                    }
                });
            }
        }
    }
    //合并图片函数
    private BufferedImage tileCrop(Boolean flag, ArrayList images) {
        BufferedImage image;
        //如果为true,则进行垂直合并,否则进行水平合并
        if (flag) {
            //宽度数组
            ArrayList width = new ArrayList();
            //高度数组
            ArrayList height = new ArrayList();
            //RGB二维数组
            ArrayList rgb = new ArrayList();
            //获取宽高数组,rgb数组
            for (int i = 0; i < images.size(); i++) {
                BufferedImage temp = (BufferedImage) images.get(i);
                int w = temp.getWidth();
                int h = temp.getHeight();
                width.add(w);
                height.add(h);
                //从图片中读取RGB
                int[] arry = new int[w * h];
                //逐行扫描图像中各个像素的RGB 存到数组中
                arry = temp.getRGB(0, 0, w, h, arry, 0, w);
                rgb.add(arry);
            }
            //获取总高度
            int totalH = 0;
            for (int i = 0; i < height.size(); i++) {
                totalH += (int) height.get(i);
            }
            //创建内存image对象
            image = new BufferedImage((Integer) width.get(0), totalH, BufferedImage.TYPE_INT_RGB);
            for (int i = rgb.size() - 1; i >= 0; i--) {
                if (i == rgb.size() - 1) {
                    image.setRGB(0, 0, (int) width.get(i), (int) height.get(i), (int[]) rgb.get(i), 0, (int) width.get(i));
                } else {
                    image.setRGB(0, (int) height.get(i) * (rgb.size() - 1 - i), (int) width.get(i), (int) height.get(i), (int[]) rgb.get(i), 0, (int) width.get(i));
                }
            }
        } else {
            //宽度数组
            ArrayList width = new ArrayList();
            //高度数组
            ArrayList height = new ArrayList();
            //RGB二维数组
            ArrayList rgb = new ArrayList();

            //获取宽高数组,rgb数组
            for (int i = 0; i < images.size(); i++) {
                BufferedImage temp = (BufferedImage) images.get(i);
                int w = temp.getWidth();
                int h = temp.getHeight();
                width.add(w);
                height.add(h);
                //从图片中读取RGB
                int[] arry = new int[w * h];
                //逐行扫描图像中各个像素的RGB 存到数组中
                arry = temp.getRGB(0, 0, w, h, arry, 0, w);
                rgb.add(arry);
            }
            //获取总宽度
            int totalW = 0;
            for (int i = 0; i < width.size(); i++) {
                totalW += (int) width.get(i);
            }
            //创建内存image对象
            image = new BufferedImage(totalW, (Integer) height.get(0), BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < rgb.size(); i++) {
                if (i == 0) {
                    image.setRGB(0, 0, (int) width.get(i), (int) height.get(i), (int[]) rgb.get(i), 0, (int) width.get(i));
                } else {
                    image.setRGB((int) width.get(i) * i, 0, (int) width.get(i), (int) height.get(i), (int[]) rgb.get(i), 0, (int) width.get(i));
                }
            }
        }
        return image;
    }
}
