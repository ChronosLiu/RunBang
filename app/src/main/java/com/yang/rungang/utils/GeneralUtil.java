package com.yang.rungang.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

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

    /**
     * 距离(米）转化为字符串 0.00格式 公里
     * @param distance
     * @return
     */
    public static String doubleToString(double distance) {
        String distanceStr = "0.00";

        //转化为千米（公里）
        double km = distance/1000;

        // 四舍五入，保留2为小数
        double d= Math.round(km*100)/100.0;


        //转化为字符串
        if(d > 0) {
            distanceStr = String.valueOf(d);
        }


        return distanceStr;
    }

    /**
     * 空气质量指数AQI ，根据AQI数值返回空气质量状况
     * @param aqi
     * @return
     */
    public static String valueToAQIState( String aqi) {

        String state = null;
        int value = Integer.parseInt(aqi);

        if( value <= 50) {
            state = "优";
        } else if (value <= 100) {
            state = "良";
        } else if (value <= 150) {
            state = "轻度污染";
        } else if (value <= 200) {
            state = "中度污染";
        } else if (value <= 300) {
            state = "重度污染";
        } else if (value >300) {
            state = "严重污染";
        }
        return state;

    }


    /**
     * 判断是否打开GPS
     * @param context
     * @return
     */
    public static boolean isOpenGPS(Context context){

        LocationManager locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);

        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        boolean agps = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if(gps || agps) {
            return true;
        }
        return false;
    }


}
