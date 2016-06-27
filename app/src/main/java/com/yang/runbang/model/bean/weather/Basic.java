package com.yang.runbang.model.bean.weather;

/**
 * 基础信息
 *
 * Created by 洋 on 2016/5/5.
 */
public class Basic {

    public String city;
    public String cnty;
    public String id;
    public String lat;
    public String lon;
    public Update update;

    public String getCity() {
        return city;
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

    public Update getUpdate() {
        return update;
    }

    public String getId() {
        return id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCnty(String cnty) {
        this.cnty = cnty;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }

    private class Update {
        public String loc;
        public String utc;

        public String getLoc() {
            return loc;
        }

        public String getUtc() {
            return utc;
        }

        public void setLoc(String loc) {
            this.loc = loc;
        }

        public void setUtc(String utc) {
            this.utc = utc;
        }
    }
}
