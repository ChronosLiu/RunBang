package com.yang.rungang.https;

import android.app.DownloadManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.yang.rungang.model.bean.City;
import com.yang.rungang.model.bean.IHttpCallback;
import com.yang.rungang.utils.ConfigUtil;
import com.yang.rungang.utils.GeneralUtil;
import com.yang.rungang.utils.IdentiferUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 网络请求工具类
 *
 * Created by 洋 on 2016/5/4.
 */
public class HttpsUtil {


    /**
     * 通过网络获取天气 Get请求
     */
    public static void  httpGetRequest(final String url, final IHttpCallback callback){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL httpUrl= new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setRequestProperty("apikey", ConfigUtil.APISTORE_API_KEY);
                    connection.connect();
                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    reader.close();
                    in.close();
                    connection.disconnect();
                    if(callback!=null){
                        callback.onSuccess(response.toString());
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }).start();
    }

    /**
     *获取城市列表
     */
    public static void getCityList(final String url,final IHttpCallback callback){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL httpUrl= new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) httpUrl.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.connect();
                    InputStream in=connection.getInputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    StringBuilder response=new StringBuilder();
                    String line;
                    while((line=reader.readLine())!=null){
                        response.append(line);
                    }
                    reader.close();
                    in.close();
                    connection.disconnect();
                    if(callback!=null){
                        callback.onSuccess(response.toString());
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }
            }
        }).start();
    }

}
