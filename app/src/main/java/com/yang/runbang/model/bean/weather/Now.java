package com.yang.runbang.model.bean.weather;

/**
 * 实况天气
 *
 * Created by 洋 on 2016/5/5.
 */
public class Now {

    private NowCond cond;
    private String fl;
    private String hum;
    private String pcpn;
    private String pres;
    private String tmp;
    private String vis;
    private Wind wind;


    public NowCond getCond() {
        return cond;
    }

    public String getFl() {
        return fl;
    }

    public String getHum() {
        return hum;
    }

    public String getPcpn() {
        return pcpn;
    }

    public String getPres() {
        return pres;
    }

    public String getTmp() {
        return tmp;
    }

    public Wind getWind() {
        return wind;
    }

    public String getVis() {
        return vis;
    }

    public void setCond(NowCond cond) {
        this.cond = cond;
    }

    public void setFl(String fl) {
        this.fl = fl;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public void setPcpn(String pcpn) {
        this.pcpn = pcpn;
    }

    public void setPres(String pres) {
        this.pres = pres;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public void setVis(String vis) {
        this.vis = vis;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public  class NowCond {
        private String code;
        private String txt;

        public String getCode() {
            return code;
        }

        public String getTxt() {
            return txt;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setTxt(String txt) {
            this.txt = txt;
        }
    }


}
