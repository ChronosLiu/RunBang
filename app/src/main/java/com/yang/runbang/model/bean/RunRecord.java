package com.yang.runbang.model.bean;

import com.baidu.mapapi.model.LatLng;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * 跑步记录
 *
 * Created by 洋 on 2016/5/2.
 */
public class RunRecord extends BmobObject {


    private String recordid;    //记录id
    private String userId;      // 用户id
    private User fromUser; //用户

    private int time;          // 用时

    private double distance;      // 距离

    private String mapShotPath;   // 地图截屏路径

    private List<LatLng> points;  //坐标点的集合

    private List<Float> speeds;   //速度集合

    private String createTime;    //创建时间

    private boolean isSync; //同步标示，true 已同步，false 未同步

    public String getUserId() {
        return userId;
    }

    public int getTime() {
        return time;
    }

    public double getDistance() {
        return distance;
    }

    public String getMapShotPath() {
        return mapShotPath;
    }

    public String getCreateTime() {
        return createTime;
    }

    public boolean isSync() {
        return isSync;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public void setTime(int time) {
        this.time = time;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setMapShotPath(String mapShotPath) {
        this.mapShotPath = mapShotPath;
    }

    public void setCreateTime(String creatTime) {
        this.createTime = creatTime;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public List<Float> getSpeeds() {
        return speeds;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }

    public void setSpeeds(List<Float> speeds) {
        this.speeds = speeds;
    }

    public void setIsSync(boolean isSync) {
        this.isSync = isSync;
    }

    public void setRecordid(String recordid) {
        this.recordid = recordid;
    }

    public String getRecordid() {

        return recordid;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RunRecord) {
            RunRecord record = (RunRecord)o;

            return this.recordid.equals(record.getRecordid());

        }
        return super.equals(o);
    }
}
