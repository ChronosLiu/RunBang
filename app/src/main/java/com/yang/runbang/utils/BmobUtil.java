package com.yang.runbang.utils;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.yang.runbang.model.bean.IBmobCallback;
import com.yang.runbang.model.bean.News;
import com.yang.runbang.model.bean.RunRecord;

import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.GetServerTimeListener;
import cn.bmob.v3.listener.UploadBatchListener;

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
     * 批量上传文件
     * @param context
     * @param pathList
     * @param callback
     */
    public static void uploadFileBatch(Context context ,List<String> pathList,final IBmobCallback callback){

        final  String[] filePaths = new String[pathList.size()];

        for (int i =0;i<pathList.size();i++) {
            filePaths[i] = pathList.get(i);
        }

        BmobFile.uploadBatch(context, filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {

                if (list1.size() == filePaths.length) { //全部上传完成

                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {

            }

            @Override
            public void onError(int i, String s) {

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
                if (callback != null) {
                    callback.onFinish(IdentiferUtil.QUERY_SINGLE_DATA_SUCCESS, news);
                }
            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG", s + i);
            }
        });
    }

    /**
     * 从bmob服务器删除一条runrecord
     * @param context
     * @param objectId
     * @param callback
     */
    public static void deleteDataFromRunRecord(Context context,String objectId,final IBmobCallback callback) {
        RunRecord runRecord = new RunRecord();
        runRecord.setObjectId(objectId);
        runRecord.delete(context, new DeleteListener() {
            @Override
            public void onSuccess() {

                if (callback != null) {
                    callback.onFinish(IdentiferUtil.DELETE_RUN_RECORD_SUCCESS, null);
                }
            }

            @Override
            public void onFailure(int i, String s) {

                if (callback != null) {
                    callback.onFinish(IdentiferUtil.DELETE_RUN_RECORD_FAILURE, null);
                }
            }
        });

    }


    private void getServiceTime(Context context,final IBmobCallback callback) {
        Bmob.getServerTime(context, new GetServerTimeListener() {
            @Override
            public void onSuccess(long l) {

            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

}
