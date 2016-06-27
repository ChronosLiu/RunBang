package com.yang.runbang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.yang.runbang.R;
import com.yang.runbang.model.bean.OLCity;
import com.yang.runbang.utils.GeneralUtil;

import java.util.ArrayList;

/**
 * Created by 洋 on 2016/5/8.
 */
public class CitylistViewAdapter extends BaseAdapter {

    private Context context;

    private ArrayList<OLCity> data;

    private LayoutInflater layoutInflater;

    public CitylistViewAdapter(Context context,ArrayList<OLCity> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
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
        ChildViewHolder childViewHolder = null;
        if (convertView == null ) {
            childViewHolder = new ChildViewHolder();
            convertView = layoutInflater.inflate(R.layout.expandlistview_child_layout,parent,false);
            childViewHolder.name = (TextView) convertView.findViewById(R.id.child_city_name);
            childViewHolder.size = (TextView) convertView.findViewById(R.id.chile_data_size);
            childViewHolder.radio = (TextView) convertView.findViewById(R.id.child_radio);
            childViewHolder.downloaded = (TextView) convertView.findViewById(R.id.child_downloaded);
            childViewHolder.downloadImg = (ImageView) convertView.findViewById(R.id.child_download_img);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        OLCity child = data.get(position);
        childViewHolder.name.setText(child.getCityName());
        childViewHolder.size.setText(GeneralUtil.formatDataSize(child.getSize()));

        switch (child.getStatus()) {
            case MKOLUpdateElement.DOWNLOADING: //正在下载
                childViewHolder.radio.setVisibility(View.VISIBLE);
                childViewHolder.radio.setText(child.getRatio()+"%");
                childViewHolder.downloaded.setVisibility(View.GONE);
                childViewHolder.downloadImg.setVisibility(View.VISIBLE);
                childViewHolder.downloadImg.setImageResource(R.drawable.pausedownload);
                break;

            case MKOLUpdateElement.SUSPENDED: //暂停
                childViewHolder.radio.setVisibility(View.VISIBLE);
                childViewHolder.radio.setText(child.getRatio()+"%");
                childViewHolder.downloaded.setVisibility(View.GONE);
                childViewHolder.downloadImg.setVisibility(View.VISIBLE);
                childViewHolder.downloadImg.setImageResource(R.drawable.startdownload);
                break;

            case MKOLUpdateElement.eOLDSFormatError: //数据错误，需重新下载

                childViewHolder.radio.setVisibility(View.GONE);
                childViewHolder.downloaded.setVisibility(View.VISIBLE);
                childViewHolder.downloadImg.setVisibility(View.GONE);
                childViewHolder.downloaded.setText("重新下载");

                break;

            case MKOLUpdateElement.eOLDSInstalling: //离线包导入
                childViewHolder.radio.setVisibility(View.GONE);
                childViewHolder.downloaded.setVisibility(View.VISIBLE);
                childViewHolder.downloadImg.setVisibility(View.GONE);
                childViewHolder.downloaded.setText("解压安装中");
                break;
            case MKOLUpdateElement.FINISHED : //完成
                childViewHolder.radio.setVisibility(View.GONE);
                childViewHolder.downloaded.setVisibility(View.VISIBLE);
                childViewHolder.downloadImg.setVisibility(View.GONE);
                childViewHolder.downloaded.setText("已下载");
                break;
            case MKOLUpdateElement.WAITING ://等待下载
                childViewHolder.radio.setVisibility(View.GONE);
                childViewHolder.downloaded.setVisibility(View.VISIBLE);
                childViewHolder.downloadImg.setVisibility(View.GONE);
                childViewHolder.downloaded.setText("等待下载");
                break;
            case 0:
                childViewHolder.radio.setVisibility(View.GONE);
                childViewHolder.downloadImg.setVisibility(View.VISIBLE);
                childViewHolder.downloaded.setVisibility(View.GONE);
                childViewHolder.downloadImg.setImageResource(R.drawable.download);
                break;
            default:
                childViewHolder.radio.setVisibility(View.GONE);
                childViewHolder.downloaded.setVisibility(View.VISIBLE);
                childViewHolder.downloadImg.setVisibility(View.GONE);
                childViewHolder.downloaded.setText("出现错误，重新下载");
                break;
        }

        return convertView;

    }

    private class ChildViewHolder {
        private TextView name;
        private TextView size;
        private TextView downloaded;
        private TextView radio;
        private ImageView downloadImg;
    }
}
