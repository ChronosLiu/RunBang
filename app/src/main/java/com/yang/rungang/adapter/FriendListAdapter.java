package com.yang.rungang.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.yang.rungang.R;
import com.yang.rungang.model.bean.User;

import java.util.List;

/**
 * Created by 洋 on 2016/5/19.
 */
public class FriendListAdapter extends BaseAdapter {

    private Context context;
    private List<User> data;
    private LayoutInflater layoutInflater;

    private ImageLoader imageLoader;
    private DisplayImageOptions circleOptions;

    public FriendListAdapter(Context context,List<User> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();

        circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.upload_head_pic)
                .showImageOnFail(R.drawable.upload_head_pic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_list_friend_layout,parent,false);
            viewHolder.headimg = (ImageView) convertView.findViewById(R.id.friend_item_head_img);
            viewHolder.name = (TextView) convertView.findViewById(R.id.friend_itme_name_text);
            viewHolder.followImg = (ImageView) convertView.findViewById(R.id.friend_item_follow_img);
            viewHolder.followLayout = (LinearLayout) convertView.findViewById(R.id.friend_item_follow_layout);
            viewHolder.followText = (TextView) convertView.findViewById(R.id.friend_item_follow_text);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User user = data.get(position);

        imageLoader.displayImage(user.getHeadImgUrl(),viewHolder.headimg,circleOptions);
        viewHolder.name.setText(user.getNickName());


        return convertView;
    }

    private class ViewHolder{
        private ImageView headimg;
        private TextView name;
        private ImageView followImg;
        private TextView followText;
        private LinearLayout followLayout;
    }

}
