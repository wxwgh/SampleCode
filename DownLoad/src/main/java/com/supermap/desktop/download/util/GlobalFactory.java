package com.supermap.desktop.download.util;

import com.supermap.data.PrjCoordSys;
import com.supermap.data.PrjCoordSysType;
import com.supermap.desktop.core.implement.SmRibbonComboBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GlobalFactory {
    private static class FactoryHolder{
        private static GlobalFactory instance = new GlobalFactory();
    }

    private Map<String, PrjCoordSys> webMapPrj = new HashMap<>();
    private List<String> cityList = new ArrayList<>();
    private List<String> countyList = new ArrayList<>();
    private String selectProvince = "";

    private SmRibbonComboBox provinceComboBox;
    private SmRibbonComboBox cityComboBox;
    private SmRibbonComboBox CountyComboBox;


    private GlobalFactory() {
        webMapPrj.put("GoogleMapNot",new PrjCoordSys(PrjCoordSysType.PCS_WGS_1984_WEB_MERCATOR));
        webMapPrj.put("GoogleMapExist",new PrjCoordSys(PrjCoordSysType.PCS_WGS_1984_WEB_MERCATOR));
        webMapPrj.put("BaiduMaps",new PrjCoordSys(PrjCoordSysType.PCS_WGS_1984_WEB_MERCATOR));
        webMapPrj.put("OpenStreetMaps",new PrjCoordSys(PrjCoordSysType.PCS_WGS_1984_WEB_MERCATOR));
        webMapPrj.put("TdtWgsMaps",new PrjCoordSys(PrjCoordSysType.PCS_EARTH_LONGITUDE_LATITUDE));
        webMapPrj.put("TdtWebMaps",new PrjCoordSys(PrjCoordSysType.PCS_WGS_1984_WEB_MERCATOR));
    }

    public static GlobalFactory getInstance(){
        return FactoryHolder.instance;
    }

    private WebMapInfo currentShowWebMap;

    public WebMapInfo getCurrentShowWebMap() {
        return currentShowWebMap;
    }

    public void setCurrentShowWebMap(WebMapInfo currentShowWebMap) {
        this.currentShowWebMap = currentShowWebMap;
    }

    public PrjCoordSys getWebMapPrj(String webMapName){
        return webMapPrj.get(webMapName);
    }

    public List<String> getCityList() {
        return cityList;
    }

    public void setCityList(String name) {
        this.cityList.add(name);
    }

    public List<String> getCountyList() {
        return countyList;
    }

    public void setCountyList(List<String> countyList) {
        this.countyList = countyList;
    }

    public String getSelectProvince() {
        return selectProvince;
    }

    public void setSelectProvince(String selectProvince) {
        this.selectProvince = selectProvince;
    }

    public SmRibbonComboBox getProvinceComboBox() {
        return provinceComboBox;
    }

    public void setProvinceComboBox(SmRibbonComboBox provinceComboBox) {
        this.provinceComboBox = provinceComboBox;
    }

    public SmRibbonComboBox getCityComboBox() {
        return cityComboBox;
    }

    public void setCityComboBox(SmRibbonComboBox cityComboBox) {
        this.cityComboBox = cityComboBox;
    }

    public SmRibbonComboBox getCountyComboBox() {
        return CountyComboBox;
    }

    public void setCountyComboBox(SmRibbonComboBox countyComboBox) {
        CountyComboBox = countyComboBox;
    }
}
