package com.yang.runbang.model.bean;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;

/**
 * 用户类
 * Created by 洋 on 2016/4/22.
 */
public class User extends BmobUser {

    private String nickName;//昵称

    private Boolean sex; // 性别

    private Integer age; // 年龄

    private BmobDate birthday; // 生日

    private String signature;// 个性签名

    private String headImgPath; //头像本地文件路径

    private String headImgUrl; //头像网络Url


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

    public String getHeadImgUrl() {
        return headImgUrl;
    }

    public void setHeadImgUrl(String headImgUrl) {
        this.headImgUrl = headImgUrl;
    }

    public String getHeadImgPath() {
        return headImgPath;
    }

    public void setHeadImgPath(String headImgPath) {
        this.headImgPath = headImgPath;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @Override
    public boolean equals(Object o) {

         if (o instanceof User) {
             User u = (User)o;

             return this.getObjectId().equals(u.getObjectId());
         }
        return super.equals(o);
    }
}
