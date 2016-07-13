package com.yang.runbang.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.yang.runbang.R;
import com.yang.runbang.listener.OnRecyclerViewListener;
import com.yang.runbang.model.bean.Dynamic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 洋 on 2016/6/11.
 */
public class DynamicRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_FOOTER = 0;//上拉加载
    private static final int TYPE_ITEM_ONE = 1;
    private static final int TYPE_ITEM_TWO = 2;
    private static final int TYPE_ITEM_THIRD = 3;
    private static final int TYPE_ITEM_FOUR = 4;

    private boolean isLoadMore = false;//是否显示footerview,默认不显示

    private OnRecyclerViewListener listener;

    private String noticeStr = null;
    private List<Dynamic> likes = new ArrayList<>();

    private List<Dynamic> data = null;

    public int lastposition =-1;

    public DynamicRecyclerAdapter(OnRecyclerViewListener listener){
        this.listener = listener;
    }

    public void setData(List<Dynamic> data) {
        this.data = data;
    }

    public void setNoticeStr(String noticeStr) {
        this.noticeStr = noticeStr;
    }

    public boolean isLoadMore() {

        return isLoadMore;
    }

    public void setLikes(List<Dynamic> likes) {
        this.likes = likes;
    }


    /**
     * 设置是否显示上拉加载更多，默认不显示
     * @param isLoadMore
     */
    public void setIsLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;

        //刷新数据
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {

            case 1://一张图片

                View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_dynamic_layout1,parent,false);

                return new DynamicViewHolder1(view1,parent.getContext(),listener);

            case 2:
                View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_dynamic_layout2,parent,false);

                return new DynamicViewHolder2(view2,parent.getContext(),listener);
            case 3:
                View view3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_dynamic_layout3,parent,false);

                return new DynamicViewHolder3(view3,parent.getContext(),listener);

            case 4:
                View view4 = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_dynamic_layout4,parent,false);

                return new DynamicViewHolder4(view4,parent.getContext(),listener);

            case TYPE_FOOTER:

                View footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_foot_view_layout,parent,false);

                return new RecyclerFootViewHolder(footerView,parent.getContext(),listener);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof RecyclerFootViewHolder) {
            ((BaseViewHolder) holder).bindData("加载中...");
        } else {
            ((BaseViewHolder) holder).setLikes(likes);
            ((BaseViewHolder) holder).bindData(data.get(position));

            if(position>lastposition){
                ((BaseViewHolder) holder).setAnimation();
                lastposition = position;
            }
        }


    }

    @Override
    public int getItemViewType(int position) {



        if (isLoadMore&&position+1 == getItemCount()){

            return TYPE_FOOTER;

        } else {
            if (data == null || data.size() <= 0) {//数据集不存在
                return 0;
            } else if (data.get(position).getImage() == null || data.get(position).getImage().size() == 0) {
                return 1;
            } else if (data.get(position).getImage().size() <= 4) {
                return data.get(position).getImage().size();
            } else {
                return 4;
            }
        }
    }

    @Override
    public int getItemCount() {
        if (isLoadMore) {
            return data==null||data.size()==0?0:data.size()+1;
        }
        return data==null||data.size()==0?0:data.size();
    }

}
