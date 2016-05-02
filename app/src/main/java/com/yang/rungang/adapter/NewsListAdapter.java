package com.yang.rungang.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yang.rungang.R;
import com.yang.rungang.model.bean.News;
import com.yang.rungang.utils.FileUtil;
import com.yang.rungang.utils.IdentiferUtil;

import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DownloadFileListener;

/**
 *
 *资讯listview适配器
 *
 * Created by 洋 on 2016/5/2.
 */
public class NewsListAdapter extends BaseAdapter {


    private Context context;
    private LayoutInflater layoutInflater;
    private List<News> data;

    public NewsListAdapter(Context context,List<News> data) {
        this.context=context;
        this.data= data;
        layoutInflater = LayoutInflater.from(context);
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
         ViewHolder viewHolder = null;
        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_listview_news_layout,parent,false);
            viewHolder.picture = (ImageView) convertView.findViewById(R.id.news_item_img);
            viewHolder.title = (TextView) convertView.findViewById(R.id.news_item_title_text);
            viewHolder.brief = (TextView) convertView.findViewById(R.id.news_item_brief_text);
            viewHolder.number = (TextView) convertView.findViewById(R.id.news_item_read_number);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.title.setText(data.get(position).getTitle());
        viewHolder.brief.setText(data.get(position).getBrief());
        viewHolder.number.setText(data.get(position).getReadNumber() + " ");
//        BmobFile file = data.get(position).getPicture();
//
//        viewHolder.picture.setImageBitmap(data.get(position).getBitmap());





        return convertView;
    }

    private class ViewHolder{

        private ImageView picture; //图片

        private TextView title; // 标题

        private TextView brief; //简介

        private TextView number; //阅读次数

    }



}
