package com.yang.rungang.model.bean;

import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 用户类
 * Created by 洋 on 2016/4/22.
 */
public class User extends BmobUser {

    private String nickName;//昵称

    private Boolean sex; // 性别

    private Integer age; // 年龄

    private BmobDate birthday; // 生日

    private BmobFile headImg; // 头像

    public String getNickName() {
        return nickName;
    }

    public Boolean getSex() {
        return sex;
    }

    public Integer getAge() {
        return age;
    }

    public BmobDate getBirthday() {
        return birthday;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setBirthday(BmobDate birthday) {
        this.birthday = birthday;
    }

    public void setHeadImg(BmobFile headImg) {
        this.headImg = headImg;
    }

    public BmobFile getHeadImg() {

        return headImg;
    }
}
