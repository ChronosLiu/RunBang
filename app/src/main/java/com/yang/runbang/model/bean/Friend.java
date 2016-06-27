package com.yang.runbang.model.bean;

import cn.bmob.v3.BmobObject;

/**
 *
 * 用户关系
 *
 * Created by 洋 on 2016/5/11.
 */
public class Friend extends BmobObject {

    private User fromUser;

    private User toUser;

    public User getToUser() {
        return toUser;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
}
