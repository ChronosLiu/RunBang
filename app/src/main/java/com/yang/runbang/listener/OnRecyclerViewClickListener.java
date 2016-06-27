package com.yang.runbang.listener;

/**
 * Created by 洋 on 2016/6/8.
 */
public interface OnRecyclerViewClickListener {

    void onItemClick(int position);

    boolean onItemLongClick(int position);

    void onChildClick(int position,int childId);
}
