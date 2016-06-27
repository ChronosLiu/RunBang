package com.yang.runbang.model.bean.weather;

import java.util.List;

/**
 * 天气数据
 *
 * Created by 洋 on 2016/5/5.
 */
public class WeatherData {

    private String status;

    private Aqi aqi;

    private Basic basic;

    private List<Alarm> alarms;

    private Now now;

    public String getStatus() {
        return status;
    }

    public Aqi getAqi() {
        return aqi;
    }

    public Basic getBasic() {
        return basic;
    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public Now getNow() {
        return now;
    }

    public void setAqi(Aqi aqi) {
        this.aqi = aqi;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    public void setNow(Now now) {
        this.now = now;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
