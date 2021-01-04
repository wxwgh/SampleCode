package com.supermap.desktop.download.bean;

import java.io.Serializable;

/**
 * 下载的瓦片文件
 *
 * @author wxw
 */
public class DownFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String url; // 瓦片下载地址
    private String fileName;// 瓦片保存路径全程
    private String id;

    public DownFile(String url, String fileName, String id) {
        super();
        this.url = url;
        this.fileName = fileName;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
