package com.yang.runbang.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yang.runbang.R;
import com.yang.runbang.listener.OnRecyclerViewListener;

/**
 * Created by 洋 on 2016/6/12.
 */
public class RecyclerFootViewHolder extends BaseViewHolder {
    private TextView loadMore;

    public RecyclerFootViewHolder(View itemView, Context context, OnRecyclerViewListener listener) {
        super(itemView, context, listener);
        loadMore = (TextView) itemView.findViewById(R.id.text_load_more);
    }

    @Override
    public void bindData(Object o) {

    }
}
