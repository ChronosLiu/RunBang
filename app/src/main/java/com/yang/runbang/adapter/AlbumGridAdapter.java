package com.yang.runbang.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.download.ImageDownloader;
import com.yang.runbang.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义显示相册的GridView的adapter
 * Created by 洋 on 2016/4/25.
 */
public class AlbumGridAdapter extends BaseAdapter {

    private Context context;

    private LayoutInflater layoutInflater;

    private List<String> data;

    public List<String> mSelectedData = new ArrayList<>(); //选择的图片

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private DisplayImageOptions options;

    private String  mDirPath;

    public  AlbumGridAdapter(Context context,List<String> data,String mDirPath,List<String> mSelectedData){
        this.context = context;

        this.data = data;

        this.mDirPath = mDirPath;

        this.mSelectedData = mSelectedData;

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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = layoutInflater .inflate(R.layout.item_gridview_album_layout,parent,false);

            viewHolder.albumLayout = (RelativeLayout) convertView.findViewById(R.id.album_gridview_item_layout);

            viewHolder.picture = (ImageView) convertView.findViewById(R.id.album_gridview_item_img);

            viewHolder.selectedSign = (TextView) convertView.findViewById(R.id.album_gridview_item_number_text);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String path = mDirPath+"/"+data.get(position);

        String url = ImageDownloader.Scheme.FILE.wrap(path);

        imageLoader.displayImage(url, viewHolder.picture, options);

        if (mSelectedData.contains(path)){ //选择的图片包含这个
            viewHolder.albumLayout.setBackgroundResource(R.drawable.gridview_item_shape);
            viewHolder.selectedSign.setVisibility(View.VISIBLE);
            viewHolder.selectedSign.setText((mSelectedData.indexOf(path)+1)+"");
        } else {
            viewHolder.albumLayout.setBackgroundResource(R.drawable.gridview_item_normal_shape);
            viewHolder.selectedSign.setVisibility(View.GONE);
        }

        return convertView;
    }

    public class ViewHolder {

        private RelativeLayout albumLayout;

        private ImageView picture;

        private TextView selectedSign;

    }
}
