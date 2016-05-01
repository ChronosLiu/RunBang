package com.yang.rungang.utils;

import android.content.Context;
import android.os.Environment;

import com.yang.rungang.model.bean.IBmobCallback;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 * bmob工具类
 * Created by 洋 on 2016/4/25.
 */
public class BmobUtil {

    /**
     * 从bmob下载文件（头像）
     * @param context
     * @param url 文件url
     * @param callback 返回处理接口
     */
    public static void downHeadImg(Context context,String url,final IBmobCallback callback){
        BmobFile bmobFile=new BmobFile("headimg.png","",url);
        File saveFile = new File(Environment.getExternalStorageDirectory(),bmobFile.getFilename());
        bmobFile.download(context, saveFile, new DownloadFileListener() {
            @Override
            public void onSuccess(String s) {

                if(callback!=null){
                    callback.onFinish(IdentiferUtil.DOWN_FILE_SUCCESS,s);
                }
            }
            @Override
            public void onFailure(int i, String s) {

                if(callback!=null){
                    callback.onFailure(IdentiferUtil.DOWN_FILE_FAIL);
                }
            }
        });
    }

}
