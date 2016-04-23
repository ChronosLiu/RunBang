package com.yang.rungang.model.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 用户类
 * Created by 洋 on 2016/4/22.
 */
public class User extends BmobUser {

    private Boolean sex; // 性别

    private Integer age; // 年龄

    private BmobFile headImg; // 头像

    public Boolean getSex() {
        return sex;
    }

    public Integer getAge() {
        return age;
    }

    public BmobFile getHeadImg() {
        return headImg;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setHeadImg(BmobFile headImg) {
        this.headImg = headImg;
    }
}
