package com.yang.runbang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.yang.runbang.R;
import com.yang.runbang.model.bean.OLCity;
import com.yang.runbang.utils.GeneralUtil;


import java.util.List;

/**
 *
 * 离线地图城市列表适配器
 *
 * Created by 洋 on 2016/5/7.
 */
public class CityExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;

    private List<OLCity> data = null; //城市列表

    private LayoutInflater layoutInflater;


    public CityExpandableListAdapter(Context context, List<OLCity> data) {
        this.context = context;
        this.data = data;
        layoutInflater = LayoutInflater.from(context);


    }

    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.get(groupPosition).getChildCities().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(childPosition).getChildCities().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, final boolean isExpanded, View convertView, ViewGroup parent) {


        GroupViewholder groupViewholder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.expandlistview_group_layout,parent,false);
            groupViewholder = new GroupViewholder();
            groupViewholder.name = (TextView) convertView.findViewById(R.id.group_city_name);
            groupViewholder.imag = (ImageView) convertView.findViewById(R.id.group_img);
            convertView.setTag(groupViewholder);
        } else {
            groupViewholder = (GroupViewholder) convertView.getTag();
        }

        groupViewholder.name.setText(data.get(groupPosition).getCityName());

        if (isExpanded) {
            groupViewholder.imag.setImageResource(R.drawable.pull);
        } else {
            groupViewholder.imag.setImageResource(R.drawable.dropdown);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

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
        OLCity child = data.get(groupPosition).getChildCities().get(childPosition);
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

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {

        return true;
    }


    private class GroupViewholder {
        private TextView name;
        private ImageView imag;
    }

    private class ChildViewHolder {
        private TextView name;
        private TextView size;
        private TextView downloaded;
        private TextView radio;
        private ImageView downloadImg;
    }
}
