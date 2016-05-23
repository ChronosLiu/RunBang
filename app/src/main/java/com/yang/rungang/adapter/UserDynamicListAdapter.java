package com.yang.rungang.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yang.rungang.R;
import com.yang.rungang.model.bean.Dynamic;
import com.yang.rungang.utils.GeneralUtil;

import java.util.List;

/**
 * Created by 洋 on 2016/5/19.
 */
public class UserDynamicListAdapter extends BaseAdapter {

    private Context context;

    private ImageLoader imageLoader;

    private DisplayImageOptions options;


    private List<Dynamic> data;

    private LayoutInflater layoutInflater;

    public UserDynamicListAdapter(Context context,List<Dynamic> data){
        this.context = context;

        this.data = data;

        this.layoutInflater = LayoutInflater.from(context);

        this.imageLoader = ImageLoader.getInstance();

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.upload_head_pic)
                .showImageOnFail(R.drawable.upload_head_pic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
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
            convertView = layoutInflater.inflate(R.layout.item_list_user_dynamic_layout,parent,false);

            viewHolder.day = (TextView) convertView.findViewById(R.id.user_dynamic_day_text);
            viewHolder.month = (TextView) convertView.findViewById(R.id.user_dynamic_month_text);
            viewHolder.contentText = (TextView) convertView.findViewById(R.id.user_dynamic_content_text);
            viewHolder.countText = (TextView) convertView.findViewById(R.id.user_dynamic_picture_count_text);
            viewHolder.picture  = (ImageView) convertView.findViewById(R.id.user_dynamic_picture_img);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Dynamic dynamic = data.get(position);

        viewHolder.day.setText(GeneralUtil.getDayFromDate(dynamic.getCreatedAt()));
        viewHolder.month.setText(GeneralUtil.getMonthFromDate(dynamic.getCreatedAt())+"月");

        if (dynamic.getContent() != null && dynamic.getContent().length()>0) {
            viewHolder.contentText.setVisibility(View.VISIBLE);
            viewHolder.contentText.setText(dynamic.getContent());
        } else {
            viewHolder.contentText.setVisibility(View.GONE);
        }
        if( dynamic.getImage().size()>0&&dynamic.getImage()!= null) {

            imageLoader.displayImage(dynamic.getImage().get(0),viewHolder.picture,options);
            if (dynamic.getImage().size()>1) {
                viewHolder.countText.setVisibility(View.VISIBLE);
                viewHolder.countText.setText("共" + dynamic.getImage().size() + "张");
            } else {
                viewHolder.countText.setVisibility(View.GONE);
            }
        } else {
            viewHolder.picture.setVisibility(View.GONE);
            viewHolder.countText.setVisibility(View.GONE);
        }


        return convertView;
    }

    private class ViewHolder{
        private TextView day;
        private TextView month;
        private TextView contentText;
        private ImageView picture;
        private TextView countText;
    }
}
