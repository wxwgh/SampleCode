package com.supermap.desktop.download.bean;


import com.supermap.desktop.download.consts.TaskStateEnum;

import java.io.Serializable;
import java.util.concurrent.Semaphore;

/**
 * 下载任务
 * 
 * @author wxw
 */
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;// 当前任务ID
    private long count;// 任务瓦片总数
    private long current;// 当前已经下载的瓦片数
    private TaskStateEnum state;// 当前任务状态
    /**
     * Semaphore(信号量)：是一种计数器，用来保护一个或者多个共享资源的访问。
     * 如果线程要访问一个资源就必须先获得信号量。
     * 如果信号量内部计数器大于0，信号量减1，然后允许共享这个资源
     * 否则，如果信号量的计数器等于0，信号量将会把线程置入休眠直至计数器大于0
     * 当信号量使用完时，必须释放
     */
    private Semaphore semaphore;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public TaskStateEnum getState() {
        return state;
    }

    public void setState(TaskStateEnum state) {
        this.state = state;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    public void setSemaphore(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

}
