package com.yang.runbang.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.yang.runbang.R;
import com.yang.runbang.activity.FriendActivity;
import com.yang.runbang.activity.PersonProfileActivity;
import com.yang.runbang.activity.RunRecordActivity;
import com.yang.runbang.activity.SetActivity;
import com.yang.runbang.db.DBManager;
import com.yang.runbang.model.bean.Friend;
import com.yang.runbang.model.bean.RunRecord;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.utils.GeneralUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TabMeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabMeFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int Query_Fans_Count_Success = 0x11;
    private static final int Query_Follow_Count_Success = 0x12;

    private static final int REQUEST_CODE_SCORE = 0x13;

    private static final int request_code_for_friend = 0x14;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private ImageView avatarImg;
    private TextView nicknameText;
    private TextView followNumber;
    private TextView fansNubmer;
    private RelativeLayout followRelative;
    private RelativeLayout fansRelative;
    private TextView meDistanceText;
    private TextView meTimeText;
    private RelativeLayout meRunScoreRelative;
    private RelativeLayout setRelative;


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
        avatarImg = (ImageView) view.findViewById(R.id.image_fragment_me_avatar);
        nicknameText = (TextView) view.findViewById(R.id.me_username_text);
        followNumber = (TextView) view.findViewById(R.id.me_follow_number_text);
        followRelative = (RelativeLayout) view.findViewById(R.id.me_follow_relative);
        fansNubmer = (TextView) view.findViewById(R.id.me_fans_number_text);
        fansRelative = (RelativeLayout) view.findViewById(R.id.me_fans_relative);

        meDistanceText = (TextView) view.findViewById(R.id.me_count_distance);
        meTimeText = (TextView) view.findViewById(R.id.me_count_time);

        meRunScoreRelative = (RelativeLayout) view.findViewById(R.id.me_run_score_relative);
        setRelative = (RelativeLayout) view.findViewById(R.id.relative_set);

        avatarImg.setOnClickListener(this);
        followRelative.setOnClickListener(this);
        fansRelative.setOnClickListener(this);
        meRunScoreRelative.setOnClickListener(this);
        setRelative.setOnClickListener(this);

    }

    /**
     * 初始化设置视图，填充数据
     */
    private void setView(){

        if(user == null) {
            return;
        }
        nicknameText.setText(user.getNickName());
        setHeadImg();

        queryData();
        queryFollowCount();
        queryFansCount();
    }

    /**
     * 设置头像
     */
    private void setHeadImg() {

        if(user == null|| user.getHeadImgUrl()==null){
            return;
        }
        DisplayImageOptions circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar)
                .showImageOnFail(R.drawable.default_avatar)
                .showImageForEmptyUri(R.drawable.default_avatar)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();

        ImageLoader.getInstance().displayImage(user.getHeadImgUrl(), avatarImg, circleOptions);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.image_fragment_me_avatar: //头像
                Intent avatarIntent = new Intent (getActivity(), PersonProfileActivity.class);
                avatarIntent.putExtra("userid",user.getObjectId());
                startActivity(avatarIntent);
                break;

            case R.id.me_follow_relative: //关注
                Intent followIntent = new Intent(getActivity(), FriendActivity.class);
                followIntent.putExtra("userid",user.getObjectId());
                followIntent.putExtra("username",user.getNickName());
                followIntent.putExtra("sign",false);
                startActivityForResult(followIntent,request_code_for_friend);
                break;
            case R.id.me_fans_relative: //粉丝
                Intent fansIntent = new Intent(getActivity(), FriendActivity.class);
                fansIntent.putExtra("userid",user.getObjectId());
                fansIntent.putExtra("username",user.getNickName());
                fansIntent.putExtra("sign",true);
                startActivityForResult(fansIntent,request_code_for_friend);
                break;

            case R.id.me_run_score_relative: //跑步记录

                Intent scoreIntent = new Intent(getActivity(), RunRecordActivity.class);
                startActivityForResult(scoreIntent,REQUEST_CODE_SCORE);
                break;

            case R.id.relative_set: //设置
                Intent setIntent = new Intent(getActivity(), SetActivity.class);
                startActivity(setIntent);
                break;

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_SCORE) {
            queryData();
        } else if (requestCode == request_code_for_friend){
            queryFansCount();
            queryFollowCount();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Query_Fans_Count_Success:
                    fansNubmer.setText(fansCount+"");
                    break;
                case Query_Follow_Count_Success:
                    followNumber.setText(followCount+"");
                    break;

            }
            super.handleMessage(msg);
        }
    };


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
                if (list.size() > 0) {
                    totalDistance = 0.0;
                    totalTime = 0;
                    for (RunRecord runRecord : list) {
                        totalTime += runRecord.getTime();
                        totalDistance += runRecord.getDistance();
                    }
                }
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        meDistanceText.setText(GeneralUtil.doubleToString(totalDistance));
                        meTimeText.setText(GeneralUtil.secondsToHourString(totalTime));
                    }
                });
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 查询粉丝数量
     */
    private void queryFansCount() {

        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("toUser", user);
        query.count(context, Friend.class, new CountListener() {
            @Override
            public void onSuccess(int i) {

                fansCount = i;
                Message msg = new Message();
                msg.what = Query_Fans_Count_Success;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {

                Log.i("TAG", s + i);
            }
        });

    }

    /**
     * 查询关注数量
     */
    private void queryFollowCount() {

        BmobQuery<Friend> query = new BmobQuery<>();
        query.addWhereEqualTo("fromUser", user);
        query.count(context, Friend.class, new CountListener() {
            @Override
            public void onSuccess(int i) {

                followCount = i;
                Message msg = new Message();
                msg.what = Query_Follow_Count_Success;
                handler.sendMessage(msg);
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });

    }


}
