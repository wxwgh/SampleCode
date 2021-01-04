package com.supermap.desktop.download.util;

import com.supermap.data.PrjCoordSys;

public class WebMapInfo {
    private String baseDatasourceAlias = "";
    private String baseDatasetName = "";
    private String baseMapUrl = "";
    private String markDatasourceAlias = "";
    private String markDatasetName = "";
    private String markMapUrl = "";
    private String mapKey = "";
    private PrjCoordSys mapPrj = null;

    public String getBaseLayerName(){
        return getBaseDatasetName() + "@" + getBaseDatasourceAlias();
    }

    public String getMarkLayerName(){
        return getMarkDatasetName() + "@" + getMarkDatasourceAlias();
    }

    public String getBaseDatasourceAlias() {
        return baseDatasourceAlias;
    }

    public void setBaseDatasourceAlias(String baseDatasourceAlias) {
        this.baseDatasourceAlias = baseDatasourceAlias;
    }

    public String getBaseDatasetName() {
        return baseDatasetName;
    }

    public void setBaseDatasetName(String baseDatasetName) {
        this.baseDatasetName = baseDatasetName;
    }

    public String getBaseMapUrl() {
        return baseMapUrl;
    }

    public void setBaseMapUrl(String baseMapUrl) {
        this.baseMapUrl = baseMapUrl;
    }

    public String getMarkDatasourceAlias() {
        return markDatasourceAlias;
    }

    public void setMarkDatasourceAlias(String markDatasourceAlias) {
        this.markDatasourceAlias = markDatasourceAlias;
    }

    public String getMarkDatasetName() {
        return markDatasetName;
    }

    public void setMarkDatasetName(String markDatasetName) {
        this.markDatasetName = markDatasetName;
    }

    public String getMarkMapUrl() {
        return markMapUrl;
    }

    public void setMarkMapUrl(String markMapUrl) {
        this.markMapUrl = markMapUrl;
    }

    public String getMapKey() {
        return mapKey;
    }

    public void setMapKey(String mapKey) {
        this.mapKey = mapKey;
    }

    public PrjCoordSys getMapPrj() {
        return mapPrj;
    }

    public void setMapPrj(PrjCoordSys mapPrj) {
        this.mapPrj = mapPrj;
    }
}
