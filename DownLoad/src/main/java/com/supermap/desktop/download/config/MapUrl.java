package com.supermap.desktop.download.config;


import com.supermap.desktop.download.bean.LngLat;

public class MapUrl {
    //系统参数
    public static final Integer downTaskPoolSize = 3;     //下载任务数量
    public static final Integer retryNum = 30;         //同一个瓦片，下载失败后最大重试次数
    public static final Integer tilePoolSize = 5;     //后台下载瓦片线程数量，现在个人电脑配置50基本没压力
    //图商
    public static String mapType = "tianditu";
    public static String mapUrl = MapUrl.TianDiTuStandardUrl;
    public static String mapName = "标准地图";
    public static String TerrainUrl = "http://mt1.google.cn/vt/lyrs=p&gl=cn&x={x}&y={y}&z={z}";
    public static String VectorUrl = "https://www.openstreetmap.org/api/0.6/map?bbox={bounds}";

    //坐标
    public static LngLat northwest = new LngLat(109.654936, 25.516771);
    public static LngLat southeast = new LngLat(117.192939, 20.211718);
    //google坐标
    public static LngLat northwestGoogle = new LngLat(116.302299499512, 39.9171095767978);
    public static LngLat southeastGoogle = new LngLat(116.35139465332, 39.9352762105026);
    //天地图坐标
//    public static LngLat northwestTianDiTu = new LngLat(116.293325053, 39.680040692);
//    public static LngLat southeastTianDiTu = new LngLat(116.336755382, 39.696520184);
    public static LngLat northwestTianDiTu = new LngLat(106.23779296875, 35.244140625);
    public static LngLat southeastTianDiTu = new LngLat(107.3583984375, 36.2548828125);
    //osm坐标
    public static LngLat northwestOSM = new LngLat(106.473, 35.07);
    public static LngLat southeastOSM = new LngLat(107.039, 35.42);
    //天地图比例尺
    public static final String[] TianDiTuScale={
        "1:295829355",
        "1:147914678",
        "1:73957339",
        "1:36978669",
        "1:18489335",
        "1:9244667",
        "1:4622334",
        "1:2311167",
        "1:1155583",
        "1:577792",
        "1:288896",
        "1:144448",
        "1:72224",
        "1:36112",
        "1:18056",
        "1:9028",
        "1:4514",
        "1:2257"
    };
    //谷歌比例尺
    public static final String[] GoogleScale={
        "1:591658711",
        "1:295829355",
        "1:147914678",
        "1:73957339",
        "1:36978669",
        "1:18489335",
        "1:9244667",
        "1:4622334",
        "1:2311167",
        "1:1155583",
        "1:577792",
        "1:288896",
        "1:144448",
        "1:72224",
        "1:36112",
        "1:18056",
        "1:9028",
        "1:4514",
        "1:2257",
        "1:1129",
        "1:564",
        "1:282"
    };
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
    public static final String BaiduStandardUrl = "http://maponline3.bdimg.com/tile/?qt=vtile&x={x}&y={y}&z={z}&styles=pl&scaler=1&udt=20181205&from=jsapi2_0";
    //百度影像地图
    public static final String BaiduImageUrl = "http://shangetu3.map.bdimg.com/it/u=x={x};y={y};z={z};v=009;type=sate&fm=46&udt=20181205&scale=1";
    //百度路网地图
    //public static final String BaiduRoadUrl="http://maponline1.bdimg.com/tile/?qt=vtile&x={x}&y={y}&z={z}&styles=sl&udt=20181205&scale=1";
    //百度个性地图 黑夜
    public static final String BaiduBlackUrl = "http://api0.map.bdimg.com/customimage/tile?&x={x}&y={y}&z={z}&udt=20181205&scale=1&ak=E4805d16520de693a3fe707cdc962045&customid=dark";
    //百度个性地图 清新蓝
    public static final String BaiduBlueUrl = "http://api0.map.bdimg.com/customimage/tile?&x={x}&y={y}&z={z}&udt=20181205&scale=1&ak=E4805d16520de693a3fe707cdc962045&customid=light";
    //百度个性地图 红色警戒
    public static final String BaiduRedUrl = "http://api0.map.bdimg.com/customimage/tile?&x={x}&y={y}&z={z}&udt=20181205&scale=1&ak=E4805d16520de693a3fe707cdc962045&customid=redalert";


    //谷歌标准地图
    public static final String GoogleStandardUrl= "http://mt1.google.cn/vt/lyrs=m&gl=cn&x={x}&y={y}&z={z}";
    //谷歌影像地图
    public static final String GoogleImageUrl= "http://mt3.google.cn/vt/lyrs=y&gl=cn&x={x}&y={y}&z={z}";
    //谷歌地形图
    public static final String GoogleTerrainUrl= "http://mt0.google.cn/vt/lyrs=p&gl=cn&x={x}&y={y}&z={z}";

    //天地图标准地图
    private static final String TianDiTuStandardUrl = "http://t0.tianditu.com/DataServer?T=vec_w&x={x}&y={y}&l={z}&tk=7d3fe81b82dfa70689fc354b52e622aa";
    //天地图影像地图
    private static final String TianDiTuImageUrl = "http://t0.tianditu.com/DataServer?T=img_w&x={x}&y={y}&l={z}&tk=7d3fe81b82dfa70689fc354b52e622aa";
    //天地图地形
    private static final String TianDiTuTerrainUrl = "http://t0.tianditu.com/DataServer?T=ter_w&x={x}&y={y}&l={z}&tk=7d3fe81b82dfa70689fc354b52e622aa";
}
