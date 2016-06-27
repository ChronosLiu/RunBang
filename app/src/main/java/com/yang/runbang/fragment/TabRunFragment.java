package com.yang.runbang.fragment;

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

import com.yang.runbang.R;
import com.yang.runbang.activity.RunActivity;
import com.yang.runbang.activity.RunRecordActivity;
import com.yang.runbang.db.DBManager;
import com.yang.runbang.https.HttpsUtil;
import com.yang.runbang.model.bean.IHttpCallback;
import com.yang.runbang.model.bean.RunRecord;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.model.bean.weather.WeatherData;
import com.yang.runbang.utils.ConfigUtil;
import com.yang.runbang.utils.GeneralUtil;
import com.yang.runbang.utils.IdentiferUtil;
import com.yang.runbang.utils.JsonUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.FindStatisticsListener;

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


    private static final int  request_code_from_friend = 0x11;

    private Button startBtn;
    private TextView ditanceText;
    private TextView timeText;
    private TextView scoreNumberText;


    private Context context;

    private double totalDistance = 0.0; //总距离

    private int totalTime = 0; //总时间

    private int totalCount =0; //次数

    private User user;

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
        user = BmobUser.getCurrentUser(context,User.class);

        initComponent(view);

        queryData();

        return view;
    }

    /**
     * 初始化组件
     * @param view
     */
    private void initComponent(View view){

        startBtn = (Button) view.findViewById(R.id.main_run_start_btn);
        ditanceText = (TextView) view.findViewById(R.id.main_run_data_distance);
        timeText = (TextView) view.findViewById(R.id.main_run_data_time);
        scoreNumberText = (TextView) view.findViewById(R.id.main_run_data_score);
        scoreNumberText.setOnClickListener(this);
        startBtn.setOnClickListener(this);
    }


    private void setView(){

        ditanceText.setText(GeneralUtil.doubleToString(totalDistance));
        timeText.setText(GeneralUtil.secondsToHourString(totalTime));
        scoreNumberText.setText(String.valueOf(totalCount));

    }
    @Override
    public void onClick(View v) {
         switch (v.getId()) {
             case R.id.main_run_start_btn:
                 Intent runIntent = new Intent(getActivity(),RunActivity.class);
                 startActivityForResult(runIntent, request_code_from_friend);
                 break;
             case R.id.main_run_data_score:
                 Intent recordIntent = new Intent(getActivity(),RunRecordActivity.class);
                 startActivityForResult(recordIntent, request_code_from_friend);
                 break;
         }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        queryData();

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 查询运动数据
     */
    private void queryData(){
        BmobQuery<RunRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("userId",user.getObjectId());
        query.setLimit(50);
        query.findObjects(context, new FindListener<RunRecord>() {
            @Override
            public void onSuccess(List<RunRecord> list) {
                if (list.size()>0) {
                    totalCount = 0;
                    totalDistance = 0.0;
                    totalTime =0;
                    for (RunRecord runRecord:list){
                        totalTime += runRecord.getTime();
                        totalDistance +=runRecord.getDistance();
                    }
                    totalCount = list.size();
                }

                new Handler().post(new Runnable() {
                    @Override
                    public void run() {

                        setView();
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }



}
