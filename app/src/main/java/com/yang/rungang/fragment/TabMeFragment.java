package com.yang.rungang.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.rungang.R;
import com.yang.rungang.activity.RunRecordActivity;
import com.yang.rungang.db.DBManager;
import com.yang.rungang.https.HttpsUtil;
import com.yang.rungang.model.bean.IBmobCallback;
import com.yang.rungang.model.bean.IHttpCallback;
import com.yang.rungang.model.bean.RunRecord;
import com.yang.rungang.model.bean.User;
import com.yang.rungang.utils.BmobUtil;
import com.yang.rungang.utils.ConfigUtil;
import com.yang.rungang.utils.FileUtil;
import com.yang.rungang.utils.GeneralUtil;
import com.yang.rungang.utils.IdentiferUtil;
import com.yang.rungang.view.RoundImageView;

import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TabMeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabMeFragment extends Fragment implements View.OnClickListener, IBmobCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REQUEST_CODE_SCORE = 0x11;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private RoundImageView headImg;
    private TextView usernameText;
    private TextView followNumber;
    private TextView fansNubmer;
    private RelativeLayout followRelative;
    private RelativeLayout fansRelative;
    private TextView meDistanceText;
    private TextView meTimeText;
    private RelativeLayout meRunScoreRelative;
    private RelativeLayout meFriendsListRelative;
    private RelativeLayout meScanRelative;


    private Context context;

    private User user;

    private int fansCount = 0; //粉丝数

    private int followCount = 0; //关注人数

    private double totalDistance = 0.0; //总距离

    private int totalTime = 0; //总时间

    private List<RunRecord> runRecords; // 跑步记录

    public TabMeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabMeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TabMeFragment newInstance(String param1, String param2) {
        TabMeFragment fragment = new TabMeFragment();
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_me, container, false);

        user = BmobUser.getCurrentUser(context,User.class);

        initComponent(view);

        setView();

        return view;
    }

    /**
     * 初始化组件
     */
    private void initComponent(View view){
        headImg = (RoundImageView) view.findViewById(R.id.me_headimg_roundImg);
        usernameText = (TextView) view.findViewById(R.id.me_username_text);
        followNumber = (TextView) view.findViewById(R.id.me_follow_number_text);
        followRelative = (RelativeLayout) view.findViewById(R.id.me_follow_relative);
        fansNubmer = (TextView) view.findViewById(R.id.me_fans_number_text);
        fansRelative = (RelativeLayout) view.findViewById(R.id.me_fans_relative);

        meDistanceText = (TextView) view.findViewById(R.id.me_count_distance);
        meTimeText = (TextView) view.findViewById(R.id.me_count_time);

        meRunScoreRelative = (RelativeLayout) view.findViewById(R.id.me_run_score_relative);
        meFriendsListRelative = (RelativeLayout) view.findViewById(R.id.me_run_list_relative);
        meScanRelative = (RelativeLayout) view.findViewById(R.id.me_scan_relative);

        headImg.setOnClickListener(this);
        followRelative.setOnClickListener(this);
        fansRelative.setOnClickListener(this);
        meRunScoreRelative.setOnClickListener(this);
        meFriendsListRelative.setOnClickListener(this);
        meScanRelative.setOnClickListener(this);

    }

    private void setView(){

        if(user == null) {
            return;
        }
        if(user.getNickName()==null || user.getNickName().length()<=0) {
            usernameText.setText("帮众");
        } else {
            usernameText.setText(user.getNickName());
        }
        setHeadImg();
        countTotalData();
        getFansAndFollow();

    }

    private void setHeadImg() {

        if(user == null){
            return;
        }
        String picpath = user.getHeadImgPath();
        if(picpath==null) {
            return;
        }else {//本地图片路径存在
            Bitmap image= FileUtil.getBitmapFromFile(picpath);

            if(image == null){ //文件不存在，从网络获取

                String url = user.getHeadImgUrl();
                if (url != null) {
                    BmobUtil.downHeadImg(context, url, this);
                }
            } else {
                headImg.setImageBitmap(image);
            }
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.me_run_score_relative: //跑步记录

                Intent scoreIntent = new Intent(getActivity(), RunRecordActivity.class);
                startActivityForResult(scoreIntent,REQUEST_CODE_SCORE);
                break;

            case R.id.me_run_list_relative: //排行榜

                break;

            case R.id.me_scan_relative: //扫一扫

                break;
        }

    }

    @Override
    public void onFinish(int identifier, Object object) {
        Message msg = new Message();
        msg.what=identifier;
        if(object!=null){
            msg.obj=object;
        }
        handler.handleMessage(msg);
    }

    @Override
    public void onFailure(int identifier) {

        Message msg = new Message();
        msg.what=identifier;
        handler.handleMessage(msg);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SCORE) {
            countTotalData();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case IdentiferUtil.DOWN_FILE_SUCCESS://下载文件成功

                    String path = (String) msg.obj;
                    Bitmap bitmap = FileUtil.getBitmapFromFile(path);
                    if (bitmap!=null) {
                        headImg.setImageBitmap(bitmap);
                    }

                    //更新数据
                    user.setHeadImgPath(path);

                    user.update(context, user.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Log.i("TAG","更新头像路径成功");
                        }

                        @Override
                        public void onFailure(int i, String s) {

                        }
                    });
                    break;
                case IdentiferUtil.DOWN_FILE_FAIL: // 下载文件失败

                    break;

            }
            super.handleMessage(msg);
        }
    };


    /**
     * 统计
     */
    private void countTotalData(){

        runRecords = DBManager.getInstance(context).getRunRecords();
        for( RunRecord runRecord:runRecords){
            if(runRecord.isSync()){
                totalDistance += runRecord.getDistance();
                totalTime += runRecord.getTime();
            }
        }


        meDistanceText.setText(GeneralUtil.doubleToString(totalDistance));
        meTimeText.setText(GeneralUtil.secondsToHourString(totalTime));

    }


    private void getFansAndFollow(){

        String fansSql= "http://cloud.bmob.cn/"+ ConfigUtil.BMOB_SECRET_KEY+"/getFansCount?objectid="+user.getObjectId();

        HttpsUtil.sendGetRequest(fansSql, new IHttpCallback() {
            @Override
            public void onSuccess(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        fansCount = Integer.parseInt(response);

                        fansNubmer .setText(response);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        String followSql = "http://cloud.bmob.cn/"+ ConfigUtil.BMOB_SECRET_KEY+"/getFollowCount?objectid="+user.getObjectId();

        HttpsUtil.sendGetRequest(followSql, new IHttpCallback() {
            @Override
            public void onSuccess(final String response) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        followCount = Integer.parseInt(response);

                        followNumber.setText(response);
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

    }


}
