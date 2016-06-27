package com.yang.runbang.https;

import com.yang.runbang.model.bean.IHttpCallback;
import com.yang.runbang.utils.ConfigUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

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

    /**
     * 发送Get请求
     * @param url
     * @param callback
     */

    public static void sendGetRequest(final String url,final IHttpCallback callback) {

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
