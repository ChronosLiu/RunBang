package com.yang.runbang.model.bean;

import java.util.List;

/**
 * 离线地图城市
 * Created by 洋 on 2016/5/6.
 */
public class OLCity {

    private int cityID;  //城市ID
    private String cityName; //城市名称
    private int cityType; //城市类型  0：全国 1：省份 2：城市
    private int size; //数据包大小
    private int status; //下载状态
    private int ratio; //下载比率
    private boolean update; //是否为更新
    private List<OLCity> childCities; //子城市列表

    public int getCityID() {
        return cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public int getCityType() {
        return cityType;
    }

    public List<OLCity> getChildCities() {
        return childCities;
    }

    public int getSize() {
        return size;
    }

    public void setChildCities(List<OLCity> childCities) {
        this.childCities = childCities;
    }

    public void setCityType(int cityType) {
        this.cityType = cityType;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityID(int cityID) {
        this.cityID = cityID;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public int getStatus() {

        return status;
    }

    public int getRatio() {
        return ratio;
    }

    public boolean isUpdate() {
        return update;
    }
}
