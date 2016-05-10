package com.yang.rungang.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.rungang.R;
import com.yang.rungang.activity.RunActivity;
import com.yang.rungang.activity.RunRecordActivity;
import com.yang.rungang.cache.ACache;
import com.yang.rungang.db.DBManager;
import com.yang.rungang.https.HttpsUtil;
import com.yang.rungang.model.bean.IHttpCallback;
import com.yang.rungang.model.bean.RunRecord;
import com.yang.rungang.model.bean.weather.WeatherData;
import com.yang.rungang.utils.ConfigUtil;
import com.yang.rungang.utils.GeneralUtil;
import com.yang.rungang.utils.IdentiferUtil;
import com.yang.rungang.utils.JsonUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TabRunFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabRunFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String cityName; //城市名称

    private String cityCode; //城市id



    private Button startBtn;
    private TextView ditanceText;
    private TextView timeText;
    private TextView scoreNumberText;

    private TextView cityText;
    private TextView tempText;
    private TextView airQuality;
    private TextView pm2_5Text;
    private TextView stateText;
    private TextView timeNowText;

    private RelativeLayout weatherLayout;

    private Context context;

    private List<RunRecord> data = null;

    private double totalDistance = 0.0; //总距离
    private int totalTime = 0; //总时间

    private int totalCount =0; //次数

    public TabRunFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabRunFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TabRunFragment newInstance(String param1, String param2) {
        TabRunFragment fragment = new TabRunFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        if (getArguments() != null) {
            cityName = getArguments().getString(ARG_PARAM1);
            cityCode = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_run, container, false);

        initComponent(view);
        if(cityCode == null){
            getLocatinCity();
        }
//        queryWeatherfromNet();

        countTotalData();

        setView();


        return view;
    }

    /**
     * 初始化组件
     * @param view
     */
    private void initComponent(View view){

        timeNowText = (TextView) view.findViewById(R.id.main_run_weather_time);
        stateText = (TextView) view.findViewById(R.id.main_run_weather_sate);
        startBtn = (Button) view.findViewById(R.id.main_run_start_btn);
        ditanceText = (TextView) view.findViewById(R.id.main_run_data_distance);
        timeText = (TextView) view.findViewById(R.id.main_run_data_time);
        scoreNumberText = (TextView) view.findViewById(R.id.main_run_data_score);
        cityText = (TextView) view.findViewById(R.id.main_run_weather_city);
        tempText = (TextView) view.findViewById(R.id.main_run_weather_temp);
        airQuality = (TextView) view.findViewById(R.id.main_run_weather_airquality);
        pm2_5Text = (TextView) view.findViewById(R.id.main_run_weather_pm_2_5);
        weatherLayout = (RelativeLayout) view.findViewById(R.id.main_run_weather);

        scoreNumberText.setOnClickListener(this);
        startBtn.setOnClickListener(this);
    }


    private void setView(){

        ditanceText.setText(GeneralUtil.doubleToString(totalDistance));
        timeText.setText(GeneralUtil.secondsToHourString(totalTime));
        scoreNumberText.setText(String.valueOf(totalCount));

    }
    /**
     * 从网络获取天气信息
     *
     */
    private void queryWeatherfromNet() {
        if(cityCode == null) {

            weatherLayout.setVisibility(View.GONE);

        } else {
            String url = ConfigUtil.WEATHER_API + "?cityid=" + cityCode;
            HttpsUtil.httpGetRequest(url, new IHttpCallback() {
                @Override
                public void onSuccess(String response) {
                    Log.i("TAG",response.toString());
                    WeatherData weatherData = JsonUtil.parseWeatherJson(response.toString());
                    if (weatherData != null) {
                        Message msg = new Message();
                        msg.what = IdentiferUtil.GET_WEATHER_SUCCESS;
                        msg.obj = weatherData;
                        handler.sendMessage(msg);
                    }
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }
    @Override
    public void onClick(View v) {
         switch (v.getId()) {
             case R.id.main_run_start_btn:
                 Intent runIntent = new Intent(getActivity(),RunActivity.class);
                 startActivity(runIntent);
                 break;
             case R.id.main_run_data_score:
                 Intent recordIntent = new Intent(getActivity(),RunRecordActivity.class);
                 startActivity(recordIntent);
                 break;
         }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IdentiferUtil.GET_WEATHER_SUCCESS : //获取天气成功

                    weatherLayout.setVisibility(View.VISIBLE);
                    WeatherData weatherData = (WeatherData) msg.obj;

                    cityText.setText(weatherData.getBasic().getCity());

                    stateText.setText(weatherData.getNow().getCond().getTxt());

                    tempText.setText(weatherData.getNow().getTmp());

                    timeNowText.setText(new SimpleDateFormat("MM-dd").format(new Date()));

                    airQuality.setText(GeneralUtil.valueToAQIState(weatherData.getAqi().getCity().getAqi()));

                    pm2_5Text.setText(weatherData.getAqi().getCity().getPm25());

                    break;
            }
            super.handleMessage(msg);
        }
    };

    private void getLocatinCity(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("rungang",Context.MODE_PRIVATE);
        cityName = sharedPreferences.getString("cityname",null);
        cityCode = sharedPreferences.getString("citycode",null);
    }

    private void countTotalData(){
        data = DBManager.getInstance(context).getRunRecords();
        for( RunRecord runRecord:data){
            if(runRecord.isSync()){
                totalDistance += runRecord.getDistance();
                totalTime += runRecord.getTime();
            }
        }

        totalCount = data.size();

    }




}
