package com.yang.runbang.adapter;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.yang.runbang.R;
import com.yang.runbang.listener.OnRecyclerViewClickListener;
import com.yang.runbang.model.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 洋 on 2016/6/6.
 */
public class AddFriendRecyclerAdapter extends RecyclerView.Adapter<AddFriendRecyclerAdapter.ViewHolder> {

    private OnRecyclerViewClickListener listener=null;
    private DisplayImageOptions circleOptions;
    private List<User> data= new ArrayList<>();

    private List<User> followData = new ArrayList<>();

    private User user = null;

    public void setData(List<User> data) {
        this.data = data;
    }

    public List<User> getData() {
        return data;
    }

    public void setFollowData(List<User> followData) {
        this.followData = followData;
    }

    public AddFriendRecyclerAdapter(User user,OnRecyclerViewClickListener listener) {
        this.user = user;
        this.listener = listener;
        this.circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar_blue)
                .showImageOnFail(R.drawable.default_avatar_blue)
                .showImageForEmptyUri(R.drawable.default_avatar_blue)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_friend_recyclerview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {


            ImageLoader.getInstance().displayImage(data.get(position).getHeadImgUrl(), holder.avatar, circleOptions);

            holder.nickName.setText(data.get(position).getNickName());

            if (followData!=null&&followData.size()>0&&followData.contains(data.get(position))) {
                holder.aleadyFollow.setVisibility(View.VISIBLE);
            } else if (user!=null&&user.equals(data.get(position))){
                holder.followLayout.setVisibility(View.INVISIBLE);
                holder.aleadyFollow.setVisibility(View.INVISIBLE);
            }else {
                holder.followLayout.setVisibility(View.VISIBLE);
            }

    }

    @Override
    public int getItemCount() {
        return data==null||data.size()<=0?0:data.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView avatar;
        private TextView nickName;
        private LinearLayout followLayout;
        private LinearLayout aleadyFollow;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = (ImageView) itemView.findViewById(R.id.image_item_add_friend_avatar);
            nickName = (TextView) itemView.findViewById(R.id.text_item_add_friend_name);
            followLayout = (LinearLayout) itemView.findViewById(R.id.linear_item_add_friend_follow);
            aleadyFollow = (LinearLayout) itemView.findViewById(R.id.linear_item_add_friend_aleady_follow);

            itemView.setOnClickListener(this);

            followLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onChildClick(getAdapterPosition(),R.id.linear_item_add_friend_follow);
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (listener!=null) {
                listener.onItemClick(getAdapterPosition());
            }
        }
    }
}
