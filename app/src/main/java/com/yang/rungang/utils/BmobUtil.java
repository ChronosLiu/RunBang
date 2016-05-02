package com.yang.rungang.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.yang.rungang.model.bean.IBmobCallback;
import com.yang.rungang.model.bean.News;

import java.io.File;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.GetListener;

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
        String savePath=Environment.getExternalStorageDirectory()+File.separator+bmobFile.getFilename();
        File saveFile = new File(savePath);
        if(!saveFile.exists()) saveFile.mkdir();

        bmobFile.download(context, saveFile, new DownloadFileListener() {
            @Override
            public void onSuccess(String s) {

                if(callback!=null){
                    callback.onFinish(IdentiferUtil.DOWN_FILE_SUCCESS,s);
                }
            }
            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG",s+i);
                if(callback!=null){
                    callback.onFailure(IdentiferUtil.DOWN_FILE_FAIL);
                }
            }
        });
    }

    /**
     *
     * @param context
     * @param file
     */
    public static void downloadFile(Context context,BmobFile file){

        File saveFile = new File(Environment.getExternalStorageDirectory(),file.getFilename());
        file.download(context, new DownloadFileListener() {
            @Override
            public void onSuccess(String s) {


            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 查询单个数据
     * @param context
     * @param objectId
     * @param callback
     */
    public static void querySingleData(Context context,String objectId,final IBmobCallback callback){
        BmobQuery<News> query = new BmobQuery<>();
        query.getObject(context, objectId, new GetListener<News>() {
            @Override
            public void onSuccess(News news) {
                if(callback!=null){
                    callback.onFinish(IdentiferUtil.QUERY_SINGLE_DATA_SUCCESS,news);
                }
            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG",s+i);
            }
        });
    }

}
