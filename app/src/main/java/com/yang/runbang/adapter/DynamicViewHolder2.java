package com.yang.runbang.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.yang.runbang.R;
import com.yang.runbang.listener.OnRecyclerViewListener;
import com.yang.runbang.model.bean.Dynamic;
import com.yang.runbang.utils.GeneralUtil;

/**
 * Created by 洋 on 2016/6/11.
 */
public class DynamicViewHolder2 extends BaseViewHolder {

    private OnRecyclerViewListener listener;

    private CardView cardView;
    private ImageView avatar;
    private TextView nickName;
    private TextView time;
    private ImageView picture1;
    private ImageView picture2;
    private TextView content;
    private TextView likeNumber;
    public ImageView likeImage;
    private TextView commentNumber;
    private RelativeLayout commentRelative;
    private RelativeLayout likeRelative;
    private RelativeLayout shareRelative;

    private DisplayImageOptions options;
    private DisplayImageOptions circleOptions;

    public DynamicViewHolder2(View itemView, Context context, final OnRecyclerViewListener listener) {
        super(itemView, context, listener);
        this.listener = listener;
        cardView = (CardView) itemView.findViewById(R.id.cardview);
        avatar = (ImageView) itemView.findViewById(R.id.image_avatar_dynamic);
        nickName = (TextView) itemView.findViewById(R.id.text_nickname_dynamic);
        time = (TextView) itemView.findViewById(R.id.text_time_dynamic);
        picture1 = (ImageView) itemView.findViewById(R.id.image_picture_one);
        picture2 = (ImageView) itemView.findViewById(R.id.image_picture_two);

        content = (TextView) itemView.findViewById(R.id.text_content_dynamic);
        likeNumber = (TextView) itemView.findViewById(R.id.text_like_number_dynamic);
        commentNumber = (TextView) itemView.findViewById(R.id.text_comment_number_dynamic);
        commentRelative = (RelativeLayout) itemView.findViewById(R.id.relative_comment_dynamic);
        likeRelative = (RelativeLayout) itemView.findViewById(R.id.relative_like_dynamic);
        shareRelative = (RelativeLayout) itemView.findViewById(R.id.relative_share_dynamic);

        likeImage = (ImageView) itemView.findViewById(R.id.dynamic_like_img);

        this.options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_no_picture)
                .showImageOnFail(R.drawable.default_no_picture)
                .showImageForEmptyUri(R.drawable.default_no_picture)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        this.circleOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_avatar_blue)
                .showImageOnFail(R.drawable.default_avatar_blue)
                .showImageForEmptyUri(R.drawable.default_avatar_blue)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new CircleBitmapDisplayer())
                .build();

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChildClick(getAdapterPosition(),R.id.image_avatar_dynamic);
            }
        });
        shareRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChildClick(getAdapterPosition(),R.id.relative_share_dynamic);

            }
        });
        likeRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChildClick(getAdapterPosition(),R.id.relative_like_dynamic);
            }
        });
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    @Override
    public void bindData(Object o) {

        Dynamic dynamic = (Dynamic)o;

        if (dynamic.getFromUser().getHeadImgUrl()==null) {

            this.avatar.setImageResource(R.drawable.default_avatar_blue);
        } else {
            ImageLoader.getInstance().displayImage(dynamic.getFromUser().getHeadImgUrl(),this.avatar,circleOptions);
        }

        this.nickName.setText(dynamic.getFromUser().getNickName());

        this.time.setText(GeneralUtil.computeTime(dynamic.getCreatedAt()));

        ImageLoader.getInstance().displayImage(dynamic.getImage().get(0),this.picture1,options);

        ImageLoader.getInstance().displayImage(dynamic.getImage().get(1),this.picture2,options);


        if (dynamic.getContent()!=null) {
            this.content.setText(dynamic.getContent());
        }

        this.commentNumber.setText(dynamic.getCommentCount().toString());
        this.likeNumber.setText(dynamic.getLikesCount().toString());
        if (likes.contains(dynamic)) {
            this.likeImage.setImageResource(R.drawable.aleadylike);
        } else {
            this.likeImage.setImageResource(R.drawable.like);
        }

    }

    @Override
    public void setAnimation() {
        Animation animation = AnimationUtils.loadAnimation(cardView.getContext(), R.anim.item_bottom_in);

        cardView.startAnimation(animation);
    }
}
