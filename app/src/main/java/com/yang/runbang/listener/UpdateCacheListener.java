package com.yang.runbang.listener;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.BmobListener;

/**
 * Created by 洋 on 2016/5/21.
 */
public abstract class UpdateCacheListener extends BmobListener {
    public abstract void done(BmobException e);
    @Override
    protected void postDone(Object o, BmobException e) {
        done(e);
    }
}
