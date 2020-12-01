package com.supermap.desktop.download.bean;

import java.io.Serializable;

/**
 * 经纬度
 * 
 * @author wxw
 *
 */
public class LngLat implements Serializable {
    private static final long serialVersionUID = 1L;

    private double lng;
	private double lat;

	public LngLat() {
		super();
	}

	public LngLat(double lng, double lat) {
		super();
		this.lng = lng;
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}
}
