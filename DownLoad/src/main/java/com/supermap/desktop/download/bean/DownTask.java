package com.supermap.desktop.download.bean;

import java.io.Serializable;

public class DownTask implements Serializable {

    private String id;//任务id 唯一标识
    private Task task;//任务详细信息

    public void setId(String id) {
        this.id = id;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public String getId() {
        return id;
    }

    public Task getTask() {
        return task;
    }
}
