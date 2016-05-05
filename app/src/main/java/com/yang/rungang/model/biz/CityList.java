package com.yang.rungang.model.biz;

import com.google.gson.annotations.SerializedName;
import com.yang.rungang.model.bean.City;

import java.util.List;

/**
 *
 * 城市列表类
 *
 * Created by 洋 on 2016/5/4.
 */
public class CityList {

    @SerializedName("city_info")
    public  List<City> cities;

    private String status;

    public List<City> getCities() {
        return cities;
    }

    public void setCities(List<City> cities) {
        this.cities = cities;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {

        return status;
    }
}
