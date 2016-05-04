package com.yang.rungang.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * 通用工具类
 *
 * Created by 洋 on 2016/4/24.
 */
public class GeneralUtil {


    /**
     * 检验是否为手机号码
     * @param number
     * @return
     */
    public static boolean isMobileNumber(String number){

        String regex="^((13[0-9]|(15[^4,\\D])|(17[0,6,7])|(18[0-9]))\\d{8}$)";

        Pattern pattern=Pattern.compile(regex);

        Matcher matcher=pattern.matcher(number);

        return matcher.matches();

    }

    /**
     *
     * 是否为Email地址
     * @param email
     * @return
     */
    public static boolean isEmail(String email){

        String regex="^([a-zA-Z0-9_-])+@(([a-zA-Z0-9_-]+)[.])+[a-z]{2,4}$";
        Pattern pattern=Pattern.compile(regex);

        Matcher matcher=pattern.matcher(email);

        return matcher.matches();

    }

    /**
     * 是否为纯数字
     * @param number
     * @return
     */
    public static boolean isNumber(String number){
        String regex="^[0-9]*$";

        Pattern pattern=Pattern.compile(regex);

        Matcher matcher=pattern.matcher(number);

        return matcher.matches();

    }

    /**
     * 正则表达式检验
     * @param input
     * @param regex
     * @return
     */
    public static boolean regularExpressions(String input , String regex ){
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(input);
        return  matcher.matches();
    }

    /**
     * 密码位数是否正确
     * @param password
     * @return
     */
    public static boolean isPasswordNumber(String password){
        if(password.length()>=6 && password.length()<=20){
            return true;
        }
        return false;
    }


    /**
     * 网络是否可连接
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context){

        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 判断是否存在SD卡
     * @return
     */
    public static boolean isSDCard(){

        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }

    }


    /**
     * 字符串转化为Date
     * @param str
     * @return
     */
    public static Date stringToDate(String str){
        Date date=null;
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");

        try {
            date=simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }


    /**
     * 总秒数转换为00:00:00 格式字符串
     * @param time
     * @return
     */
    public static  String secondsToString(int time) {

        int house = 0; //小时
        int minute = 0; // 分钟
        int second = 0; //秒

        String houseStr ="00";
        String minuteStr ="00";
        String secondStr = "00";

        second = time % 60;
        minute = (time / 60) % 60;
        house = (time /60) / 60;
        if (second <10) {
            secondStr = "0"+second;
        } else {
            secondStr = Integer.toString(second);
        }

        if ( minute < 10) {
            minuteStr = "0"+minute;
        } else {
            minuteStr = Integer.toString(minute);
        }

        if (house < 10) {
            houseStr = "0"+house;

        } else {
            houseStr = Integer.toString(house);
        }

        return houseStr+":"+minuteStr+":"+secondStr;

    }



}
