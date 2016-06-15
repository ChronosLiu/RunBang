package com.yang.runbang.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 *recyclerview的分割线
 *
 * Created by 洋 on 2016/6/13.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {


    /**
     * RecyclerView的布局方向，默认先赋值为纵向布局
     * RecyclerView 布局可横向，也可纵向
     * 横向和纵向对应的分割想画法不一样
     * */
    private int mOrientation = LinearLayoutManager.VERTICAL;
    /**
     * item之间分割线的size，默认为1
     */
    private int mDividerSize =1;

    /**
     * 画笔
     */
    private Paint mPaint;


    private Drawable mDivider;



    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    /**
     * 构造方法
     * @param context 上下文
     * @param orientation 布局方向
     */
    public DividerItemDecoration(Context context,int orientation) {
        this.mOrientation = orientation;
        if(orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL){
            throw new IllegalArgumentException("请传入正确的参数") ;
        }

        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    /**
     * 自定义分割线，
     * @param context 上下文
     * @param orientation 布局方向
     * @param dividerSize 大小
     */
    public DividerItemDecoration(Context context,int orientation,int dividerSize){
        this(context,orientation);
        this.mDividerSize = dividerSize;
    }

    /**
     * 自定义分割线，
     * @param context 上下文
     * @param orientation 布局方向
     * @param dividerSize 大小
     * @param dividerColor 颜色
     */
    public DividerItemDecoration(Context context,int orientation,int dividerSize,int dividerColor){
        this(context,orientation);
        this.mDividerSize = dividerSize;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == LinearLayoutManager.VERTICAL){
            drawVerticalDivider(c, parent);
        }else {
            drawHorizontalDivider(c, parent);
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0,0,0,mDividerSize);
        } else {
            outRect.set(0,0,mDividerSize,0);
        }
    }

    /**
     * 绘制纵向 item 分割线
     * @param canvas
     * @param parent
     */
    private void drawVerticalDivider(Canvas canvas,RecyclerView parent){

        final int left = parent.getPaddingLeft() ;
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight() ;
        final int childSize = parent.getChildCount() ;

        for(int i = 0 ; i < childSize ; i ++){
            final View child = parent.getChildAt( i ) ;
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin ;
            final int bottom = top + mDividerSize ;

            if (mPaint!=null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
            if (mDivider!=null) {
                mDivider.setBounds(left,top,right,bottom);
                mDivider.draw(canvas);
            }
        }
    }

    /**
     * 绘制横向 item 分割线
     * @param canvas
     * @param parent
     */
    private void drawHorizontalDivider(Canvas canvas, RecyclerView parent){
        final int top = parent.getPaddingTop() ;
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom() ;
        final int childSize = parent.getChildCount() ;
        for(int i = 0 ; i < childSize ; i ++){
            final View child = parent.getChildAt( i ) ;
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin ;
            final int right = left + mDividerSize ;
            if (mPaint!=null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
            if (mDivider!=null) {
                mDivider.setBounds(left,top,right,bottom);
                mDivider.draw(canvas);
            }
        }
    }
}
