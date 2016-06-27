package com.yang.runbang.listener;

import com.yang.runbang.model.bean.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobListener;

/**
 * Created by 洋 on 2016/5/21.
 */
public abstract class QueryUserListener extends BmobListener<User> {

    public abstract void done(User s, BmobException e);
    @Override
    protected void postDone(User user, BmobException e) {
        done(user, e);
    }
}
