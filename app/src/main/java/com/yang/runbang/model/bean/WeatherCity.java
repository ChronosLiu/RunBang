package com.yang.runbang.model.bean;

import cn.bmob.v3.BmobObject;

/**
 *
 * 城市信息
 *
 * Created by 洋 on 2016/5/4.
 */
public class WeatherCity extends BmobObject {

    private String id; //城市id
    private String city; //城市名称
    private String prov; //省份
    private String cnty; //国家
    private String lat; //纬度
    private String lon; //经度

    public String getId() {
        return id;
    }

    public String getCity() {
        return city;
    }

    public String getProv() {
        return prov;
    }

    public String getCnty() {
        return cnty;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
