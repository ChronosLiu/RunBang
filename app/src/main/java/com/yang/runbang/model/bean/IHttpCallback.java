package com.yang.runbang.model.bean;

/**
 *
 * 网络请求返回接口
 *
 * Created by 洋 on 2016/5/4.
 */
public interface IHttpCallback {

    /**
     * 成功
     * @param response
     */
    void onSuccess(String response);

    /**
     * 失败
     */
    void onFailure(Exception e);
}
