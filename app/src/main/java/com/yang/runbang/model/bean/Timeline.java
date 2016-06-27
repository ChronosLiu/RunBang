package com.yang.runbang.model.bean;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 洋 on 2016/5/13.
 */
public class Timeline extends BmobObject{

    private User fromUser; // 用户

    private BmobRelation allDynamic; //所有动态

    public User getFromUser() {
        return fromUser;
    }

    public BmobRelation getAllDynamic() {
        return allDynamic;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public void setAllDynamic(BmobRelation allDynamic) {
        this.allDynamic = allDynamic;
    }
}
