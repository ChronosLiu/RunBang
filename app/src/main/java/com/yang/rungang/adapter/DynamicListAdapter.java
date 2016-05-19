package com.yang.rungang.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.yang.rungang.R;
import com.yang.rungang.activity.UserProfileActivity;
import com.yang.rungang.model.bean.Dynamic;
import com.yang.rungang.utils.GeneralUtil;

import java.util.List;

/**
 * Created by 洋 on 2016/5/10.
 */
public class DynamicListAdapter  extends BaseAdapter{

    private Context context;

    private ImageLoader imageLoader;

    private DisplayImageOptions options;

    private DisplayImageOptions circleOptions;

    private List<Dynamic> data;

    private LayoutInflater layoutInflater;

    public DynamicListAdapter(Context context,List<Dynamic> data) {
        this.context = context;

        this.data = data;

        this.layoutInflater = LayoutInflater.from(context);

        this.imageLoader = ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.upload_head_pic)
                .showImageOnFail(R.drawable.upload_head_pic)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
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

            convertView = layoutInflater.inflate(R.layout.item_listview_dynamic_layout,parent,false);

            viewHolder.itemLayout = (LinearLayout) convertView.findViewById(R.id.dynamic_item_layout);

            viewHolder.headimg = (ImageView) convertView.findViewById(R.id.dynamic_head_img);

            viewHolder.name = (TextView) convertView.findViewById(R.id.dynamic_name_text);

            viewHolder.time = (TextView) convertView.findViewById(R.id.dynamic_time_text);

            viewHolder.pictureLayout = (RelativeLayout) convertView.findViewById(R.id.dynamic_picture_layout);

            viewHolder.firseImg = (ImageView) convertView.findViewById(R.id.dynamic_picture_img);

            viewHolder.pictureCountLayout = (LinearLayout) convertView.findViewById(R.id.dynamic_picture_count_layout);

            viewHolder.pictureCount = (TextView) convertView.findViewById(R.id.dynamic_picture_count);

            viewHolder.content = (TextView) convertView.findViewById(R.id.dynamic_content_text);

            viewHolder.theme = (TextView) convertView.findViewById(R.id.dynamic_theme_text);

            viewHolder.commentLayout = (RelativeLayout) convertView.findViewById(R.id.dynamic_comment_layout);

            viewHolder.commentCount = (TextView) convertView.findViewById(R.id.dynamic_comment_count);

            viewHolder.likeLayout = (RelativeLayout) convertView.findViewById(R.id.dynamic_like_layout);

            viewHolder.likeCount = (TextView) convertView.findViewById(R.id.dynamic_like_count);

            viewHolder.shareLayout = (RelativeLayout) convertView.findViewById(R.id.dynamic_share_layout);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final Dynamic dynamic = data.get(position);

        if (dynamic.getFromUser().getHeadImgUrl()!=null) {
            imageLoader.displayImage(dynamic.getFromUser().getHeadImgUrl(),viewHolder.headimg,circleOptions);
        }

        viewHolder.name.setText(dynamic.getFromUser().getNickName());

        viewHolder.time.setText(GeneralUtil.computeTime(dynamic.getCreatedAt()));

        viewHolder.content.setText(dynamic.getContent());

        viewHolder.theme.setText(dynamic.getTheme());

        viewHolder.commentCount.setText(dynamic.getCommentCount().toString());

        viewHolder.likeCount.setText(dynamic.getLikesCount().toString());

        if (dynamic.getImage()== null || dynamic.getImage().size() < 0) {
            viewHolder.pictureLayout.setVisibility(View.GONE);

        } else {

            viewHolder.pictureLayout.setVisibility(View.VISIBLE);

            if (dynamic.getImage().size()>1) {
                viewHolder.pictureCountLayout.setVisibility(View.VISIBLE);
                viewHolder.pictureCount.setText(dynamic.getImage().size()+"");
            } else {
                viewHolder.pictureCountLayout.setVisibility(View.GONE);
            }
            String firstUrl = dynamic.getImage().get(0);
            imageLoader.displayImage(firstUrl, viewHolder.firseImg, options);

        }




        //头像
        viewHolder.headimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"头像",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("userid",dynamic.getFromUser().getObjectId());
                context.startActivity(intent);
            }
        });
        //分享
        viewHolder.shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context,"分享",Toast.LENGTH_SHORT).show();
            }
        });

        //点赞
        viewHolder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context,"点赞",Toast.LENGTH_SHORT).show();

        }
        });
        return convertView;
    }


    private class ViewHolder {

        private LinearLayout itemLayout;

        private ImageView headimg;

        private TextView name;

        private TextView time;

        private RelativeLayout pictureLayout;

        private ImageView firseImg;

        private LinearLayout pictureCountLayout;

        private TextView pictureCount;

        private TextView content;

        private TextView theme;

        private RelativeLayout commentLayout;

        private TextView commentCount;

        private RelativeLayout likeLayout;

        private TextView likeCount;

        private RelativeLayout shareLayout;

    }

}
