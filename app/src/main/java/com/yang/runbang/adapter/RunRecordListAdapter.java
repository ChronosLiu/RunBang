package com.yang.runbang.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yang.runbang.R;
import com.yang.runbang.model.bean.RunRecord;
import com.yang.runbang.utils.GeneralUtil;

import java.util.List;

/**
 * Created by 洋 on 2016/5/9.
 */
public class RunRecordListAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater layoutInflater;

    private List<RunRecord> data;

    public RunRecordListAdapter(Context context,List<RunRecord> data) {

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

        ViewHolder viewHolder = null;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_listview_run_record_layout,parent,false);
            viewHolder.createTime = (TextView) convertView.findViewById(R.id.record_create_time_text);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.record_distance_text);
            viewHolder.time = (TextView) convertView.findViewById(R.id.record_time_text);
            viewHolder.syncImg = (ImageView) convertView.findViewById(R.id.record_sync_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RunRecord runRecord = data.get(position);
        viewHolder.createTime.setText(runRecord.getCreateTime());
        viewHolder.distance.setText(GeneralUtil.doubleToString(runRecord.getDistance()));
        viewHolder.time.setText(GeneralUtil.secondsToString(runRecord.getTime()));

        if( runRecord.isSync()) { //已同步
            viewHolder.syncImg.setVisibility(View.GONE);
        } else {
            viewHolder.syncImg.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    private class ViewHolder {

        private TextView createTime;

        private TextView distance;

        private TextView time;

        private ImageView syncImg;


    }

}
