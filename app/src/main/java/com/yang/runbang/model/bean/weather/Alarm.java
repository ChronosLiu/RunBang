package com.yang.runbang.model.bean.weather;

/**
 * 预警信息
 *
 * Created by 洋 on 2016/5/5.
 */
public class Alarm {

    private String level; //预警等级
    private String stat; // 预警状态
    private String title; //预警信息标题
    private String txt; // 预警信息详情
    private String type; // 预警天气类型

    public String getLevel() {
        return level;
    }

    public String getStat() {
        return stat;
    }

    public String getTitle() {
        return title;
    }

    public String getTxt() {
        return txt;
    }

    public String getType() {
        return type;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }
}
