package com.yang.rungang.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.yang.rungang.R;
import com.yang.rungang.activity.DynamicDetailsActivity;
import com.yang.rungang.adapter.DynamicListAdapter;
import com.yang.rungang.model.bean.Dynamic;
import com.yang.rungang.model.bean.Timeline;
import com.yang.rungang.model.bean.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.AsyncCustomEndpoints;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.CloudCodeListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetServerTimeListener;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link TabHomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabHomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Context context;

    private SwipeRefreshLayout refreshLayout;

    private ListView mListView;

    private User user;

    private AsyncCustomEndpoints ace = null;

    private String timelineId; //用户时间线id

    private List<Dynamic> data; //数据

    private DynamicListAdapter adapter; //适配器

    private BmobDate endUpdateTime; //最后更新时间



    public TabHomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TabHomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TabHomeFragment newInstance(String param1, String param2) {
        TabHomeFragment fragment = new TabHomeFragment();
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
        View view = inflater.inflate(R.layout.fragment_tab_home, container, false);

        user = BmobUser.getCurrentUser(context,User.class);
        getUpdateTime();

        initComponent(view);

        if(user!= null) {
            getTimelineId();
        }

        setAdapter();

        setListener();

        return view;
    }

    /**
     * 初始化组件
     * @param view
     */
    private void initComponent(View view) {
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.fragment_home_swiperefresh);
        refreshLayout.setColorSchemeResources(android.R.color.holo_purple, android.R.color.holo_blue_bright, android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mListView = (ListView) view.findViewById(R.id.fragment_home_listview);
    }


    /**
     * 设置适配器
     */
    private void setAdapter() {
        if(data != null) {
            adapter = new DynamicListAdapter(context, data);
            mListView.setAdapter(adapter);
        }

    }


    /**
     * 设置监听
     */
    private void setListener(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //获取动态
                getTimelineId();
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent (getActivity(), DynamicDetailsActivity.class);
                intent.putExtra("dynamicId",data.get(position).getObjectId());
                startActivity(intent);

            }
        });
    }
    /**
     * 获取动态
     */
    private void getData(){

        BmobQuery<Dynamic> query = new BmobQuery<>();

        Timeline timeline = new Timeline();
        timeline.setObjectId(timelineId);

        Log.i("TAG", timelineId);

//        query.order("-createAt"); //降序

        query.addWhereGreaterThanOrEqualTo("createdAt", endUpdateTime);

        query.addWhereRelatedTo("allDynamic", new BmobPointer(timeline));


        query.include("fromUser");
        query.findObjects(context, new FindListener<Dynamic>() {
            @Override
            public void onSuccess(List<Dynamic> list) {
                if(list.size()>0 && list!= null){
                    data = list;
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            setAdapter();
                        }
                    });
                }else {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            refreshLayout.setRefreshing(false);
                            Toast.makeText(context, "已是最新动态", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            @Override
            public void onError(int i, String s) {

            }
        });
    }

    /**
     * 获取用户对应时间线id
     */
    private void getTimelineId(){
        ace = new AsyncCustomEndpoints();

        JSONObject params = new JSONObject();
        try {
            params.put("objectid",user.getObjectId());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ace.callEndpoint(context, "getTimelineId", params, new CloudCodeListener() {
            @Override
            public void onSuccess(Object o) {
                timelineId = o.toString();


                getData();
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     * 获取服务器时间
     */
    private void getServerTime(){

        Bmob.getServerTime(context, new GetServerTimeListener() {
            @Override
            public void onSuccess(long l) {
                endUpdateTime = new BmobDate(new Date(l*1000L));
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    /**
     *保存最后刷新时间到SharedPreferences中
     */
    private void saveUpdateTime(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("config",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endUpdateTime);
        editor.putString("endUpdateTime",time);
        editor.commit();
    }

    /**
     * 从配置中获取保存的最后刷新时间
     */
    private void getUpdateTime(){
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("config",Context.MODE_PRIVATE);

        String time = sharedPreferences.getString("endUpdateTime", "2016-05-01 00:00:00");
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        endUpdateTime = new BmobDate(date);
    }

}
