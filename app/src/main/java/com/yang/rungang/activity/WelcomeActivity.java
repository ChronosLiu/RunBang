package com.yang.rungang.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;

import com.yang.rungang.R;
import com.yang.rungang.db.DBManager;
import com.yang.rungang.https.HttpsUtil;
import com.yang.rungang.model.bean.IHttpCallback;
import com.yang.rungang.model.bean.WeatherCity;
import com.yang.rungang.model.biz.CityList;
import com.yang.rungang.utils.ConfigUtil;
import com.yang.rungang.utils.GeneralUtil;
import com.yang.rungang.utils.JsonUtil;

import java.util.List;

public class WelcomeActivity extends BaseActivity {

    private boolean isFirstUse ; //首次使用标识,默认true
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_welcome);

        getFirstUserIndentifer();

        //配置app
        configAPP();

        isFirstUse = false;

        saveFirstUseIdentifer();

        //延迟3秒执行，跳入主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                WelcomeActivity.this.finish();
            }
        }, 3 * 1000);

    }

    /**
     * 配置app
     * @return
     */
    private void configAPP(){

        if(isFirstUse) { //首次使用

            //获取天气城市列表，存储进数据库
            getWeatherCityList();

        }
    }


    /**
     * 保存首次使用app标示
     */
    private void saveFirstUseIdentifer() {

        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("firstuse",isFirstUse);
        editor.commit();
    }

    /**
     * 获取首次使用app标示
     */
    private void getFirstUserIndentifer() {
        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        isFirstUse = sharedPreferences.getBoolean("firstuse",true);

    }

    /**
     * 获取天气城市列表,存储到数据库中
     */
    private void getWeatherCityList(){

        if(GeneralUtil.isNetworkAvailable(context)) {

            HttpsUtil.getCityList(ConfigUtil.CITY_LIST_API, new IHttpCallback() {
                @Override
                public void onSuccess(String response) {

                    CityList cityList = JsonUtil.parseCityListJson(response.toString());

                    DBManager.getInstance(context).insertCitys(cityList.getCities());

                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }

    private void getOfflineCity(){

        if(GeneralUtil.isNetworkAvailable(context)) {

        }
    }

}
