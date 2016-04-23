package com.yang.rungang.utils;

/**
 * 基本配置
 * Created by 洋 on 2016/4/22.
 */
public class ConfigUtil {

    /**
     * Application ID ，初始化用到密钥
     */
    public static final String BMOB_APP_ID = "99ab5f136b4e6339a7c11a3a65b2248f";

    /**
     * REST API Key , REST API请求中HTTP头部信息必须附带密钥之一
     */
    public static final String BMOB_API_KEY = "862d75221026f70715041c9faa9b41e5";

    /**
     * Secret key ，是SDK安全密钥，不可泄漏，在云端逻辑测试云端代码时需要用到
     */
    public static final String BMOB_SECRET_KEY = "6d66e769c5716345";

    /**
     * Master Key , 超级权限Key。应用开发或调试的时候可以使用该密钥进行各种权限的操作，此密钥不可泄漏
     */
    public static final String BMOB_MASTER_KEY = "f42e76d52e9b8962ed6f9e0ccfb47e94";

}
