package com.yang.rungang.model.bean;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 跑步记录
 *
 * Created by 洋 on 2016/5/2.
 */
public class RunRecord extends BmobObject {

    private String userId;        // 用户id

    private double time;          // 用时

    private double distance;      // 距离

    private List<LatLng> points;  //坐标点的集合

    private List<Float> speeds; //速度集合

    public String getUserId() {
        return userId;
    }

    public double getTime() {
        return time;
    }

    public double getDistance() {
        return distance;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public List<Float> getSpeeds() {
        return speeds;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setSpeeds(List<Float> speeds) {
        this.speeds = speeds;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
