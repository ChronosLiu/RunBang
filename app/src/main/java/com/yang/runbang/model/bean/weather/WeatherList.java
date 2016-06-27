package com.yang.runbang.model.bean.weather;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 洋 on 2016/5/5.
 */
public class WeatherList {

    @SerializedName("HeWeather data service 3.0")
    private List<WeatherData> weatherDatas;

    public List<WeatherData> getWeatherDatas() {
        return weatherDatas;
    }

    public void setWeatherDatas(List<WeatherData> weatherDatas) {
        this.weatherDatas = weatherDatas;
    }
}
