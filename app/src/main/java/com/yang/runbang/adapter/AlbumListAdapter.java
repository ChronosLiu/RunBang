package com.yang.runbang.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.yang.runbang.R;
import com.yang.runbang.model.bean.ImageFloder;

import java.util.List;

/**
 * Created by 洋 on 2016/5/15.
 */
public class AlbumListAdapter extends BaseAdapter{

    private Context context;

    private List<ImageFloder> data;

    private LayoutInflater layoutInflater;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    public AlbumListAdapter (Context context,List<ImageFloder> data){

        this.context = context;

        this.data = data;

        this.layoutInflater = LayoutInflater.from(context);

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.upload_head_pic)
                .showImageOnFail(R.drawable.upload_head_pic)
                .cacheInMemory(true)
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
            convertView = layoutInflater.inflate(R.layout.item_listview_album_layout,parent,false);

            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.album_listview_img);

            viewHolder.textView = (TextView) convertView.findViewById(R.id.album_listview_name_count);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String url = ImageDownloader.Scheme.FILE.wrap(data.get(position).getFirstImagePath());

        imageLoader.displayImage(url, viewHolder.imageView, options);

        viewHolder.textView.setText(data.get(position).getName()+"("+data.get(position).getCount()+")");

        return convertView;
    }

    private class ViewHolder{
        private ImageView imageView;

        private TextView textView;
    }

}
