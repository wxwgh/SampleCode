package com.supermap.desktop.download.ui;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.supermap.desktop.controls.ui.controls.SmDialog;
import com.supermap.desktop.download.bean.DownFile;
import com.supermap.desktop.download.bean.DownTask;
import com.supermap.desktop.download.bean.Task;
import com.supermap.desktop.download.config.MapUrl;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.impl.client.HttpClientBuilder;
import redis.clients.jedis.Jedis;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DialogVectorLoad extends SmDialog {

    public static Jedis jedis = new Jedis("localhost");
    private static String taskName;
    private static String oldTaskName="OSM矢量下载";
    //下载路径
    private static String filePath = "D:\\SuperMap DownLoad";
    //文件类型
    private String type = ".osm";
    private String formatName = "osm";
    private static int fileNum = 0;
    private static int fileCurrentNum = 0;

    public DialogVectorLoad() {
        super();
        //设置弹窗宽度和高度
        this.setSize(new Dimension(500, 500));
        //初始化对话框布局管理器
        this.setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
        this.setResizable(false);
        this.setTitle("矢量下载");
        //初始化线程池
        initPool();
        //初始化
        initPanel();
    }
    //瓦片下载线程池
    private static ExecutorService vectorPool = null;
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
        if (this.vectorPool == null) {
            this.vectorPool = Executors.newFixedThreadPool(MapUrl.tilePoolSize);
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
    //初始化弹窗
    private void initPanel(){
        Box parentBox = Box.createVerticalBox();
        Component header = Box.createVerticalStrut(30);
        parentBox.add(header);
        //创建横向容器 任务名称行
        Box taskNameBox = Box.createHorizontalBox();
        JLabel taskNameLabel = new JLabel("任务名称");
        JTextField taskNameField = new JTextField("OSM矢量下载",24);
        //创建固定宽度不可见组件
        Component leftTemp = Box.createHorizontalStrut(10);
        Component centerTemp = Box.createHorizontalStrut(10);
        Component rightTemp = Box.createHorizontalStrut(122);
        taskNameBox.add(leftTemp);
        taskNameBox.add(taskNameLabel);
        taskNameBox.add(centerTemp);
        taskNameBox.add(taskNameField);
        taskNameBox.add(rightTemp);
        parentBox.add(taskNameBox);
        Component bottom = Box.createVerticalStrut(16);
        parentBox.add(bottom);
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
        parentBox.add(catalogBox);
        Component bottom2 = Box.createVerticalStrut(30);
        parentBox.add(bottom2);

        //创建横向容器 保存格式
        Box saveBox = Box.createHorizontalBox();
        JLabel saveLabel = new JLabel("保存格式");
        Component leftTemp3 = Box.createHorizontalStrut(10);
        Component rightTemp3 = Box.createHorizontalGlue();
        saveBox.add(leftTemp3);
        saveBox.add(saveLabel);
        saveBox.add(rightTemp3);
        parentBox.add(saveBox);


        //创建横向容器 保存格式按钮组
        Box vectorButtonBox = Box.createHorizontalBox();
        ButtonGroup vectorButtonGroup = new ButtonGroup();
        JRadioButton vectorJRadio = new JRadioButton(".shp");
        vectorJRadio.setSelected(true);
        JRadioButton vectorJRadio2 = new JRadioButton(".udb");
        JRadioButton vectorJRadio3 = new JRadioButton(".udbx");
        JRadioButton vectorJRadio4 = new JRadioButton(".osm");
        Component leftTemp5 = Box.createHorizontalStrut(5);
        Component rightTemp5 = Box.createHorizontalGlue();
        vectorButtonGroup.add(vectorJRadio);
        vectorButtonGroup.add(vectorJRadio2);
        vectorButtonGroup.add(vectorJRadio3);
        vectorButtonGroup.add(vectorJRadio4);
        vectorButtonBox.add(leftTemp5);
        vectorButtonBox.add(vectorJRadio);
        vectorButtonBox.add(vectorJRadio2);
        vectorButtonBox.add(vectorJRadio3);
        vectorButtonBox.add(vectorJRadio4);
        vectorButtonBox.add(rightTemp5);
        parentBox.add(vectorButtonBox);
        Component bottom3 = Box.createVerticalStrut(30);
        parentBox.add(bottom3);
        //创建横向容器 文字提示
        Box hintBox = Box.createHorizontalBox();
        JLabel hintLabel = new JLabel("注:目前仅提供OpenStreetMap开发数据下载");
        Component leftTemp4 = Box.createHorizontalStrut(10);
        Component rightTemp4 = Box.createHorizontalGlue();
        hintBox.add(leftTemp4);
        hintBox.add(hintLabel);
        hintBox.add(rightTemp4);
        parentBox.add(hintBox);
        Component bottom4 = Box.createVerticalStrut(365);
        parentBox.add(bottom4);

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
        parentBox.add(affirmBox);

        //底部
        Component bottom5 = Box.createVerticalStrut(10);
        parentBox.add(bottom5);

        this.add(parentBox);

        DialogVectorLoad thisTemp =this;
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
        vectorJRadio.addActionListener(listenType);
        vectorJRadio2.addActionListener(listenType);
        vectorJRadio3.addActionListener(listenType);
        vectorJRadio4.addActionListener(listenType);
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
    //保存格式 单选框事件
    private final ActionListener listenType = e -> {
        type = e.getActionCommand();
        formatName = e.getActionCommand().substring(1);
        System.out.println(formatName);
    };
    /**
     * 将下载任务加入队列
     */
    private void addDownLoad(String id) {
        downTaskPool.execute(new Runnable() {
            @Override
            public void run() {
                String fileParent = filePath+File.separator+taskName;
                File file = new File(fileParent);
                if(!file.exists()){
                    file.mkdirs();
                }
                String fileName = fileParent+File.separator+"map"+type;
                String bounds = MapUrl.northwestOSM.getLng()+","+MapUrl.northwestOSM.getLat()+","+MapUrl.southeastOSM.getLng()+","+MapUrl.southeastOSM.getLat();
                String url = MapUrl.VectorUrl.replace("{bounds}",bounds);
                System.out.println(url);
                DownFile downFile = new DownFile(url,fileName,id);
                downLoad(downFile);
            }
        });
    }
    /**
     * 更新表格进度条
     */
    private synchronized void updateProgress(String id) {
        Jedis jedis = new Jedis("localhost");
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
    //下载函数
    private void downLoad(DownFile downFile){
        vectorPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //如果文件不存在 则进行矢量下载
                    if (!new File(downFile.getFileName()).exists()) {
                        Response response = executor.execute(Request.Get(downFile.getUrl()).connectTimeout(10000).socketTimeout(10000));
                        byte[] content = response.returnContent().asBytes();

                        try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(downFile.getFileName())))) {
                            out.write(content);
                            updateProgress(downFile.getId());
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private static String mapType="osm";
    private static String mapName="矢量";
    /**
     * 将下载任务信息存入redis中
     */
    private void addTask(String id) {
        Task task = new Task();
//        String bounds = MapUrl.northwestOSM.getLng()+","+MapUrl.northwestOSM.getLat()+","+MapUrl.southeastOSM.getLng()+","+MapUrl.southeastOSM.getLat();
//        String url = MapUrl.VectorUrl.replace("{bounds}",bounds);
//        try {
//            Response response = executor.execute(Request.Get(url).connectTimeout(10000).socketTimeout(10000));
//            HttpResponse httpResponse = response.returnResponse();
//            //获取状态码
//            int code = httpResponse.getStatusLine().getStatusCode();
//            if(code==200){
//                //获取文件最大值
//                fileNum = (int) httpResponse.getEntity().getContentLength();
//                System.out.println(fileNum);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        //任务名称
        task.setTaskName(taskName);
        //地图类型
        task.setMapType(mapType);
        //地图名称
        task.setMapName(mapName);
        //当前下载瓦片数量
        task.setTileCurrentNum(fileCurrentNum);
        //瓦片总数
        task.setTileNum(1);
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
}
