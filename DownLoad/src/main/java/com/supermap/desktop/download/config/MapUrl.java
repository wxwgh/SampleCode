package com.supermap.desktop.download.config;


import com.supermap.desktop.download.bean.LngLat;

public class MapUrl {
    //系统参数
    public static final Integer downTaskPoolSize=3;     //下载任务数量
    public static final Integer retryNum=3;         //同一个瓦片，下载失败后最大重试次数
    public static final Integer tilePoolSize=5;     //后台下载瓦片线程数量，现在个人电脑配置50基本没压力
    //图商
    public static String mapType = "baidu";
    public static String mapUrl = MapUrl.BaiduRedUrl;

    //西北角坐标
    public static LngLat northwest = new LngLat(109.654936, 25.516771);
    public static LngLat southeast = new LngLat(117.192939, 20.211718);
    //百度比例尺
    public static final String[] BaiduScale = {
        "1:495390236",
        "1:247695118",
        "1:123847559",
        "1:61923779",
        "1:30961889",
        "1:15480944",
        "1:7740472",
        "1:3870236",
        "1:1935118",
        "1:967559",
        "1:483779",
        "1:241889",
        "1:120944",
        "1:60472",
        "1:30236",
        "1:15118",
        "1:7559",
        "1:3779",
        "1:1889"
    };
    //百度标准地图 即普通街道
    public static final String BaiduStandardUrl ="http://maponline3.bdimg.com/tile/?qt=vtile&x={x}&y={y}&z={z}&styles=pl&scaler=1&udt=20181205&from=jsapi2_0";
    //百度影像地图
    public static final String BaiduImageUrl="http://shangetu3.map.bdimg.com/it/u=x={x};y={y};z={z};v=009;type=sate&fm=46&udt=20181205&scale=1";
    //百度路网地图
    //public static final String BaiduRoadUrl="http://maponline1.bdimg.com/tile/?qt=vtile&x={x}&y={y}&z={z}&styles=sl&udt=20181205&scale=1";
    //百度个性地图 黑夜
    public static final String BaiduBlackUrl="http://api0.map.bdimg.com/customimage/tile?&x={x}&y={y}&z={z}&udt=20181205&scale=1&ak=E4805d16520de693a3fe707cdc962045&customid=dark";
    //百度个性地图 清新蓝
    public static final String BaiduBlueUrl="http://api0.map.bdimg.com/customimage/tile?&x={x}&y={y}&z={z}&udt=20181205&scale=1&ak=E4805d16520de693a3fe707cdc962045&customid=light";
    //百度个性地图 红色警戒
    public static final String BaiduRedUrl="http://api0.map.bdimg.com/customimage/tile?&x={x}&y={y}&z={z}&udt=20181205&scale=1&ak=E4805d16520de693a3fe707cdc962045&customid=redalert";


}
