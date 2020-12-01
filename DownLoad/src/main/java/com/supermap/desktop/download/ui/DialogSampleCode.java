package com.supermap.desktop.download.ui;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.supermap.desktop.controls.ui.controls.SmDialog;
import com.supermap.desktop.controls.ui.controls.button.SmButton;
import com.supermap.desktop.controls.utilities.ComponentFactory;
import com.supermap.desktop.core.ui.controls.GridBagConstraintsHelper;
import com.supermap.desktop.core.utilties.DefaultValues;
import com.supermap.desktop.download.bean.DownFile;
import com.supermap.desktop.download.bean.Tile;
import com.supermap.desktop.download.config.MapUrl;
import com.supermap.desktop.download.util.CoordinateToolkit;
import com.supermap.desktop.download.util.CoordinateToolkit2;
import org.apache.http.client.fluent.Request;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.*;

import org.apache.http.client.fluent.Executor;
import org.apache.http.impl.client.HttpClientBuilder;

/**
 * @author SuperMap
 */
public class DialogSampleCode extends SmDialog{


	private SmButton buttonOK;
	private SmButton buttonCancel;
	private JPanel YXPanel;
	private JLabel nameLabel;
	private JLabel catalogLabel;
	private JLabel saveLabel;
	private JTextField nameText;
	private JTextField catalogText;
	private JButton fileButon;
	private JCheckBox cropCheckBox;

	private ButtonGroup saveGroup;
	private JRadioButton tile;
	private JRadioButton join;

	private ButtonGroup tileGroup;
	private JRadioButton tilePng;
	private JRadioButton tileJpg;
    private JPanel tilePanel;
    private JPanel joinPanel;

	private ButtonGroup joinGroup;
	private JRadioButton joinTif;
	private JRadioButton joinBmp;
	private JRadioButton joinImg;
	private JRadioButton joinPng;
	private JRadioButton joinJpg;

	private JLabel levelLabel;
	private JPanel levelPanel;
	private JPanel levelTablePanel;
	private JCheckBox levelCheckBox;
	private JTable levelTable;

	private ColorTableModel model = new ColorTableModel();
	//是否边界裁剪
    private static boolean isCrop = true;
    //是否拼接大图
    private static boolean isJoin = false;
    //文件类型
    private static String type = ".png";
    private static String formatName = "png";
    //任务名称
    private static String taskName;
    private static String oldTaskName = "影像地图下载";
    //下载路径
    private static String filePath = "D:/SuperMap DownLoad";
    //选取级别
    private ArrayList level = new ArrayList();
    private Vector<Object> rowData = new Vector<Object>();
    //瓦片下载线程池
    private static ExecutorService tilePool = null;
    //下载任务线程池
    private static ExecutorService downTaskPool = null;
    private static ArrayBlockingQueue tileQueue = null;
    private static Executor executor = null;

	public DialogSampleCode() {
		super();
		//设置弹窗宽度和高度
		this.setSize(new Dimension(500, 500));
		//初始化组件
		initComponents();
		//初始化线程池 执行器相关内容
        initPool();
		//更新表格内容
        initTable();
        //初始化布局
        initLayout();
        //初始化文本内容
        initResources();
        //绑定事件
        registerEvents();
	}
    /**
     * 初始化线程池 相关内容
     */
    private void initPool(){
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
            //启动多个线程 处理瓦片下载
            for (int i = 0; i < MapUrl.tilePoolSize; i++) {
                this.tilePool.execute(() -> this.downLoad());
            }
        }
        /**
         * 初始化队列
         * ArrayBlockingQueue是一个基于数组的阻塞队列
         * 先进先出队列
         * 有界队列 初始化时指定的容量,就是队列最大的容量,不会出现扩容,容量满,阻塞进队操作,容量空,阻塞出队操作
         * 队列不支持空元素
         */
        if(tileQueue == null) {
            //设置队列大小,为线程池的3倍即可
            tileQueue = new ArrayBlockingQueue<>(MapUrl.tilePoolSize*3);
        }
        /**
         * 初始化 apache快速构建HTTP请求 执行器
         * 所有请求会使用一个公共的连接池,共200个连接
         * executor实际上 相当于一个线程池实例
         */
        if(executor == null) {
            executor = Executor.newInstance(
                    HttpClientBuilder.create()
                            .setRetryHandler((exception, executionCount, context) -> {
                                return executionCount < MapUrl.retryNum;
                            })
                            .build());
        }
    }
    //初始化组件
	private void initComponents() {
        //创建一个确定按钮
        this.buttonOK = ComponentFactory.createButtonOK();
        //创建一个取消按钮
        this.buttonCancel = ComponentFactory.createButtonCancel();
        //创建影像面板
        this.YXPanel = new JPanel();
        //任务名称 单行文本
        this.nameLabel = new JLabel();
        //下载目录 单行文本
        this.catalogLabel = new JLabel();
        //任务名称 输入框
        this.nameText = new JTextField(24);
        //下载目录 输入框
        this.catalogText = new JTextField(24);
        //路径选取 按钮
        this.fileButon = new JButton();
        //创建复选框
        this.cropCheckBox = new JCheckBox();
        //保存格式
        this.saveLabel = new JLabel();
        //保存格式 按钮组
        this.saveGroup = new ButtonGroup();
        //单选按钮
        this.tile = new JRadioButton();
        this.join = new JRadioButton();
        //瓦片 按钮组
        this.tileGroup = new ButtonGroup();
        this.tileJpg = new JRadioButton();
        this.tilePng = new JRadioButton();
        //按钮面板
        this.tilePanel=new JPanel();
        //拼接大图 按钮组
        this.joinGroup = new ButtonGroup();
        this.joinBmp = new JRadioButton();
        this.joinJpg = new JRadioButton();
        this.joinPng = new JRadioButton();
        this.joinTif = new JRadioButton();
        this.joinPanel=new JPanel();

        //选取级别面板
        this.levelPanel = new JPanel();
        //下载级别 单行文本
        this.levelLabel = new JLabel();
        //全选
        this.levelCheckBox = new JCheckBox();
        //表格面板
        this.levelTablePanel = new JPanel();


    }
    //更新表格内容
    private void initTable(){
	    if(MapUrl.mapType.equals("baidu")){
            Tile tile1 = new Tile();
            Tile tile2 = new Tile();
            for(int i =3;i<MapUrl.BaiduScale.length;i++){
                long tileNum = 0;
                // 经纬度转瓦片
                CoordinateToolkit.bd09_LngLat_To_Tile(MapUrl.northwest,i+1,tile1);
                CoordinateToolkit.bd09_LngLat_To_Tile(MapUrl.southeast,i+1,tile2);
                int minX = tile1.getX() < tile2.getX() ? tile1.getX() : tile2.getX();
                int minY = tile1.getY() < tile2.getY() ? tile1.getY() : tile2.getY();
                int maxX = tile1.getX() > tile2.getX() ? tile1.getX() : tile2.getX();
                int maxY = tile1.getY() > tile2.getY() ? tile1.getY() : tile2.getY();
                tileNum += (maxX - minX + 1)*(maxY - minY + 1);
                long temp = tileNum*30;
                String tileWeight = "";
                if(temp<1024){
                    tileWeight=temp+"KB";
                }else if(temp>=1024&&temp<1048576){
                    float num = (float)temp/1024;
                    DecimalFormat df = new DecimalFormat("0.00");//保留2位小数
                    tileWeight = df.format(num)+"MB";
                }else if(temp>=1048576&&temp<1073741824){
                    float num = (float)temp/1024/1024;
                    DecimalFormat df = new DecimalFormat("0.00");//保留2位小数
                    tileWeight = df.format(num)+"GB";
                }else{
                    float num = (float)temp/1024/1024/1024;
                    DecimalFormat df = new DecimalFormat("0.00");//保留2位小数
                    tileWeight = df.format(num)+"T";
                }
                this.rowData.add(new Object[] { Boolean.FALSE,i+1,MapUrl.BaiduScale[i],"256x256",tileNum,tileWeight});
            }
        }
    };
    //初始化布局
	private void initLayout() {
	    //初始化面板布局
        this.YXPanel.setLayout(new GridBagLayout());
        this.tilePanel.setLayout(new GridBagLayout());
        this.joinPanel.setLayout(new GridBagLayout());
        this.levelPanel.setLayout(new GridBagLayout());
        this.levelTablePanel.setLayout(new GridBagLayout());
        //设置文本 默认大小
        this.nameLabel.setPreferredSize(DefaultValues.getLabelDefaultSize());
        this.catalogLabel.setPreferredSize(DefaultValues.getLabelDefaultSize());
        this.saveLabel.setPreferredSize(DefaultValues.getLabelDefaultSize());
        this.levelLabel.setPreferredSize(DefaultValues.getLabelDefaultSize());
        //按钮去除焦点
        this.fileButon.setFocusPainted(false);
        this.buttonOK.setFocusPainted(false);
        this.buttonCancel.setFocusPainted(false);
        //设置复选框选中
        this.cropCheckBox.setSelected(true);
        //设置单选框选中
        this.tile.setSelected(true);
        this.isJoin=false;
        this.tilePng.setSelected(true);
        this.joinTif.setSelected(true);
        //隐藏拼接大图面板
        this.joinPanel.setVisible(false);
        //按钮分组
        this.saveGroup.add(this.tile);
        this.saveGroup.add(this.join);
        this.tileGroup.add(this.tileJpg);
        this.tileGroup.add(this.tilePng);
        this.joinGroup.add(this.joinBmp);
        this.joinGroup.add(this.joinJpg);
        this.joinGroup.add(this.joinPng);
        this.joinGroup.add(this.joinTif);
        //单选框添加进面板
        this.tilePanel.add(this.tilePng,new GridBagConstraintsHelper(0, 0, 1, 1).setInsets(0, 15, 0, 0).setAnchor(GridBagConstraints.WEST));
        this.tilePanel.add(this.tileJpg,new GridBagConstraintsHelper(1, 0, 1, 1).setInsets(0, 15, 0, 0).setAnchor(GridBagConstraints.WEST));
        this.joinPanel.add(this.joinTif,new GridBagConstraintsHelper(0, 0, 1, 1).setInsets(0, 15, 0, 0).setAnchor(GridBagConstraints.WEST));
        this.joinPanel.add(this.joinBmp,new GridBagConstraintsHelper(1, 0, 1, 1).setInsets(0, 15, 0, 0).setAnchor(GridBagConstraints.WEST));
        this.joinPanel.add(this.joinPng,new GridBagConstraintsHelper(3, 0, 1, 1).setInsets(0, 15, 0, 0).setAnchor(GridBagConstraints.WEST));
        this.joinPanel.add(this.joinJpg,new GridBagConstraintsHelper(4, 0, 1, 1).setInsets(0, 15, 0, 0).setAnchor(GridBagConstraints.WEST));
        //添加组件进影像面板中
        this.YXPanel.add(this.nameLabel, new GridBagConstraintsHelper(0, 0, 1, 1).setInsets(5, 15, 0, 0));
        this.YXPanel.add(this.nameText, new GridBagConstraintsHelper(1, 0, 1, 1).setInsets(5, 0, 0, 0));
        this.YXPanel.add(this.catalogLabel, new GridBagConstraintsHelper(0, 1, 1, 1).setInsets(5, 15, 0, 0));
        this.YXPanel.add(this.catalogText, new GridBagConstraintsHelper(1, 1, 1, 1).setInsets(5, 0, 0, 0));
        this.YXPanel.add(this.fileButon,new GridBagConstraintsHelper(2, 1, 1, 1).setInsets(5, 5, 0, 0));
        this.YXPanel.add(this.cropCheckBox,new GridBagConstraintsHelper(0, 2, 1, 1).setInsets(10, 5, 0, 0));
        this.YXPanel.add(this.saveLabel,new GridBagConstraintsHelper(0, 3, 1, 1).setInsets(10, 15, 0, 0));
        this.YXPanel.add(this.tile,new GridBagConstraintsHelper(1, 3, 1, 1).setInsets(15, 0, 0, 0).setAnchor(GridBagConstraints.WEST));
        this.YXPanel.add(this.join,new GridBagConstraintsHelper(1, 3, 1, 1).setInsets(15, 0, 0, 0).setAnchor(GridBagConstraints.CENTER));
        //选取级别文本框,多选框添加进面板
        this.levelPanel.add(this.levelLabel,new GridBagConstraintsHelper(0, 0, 1, 1).setInsets(0, 15, 0, 0).setAnchor(GridBagConstraints.WEST));
		this.levelPanel.add(new JPanel(), new GridBagConstraintsHelper(1, 0, 1, 1).setInsets(0, 0, 0, 0).setWeight(1, 0).setAnchor(GridBagConstraints.CENTER));
        this.levelPanel.add(this.levelCheckBox,new GridBagConstraintsHelper(2, 0, 1, 1).setInsets(0, 0, 0, 0).setAnchor(GridBagConstraints.SOUTH));
        //创建表格
        this.model.setRowData(this.rowData);
        this.levelTable = new JTable(this.model);
        // 设置行高
        this.levelTable.setRowHeight(40);
        // 设置不允许手动改变列宽
        this.levelTable.getTableHeader().setResizingAllowed(false);
        //内容居中
        DefaultTableCellRenderer render = new DefaultTableCellRenderer();
        render.setHorizontalAlignment(SwingConstants.CENTER);
        this.levelTable.getColumnModel().getColumn(1).setCellRenderer(render);
        this.levelTable.getColumnModel().getColumn(2).setCellRenderer(render);
        this.levelTable.getColumnModel().getColumn(3).setCellRenderer(render);
        this.levelTable.getColumnModel().getColumn(4).setCellRenderer(render);
        this.levelTable.getColumnModel().getColumn(5).setCellRenderer(render);
        //设置列宽
        this.levelTable.getColumnModel().getColumn(0).setPreferredWidth(10);
        this.levelTable.getColumnModel().getColumn(1).setPreferredWidth(10);
        //设置 不可以调整此列大小
        this.levelTable.getColumnModel().getColumn(0).setResizable(false);
        this.levelTable.getColumnModel().getColumn(1).setResizable(false);
        //设置表头居中
        this.levelTable.getTableHeader().setDefaultRenderer(render);
        // 设置不允许拖动重新排序各列
        this.levelTable.getTableHeader().setReorderingAllowed(false);
        // 设置滚动面板视口大小（超过该大小的行数据，需要拖动滚动条才能看到）
        this.levelTable.setPreferredScrollableViewportSize(new Dimension(685, 300));
        // 把 表格 放到 滚动面板 中（表头将自动添加到滚动面板顶部）
        JScrollPane scrollPane = new JScrollPane(this.levelTable);
        //将表格添加进 表格面板
        this.levelTablePanel.add(scrollPane,new GridBagConstraintsHelper(0, 0, 1, 1).setInsets(0, 10, 0, 0).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.WEST));
        //设置对话窗口的 布局管理器
		this.setLayout(new GridBagLayout());
		this.setResizable(false);
		//添加影像面板到对话窗
		this.add(this.YXPanel,new GridBagConstraintsHelper(0, 0, 1, 1).setInsets(10, 10, 0, 10).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
		this.add(this.tilePanel,new GridBagConstraintsHelper(0, 1, 1, 1).setInsets(10, 10, 0, 10).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.WEST));
		this.add(this.joinPanel,new GridBagConstraintsHelper(0, 1, 1, 1).setInsets(10, 10, 0, 10).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.WEST));
		this.add(this.levelPanel,new GridBagConstraintsHelper(0, 2, 1, 1).setInsets(10, 10, 0, 10).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.WEST));
		this.add(this.levelTablePanel,new GridBagConstraintsHelper(0, 3, 1, 1).setInsets(0, 10, 0, 10).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.WEST));
        //添加空面板容器
		this.add(new JPanel(), new GridBagConstraintsHelper(0, 3, 1, 1).setInsets(0, GridBagConstraintsHelper.FRAME_CONTROL_GAP, 0, GridBagConstraintsHelper.FRAME_CONTROL_GAP).setFill(GridBagConstraints.BOTH).setWeight(1, 1));
        //创建按钮面板
		this.add(ComponentFactory.createButtonPanel(this.buttonOK, this.buttonCancel), new GridBagConstraintsHelper(0, 4, 1, 1).setInsets(0, GridBagConstraintsHelper.FRAME_CONTROL_GAP, GridBagConstraintsHelper.FRAME_CONTROL_GAP, GridBagConstraintsHelper.FRAME_CONTROL_GAP).setWeight(1, 0).setFill(GridBagConstraints.HORIZONTAL));
	}

	//设置文字内容
    private void initResources() {
	    //设置对话窗标题
		this.setTitle("影像地图下载");
        //设置组件显示文本
		this.nameLabel.setText("任务名称");
		this.catalogLabel.setText("下载目录");
        this.fileButon.setText("...");
        this.cropCheckBox.setText("按边界裁剪");
        this.saveLabel.setText("保存格式");
        this.tile.setText("瓦片");
        this.join.setText("拼接大图");
        this.tilePng.setText(".png");
        this.tileJpg.setText(".jpg");
        this.joinTif.setText(".tif");
        this.joinPng.setText(".png");
        this.joinJpg.setText(".jpg");
        this.joinBmp.setText(".bmp");
        this.levelLabel.setText("选取级别");
        this.levelCheckBox.setText("全选");
        this.nameText.setText("影像地图下载");
        this.catalogText.setText("D:/SuperMap DownLoad");
	}
	//绑定事件
    private void registerEvents() {
        this.cropCheckBox.addActionListener(this.listenCropCheckBox);
        this.levelCheckBox.addActionListener(this.listenLevelCheckBox);
        this.tile.addActionListener(this.listenTile);
        this.join.addActionListener(this.listenJoin);
        this.fileButon.addActionListener(this.listenFileButton);
        this.buttonOK.addActionListener(this.actionListenerOK);
        this.buttonCancel.addActionListener(this.actionListenerCancel);
        this.tilePng.setActionCommand(".png");
        this.tileJpg.setActionCommand(".jpg");
        this.joinTif.setActionCommand(".tif");
        this.joinPng.setActionCommand(".png");
        this.joinJpg.setActionCommand(".jpg");
        this.joinBmp.setActionCommand(".bmp");
        this.tilePng.addActionListener(listenType);
        this.tileJpg.addActionListener(listenType);
        this.joinTif.addActionListener(listenType);
        this.joinPng.addActionListener(listenType);
        this.joinJpg.addActionListener(listenType);
        this.joinBmp.addActionListener(listenType);
        this.levelTable.addMouseListener(listenTable);
        Document document = this.nameText.getDocument();
        document.addDocumentListener(listenName);
        Document document2 = this.catalogText.getDocument();
        document2.addDocumentListener(listenCatalog);


    }
    //目录路径输入框 内容改变事件
    private final DocumentListener listenCatalog = new DocumentListener() {
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
    };
    //任务名称输入框 内容改变事件
    private final DocumentListener listenName = new DocumentListener() {
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
    };
    //任意单元格绑定 点击事件
    private final MouseAdapter listenTable = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
            int row = levelTable.getSelectedRow();
            int column = levelTable.getSelectedColumn();
            Object obj = levelTable.getValueAt(row, column);
            if(obj.equals(Boolean.TRUE)){
                level.add(levelTable.getValueAt(row, 1));
            }else{
                level.remove(levelTable.getValueAt(row, 1));
            }
        }
    };
    //文件格式单选框 点击事件
    private final ActionListener listenType = e -> {
       type = e.getActionCommand();
       formatName = e.getActionCommand().substring(1);
        System.out.println(formatName);
    };
	//全选按钮 点击事件
    private final ActionListener listenLevelCheckBox = e -> {
        if(this.levelCheckBox.isSelected()==false){
            this.levelCheckBox.setSelected(false);
            int rowCount = this.levelTable.getRowCount();
            Object value = Boolean.FALSE;
            for(int i =0;i<rowCount;i++){
                this.levelTable.setValueAt(value,i,0);
                Object valueAt = this.levelTable.getValueAt(i, 1);
                this.level.remove(valueAt);
            }
            this.levelTable.updateUI();
        }else{
            this.levelCheckBox.setSelected(true);
            int rowCount = this.levelTable.getRowCount();
            Object value = Boolean.TRUE;
            for(int i =0;i<rowCount;i++){
                this.levelTable.setValueAt(value,i,0);
                Object valueAt = this.levelTable.getValueAt(i, 1);
                if(!this.level.contains(valueAt)){
                    this.level.add(valueAt);
                }
            }
            this.levelTable.updateUI();
        }

    };


    //...按钮 点击事件
    private final ActionListener listenFileButton = e ->{
        JFileChooser jf = new JFileChooser();
        jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jf.showOpenDialog(this);
        //获取选择的文件
        File f =  jf.getSelectedFile();
        //返回路径名
        String s = f.getAbsolutePath();
        this.catalogText.setText(s);
        this.filePath = s;
    };
    //边界剪裁多选框 交互逻辑
    private final ActionListener listenCropCheckBox = e -> {
	    if(this.cropCheckBox.isSelected()==false){
            this.cropCheckBox.setSelected(false);
            this.isCrop = false;
        }else{
            this.cropCheckBox.setSelected(true);
            this.isCrop=true;
        }

    };
	//瓦片单选框 点击事件
    private final ActionListener listenTile = e -> {
        this.joinPanel.setVisible(false);
        this.tilePanel.setVisible(true);
        this.isJoin=false;
        if(tilePng.isSelected()==true){
            type = tilePng.getActionCommand();
            formatName=tilePng.getActionCommand().substring(1);
        }else if(tileJpg.isSelected()==true){
            type = tileJpg.getActionCommand();
            formatName=tileJpg.getActionCommand().substring(1);
        }
    };
    //拼接大图单选框 点击事件
    private final ActionListener listenJoin = e -> {
        this.joinPanel.setVisible(true);
        this.tilePanel.setVisible(false);
        this.isJoin=true;
        if(joinTif.isSelected()==true){
            type = joinTif.getActionCommand();
            formatName=joinTif.getActionCommand().substring(1);
        }else if(joinPng.isSelected()==true){
            type = joinPng.getActionCommand();
            formatName=joinPng.getActionCommand().substring(1);
        }else if(joinJpg.isSelected()==true){
            type = joinJpg.getActionCommand();
            formatName=joinJpg.getActionCommand().substring(1);
        }else if(joinImg.isSelected()==true){
            type = joinImg.getActionCommand();
            formatName=joinImg.getActionCommand().substring(1);
        }else if(joinBmp.isSelected()==true){
            type = joinBmp.getActionCommand();
            formatName=joinBmp.getActionCommand().substring(1);
        }
    };
    private final ActionListener actionListenerOK = e -> {
        //将下载任务加入队列
        addDownLoad();
        dispose();
    };

    /**
     * 将下载任务加入队列
     */
    private void addDownLoad(){
        downTaskPool.execute(() -> {
            // 提前创建对象，重复利用，下面的循环次数可能到达百万次甚至千万次，每次创建对象效率太低
            Tile tile1 = new Tile();
            Tile tile2 = new Tile();
            int minX = 0;
            int minY = 0;
            int maxX = 0;
            int maxY = 0;
            File folder = null;
            String fileName="";
            String url;
            StringBuilder sb = new StringBuilder();
            //重命名文件夹
            renameFile();
            for (int i=0;i<level.size();i++) {
                CoordinateToolkit.bd09_LngLat_To_Tile(MapUrl.northwest,(int)level.get(i),tile1);
                CoordinateToolkit.bd09_LngLat_To_Tile(MapUrl.southeast,(int)level.get(i),tile2);
                minX = tile1.getX() < tile2.getX() ? tile1.getX() : tile2.getX();
                minY = tile1.getY() < tile2.getY() ? tile1.getY() : tile2.getY();
                maxX = tile1.getX() > tile2.getX() ? tile1.getX() : tile2.getX();
                maxY = tile1.getY() > tile2.getY() ? tile1.getY() : tile2.getY();
                ArrayList imagesY = new ArrayList();
                for (int x = minX; x <= maxX; x++) {
                    ArrayList imagesX = new ArrayList();
                    sb.setLength(0);
                    if(isJoin){
                        sb.append(this.filePath).append(File.separator).append(this.taskName).append(File.separator).append(level.get(i));
                    }else{
                        sb.append(this.filePath).append(File.separator).append(this.taskName).append(File.separator).append(level.get(i)).append(File.separator).append(x);
                    }
                    folder = new File(sb.toString());
                    if (!folder.exists()) {
                        folder.mkdirs();
                    }
                    for (int y = minY; y <= maxY; y++) {
                        // 允许下载
                        sb.setLength(0);
                        if(isJoin){
                            fileName = sb.append(folder.getPath()).append(File.separator).append(taskName).append(type).toString();
                        }else{
                            fileName = sb.append(folder.getPath()).append(File.separator).append(y).append(this.type).toString();
                        }
                        url = MapUrl.mapUrl.replace("{x}", String.valueOf(x)).replace("{y}", String.valueOf(y)).replace("{z}", String.valueOf(level.get(i)));
                        if(isJoin){
                            try {
                                //如果文件不存在 则进行瓦片下载
                                if(!new File(fileName).exists()){
                                    //是否剪裁
                                    if(this.isCrop){
                                        byte[] content = executor.execute(Request.Get(url).connectTimeout(5000).socketTimeout(5000)).returnContent().asBytes();
                                        //将字节作为输入流
                                        ByteArrayInputStream in = new ByteArrayInputStream(content);
                                        //字节转带缓冲区的图像
                                        BufferedImage image = ImageIO.read(in);
                                        //按边界裁剪
                                        BufferedImage temp = image.getSubimage(0,0,256,256);
                                        imagesX.add(temp);
                                    }else{
                                        byte[] content = executor.execute(Request.Get(url).connectTimeout(5000).socketTimeout(5000)).returnContent().asBytes();
                                        //将字节作为输入流
                                        ByteArrayInputStream in = new ByteArrayInputStream(content);
                                        //字节转带缓冲区的图像
                                        BufferedImage image = ImageIO.read(in);
                                        imagesX.add(image);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else{
                            try {
                                //将下载任务加入队列,如果tileQueue没有空间,则调用此方法的线程被阻塞,直到队列有空间再继续执行
                                tileQueue.put(new DownFile(url,fileName));
                            } catch (InterruptedException e) {
                                /**
                                 * 只对阻塞线程起作用
                                 * 当线程阻塞时,调用interrupt中断该线程
                                 */
                                Thread.currentThread().interrupt();
                            }
                        }

                    }
                    if(isJoin){
                        BufferedImage imagesPS = tileCrop(true,imagesX);
                        imagesY.add(imagesPS);
                    }
                }
                if(isJoin){
                    BufferedImage imagesZoom = tileCrop(false,imagesY);
                    try {
                        ImageIO.write(imagesZoom, formatName, new File(fileName));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    };
    //合并图片函数
    private BufferedImage tileCrop(Boolean flag,ArrayList images){
        BufferedImage image;
        //如果为true,则进行垂直合并,否则进行水平合并
        if(flag){
            //宽度数组
            ArrayList width = new ArrayList();
            //高度数组
            ArrayList height = new ArrayList();
            //RGB二维数组
            ArrayList rgb = new ArrayList();
            //获取宽高数组,rgb数组
            for(int i =0;i<images.size();i++){
                BufferedImage temp = (BufferedImage) images.get(i);
                int w = temp.getWidth();
                int h = temp.getHeight();
                width.add(w);
                height.add(h);
                //从图片中读取RGB
                int[] arry = new int[w*h];
                //逐行扫描图像中各个像素的RGB 存到数组中
                arry = temp.getRGB(0, 0, w, h, arry, 0, w);
                rgb.add(arry);
            }
            //获取总高度
            int totalH = 0;
            for(int i =0;i<height.size();i++){
                totalH +=(int)height.get(i);
            }
            //创建内存image对象
            image = new BufferedImage((Integer)width.get(0),totalH, BufferedImage.TYPE_INT_RGB);
            for(int i =rgb.size()-1;i>=0;i--){
                if(i==rgb.size()-1){
                    image.setRGB(0,0,(int)width.get(i),(int)height.get(i),(int[])rgb.get(i),0,(int)width.get(i));
                }else{
                    image.setRGB(0,(int)height.get(i)*(rgb.size()-1-i),(int)width.get(i),(int)height.get(i),(int[])rgb.get(i),0,(int)width.get(i));
                }
            }
        }else{
            //宽度数组
            ArrayList width = new ArrayList();
            //高度数组
            ArrayList height = new ArrayList();
            //RGB二维数组
            ArrayList rgb = new ArrayList();

            //获取宽高数组,rgb数组
            for(int i =0;i<images.size();i++){
                BufferedImage temp = (BufferedImage) images.get(i);
                int w = temp.getWidth();
                int h = temp.getHeight();
                width.add(w);
                height.add(h);
                //从图片中读取RGB
                int[] arry = new int[w*h];
                //逐行扫描图像中各个像素的RGB 存到数组中
                arry = temp.getRGB(0, 0, w, h, arry, 0, w);
                rgb.add(arry);
            }
            //获取总宽度
            int totalW = 0;
            for(int i =0;i<width.size();i++){
                totalW +=(int)width.get(i);
            }
            //创建内存image对象
            image = new BufferedImage(totalW, (Integer) height.get(0), BufferedImage.TYPE_INT_RGB);
            for(int i =0;i<rgb.size();i++){
                if(i==0){
                    image.setRGB(0,0,(int)width.get(i),(int)height.get(i),(int[])rgb.get(i),0,(int)width.get(i));
                }else{
                    image.setRGB((int)width.get(i)*i,0,(int)width.get(i),(int)height.get(i),(int[])rgb.get(i),0,(int)width.get(i));
                }
            }
        }
        return image;
    }
    private void downLoad(){
        //当队列为空,内部会进入空循环等待,避免过高耗费cpu
        while (true) {
            try {
                //取走队列里排在首位的对象,如果队列为空,阻塞线程,进入等待状态,直到队列有新的对象被加入为止
                DownFile downFile = (DownFile) tileQueue.take();
                try {
                    //如果文件不存在 则进行瓦片下载
                    if(!new File(downFile.getFileName()).exists()){
                        //是否剪裁
                        if(this.isCrop){
                            byte[] content = executor.execute(Request.Get(downFile.getUrl()).connectTimeout(5000).socketTimeout(5000)).returnContent().asBytes();
                            //将字节作为输入流
                            ByteArrayInputStream in = new ByteArrayInputStream(content);
                            //字节转带缓冲区的图像
                            BufferedImage image = ImageIO.read(in);
                            //按边界裁剪
                            BufferedImage temp = image.getSubimage(0,0,256,256);
                            ImageIO.write(temp, formatName, new File(downFile.getFileName()));
                        }else{
                            byte[] content = executor.execute(Request.Get(downFile.getUrl()).connectTimeout(5000).socketTimeout(5000)).returnContent().asBytes();
                            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(downFile.getFileName())))) {
                                out.write(content);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                /**
                 * 只对阻塞线程起作用
                 * 当线程阻塞时,调用interrupt中断该线程
                 */
                Thread.currentThread().interrupt();
            }catch (NullPointerException e){
            }
        }
    }
    //重命名文件夹
    private void renameFile(){
        File file = new File(this.filePath);
        //如果文件夹存在
        if(file.exists()){
            File[] files = file.listFiles();
            int index = 0;
            Boolean flag = false;
            for(int i=0;i<files.length;i++){
                //如果是文件夹
                if(files[i].isDirectory()){
                    //判断文件名是否包含 当前设置的任务名称
                    if(files[i].getName().contains(this.oldTaskName)){
                        index++;
                        flag=true;
                    }
                }
            }
            if(flag){
                this.taskName = this.oldTaskName+"("+index+")";
            }else{
                this.taskName = this.oldTaskName;
            }
        }else{
            this.taskName = this.oldTaskName;
        }
    }
    private final ActionListener actionListenerCancel = e -> dispose();
}

