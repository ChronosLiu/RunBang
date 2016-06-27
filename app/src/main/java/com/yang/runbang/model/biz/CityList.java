package com.yang.runbang.model.biz;

import com.google.gson.annotations.SerializedName;
import com.yang.runbang.model.bean.WeatherCity;

import java.util.List;

/**
 *
 * 城市列表类
 *
 * Created by 洋 on 2016/5/4.
 */
public class CityList {

    @SerializedName("city_info")
    public  List<WeatherCity> cities;

    private String status;

    public List<WeatherCity> getCities() {
        return cities;
    }

    public void setCities(List<WeatherCity> cities) {
        this.cities = cities;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {

        return status;
    }
}
