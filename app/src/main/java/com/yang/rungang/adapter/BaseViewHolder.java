package com.yang.rungang.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yang.rungang.listener.OnRecyclerViewListener;

/**
 * Created by 洋 on 2016/5/22.
 */
public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

    private Context context;
    private OnRecyclerViewListener listener;

    public BaseViewHolder(View itemView,Context context,OnRecyclerViewListener listener) {
        super(itemView);
        this.context = context;
        this.listener = listener;

        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    public Context getContext() {
        return itemView.getContext();
    }

    public abstract void bindData(T t);

    @Override
    public void onClick(View v) {
        if (listener!= null) {
            listener.onItemClick(getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (listener != null) {
            listener.onItemLongClick(getAdapterPosition());
        }
        return true;
    }
}
