package com.yang.rungang.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.yang.rungang.R;
import com.yang.rungang.adapter.NewsListAdapter;
import com.yang.rungang.model.bean.IBmobCallback;
import com.yang.rungang.model.bean.News;
import com.yang.rungang.utils.BmobUtil;
import com.yang.rungang.utils.IdentiferUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFragment extends Fragment implements IBmobCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ListView mListView;
    private NewsListAdapter mAdapter;
    private List<News>  data = new ArrayList<>();


    public NewsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFragment newInstance(String param1, String param2) {
        NewsFragment fragment = new NewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_news, container, false);

        getData();

        mListView = (ListView) view.findViewById(R.id.news_listview);


        setmAdapter();



        return view;
    }

    private void getData(){

        BmobUtil.querySingleData(getActivity().getApplicationContext(),"o5NwIIIb",this);
        BmobUtil.querySingleData(getActivity().getApplicationContext(),"ecZAQQQd",this);
    }

    /**
     *
     */
    private void setmAdapter(){

        mAdapter = new NewsListAdapter(getActivity(),data);
        mListView.setAdapter(mAdapter);
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

    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IdentiferUtil.QUERY_SINGLE_DATA_SUCCESS:
                    News news= (News) msg.obj;
                    data.add(news);
                    mAdapter.notifyDataSetChanged();
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
