package com.supermap.desktop.download.bean;

import java.io.Serializable;

public class Task implements Serializable {

    private String taskName; // 任务名称
    private String mapType;//地图类型
    private String mapName;// 地图名称
    private String createTime;// 创建时间
    private String filePath;// 文件所在目录
    private int tileNum;//瓦片总数
    private int tileCurrentNum;//当前下载瓦片数量

    public int getTileNum() {
        return tileNum;
    }

    public int getTileCurrentNum() {
        return tileCurrentNum;
    }

    public void setTileNum(int tileNum) {
        this.tileNum = tileNum;
    }

    public void setTileCurrentNum(int tileCurrentNum) {
        this.tileCurrentNum = tileCurrentNum;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setMapType(String mapType) {
        this.mapType = mapType;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }


    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getMapType() {
        return mapType;
    }

    public String getMapName() {
        return mapName;
    }


    public String getCreateTime() {
        return createTime;
    }

    public String getFilePath() {
        return filePath;
    }
}
