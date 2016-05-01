package com.yang.rungang.model.bean;

/**
 *
 * bmob返回结果处理接口
 * Created by 洋 on 2016/5/1.
 */
public interface IBmobCallback {

    /**
     * 成功
     * @param identifier 标识
     * @param str
     */
    void onFinish(int identifier,String str);

    /**
     * 失败
     * @param identifier
     */
    void onFailure(int identifier);
}
