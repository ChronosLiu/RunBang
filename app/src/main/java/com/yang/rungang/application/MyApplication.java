package com.yang.rungang.application;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 *
 * 我的Application
 *
 * Created by 洋 on 2016/5/4.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //创建默认imageloader的默认参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }
}
