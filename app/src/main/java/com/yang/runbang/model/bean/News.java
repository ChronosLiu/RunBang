package com.yang.runbang.model.bean;


import android.graphics.Bitmap;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * 资讯
 * Created by 洋 on 2016/5/2.
 */
public class News extends BmobObject {

    private String title;
    private String author;
    private String brief;
    private int readNumber;
    private BmobFile picture;
    private Bitmap bitmap;

    public void setTitle(String title) {
        this.title = title;
    }


    public void setReadNumber(int readNumber) {
        this.readNumber = readNumber;
    }

    public void setBrief(String brief) {
        this.brief = brief;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {

        return title;
    }

    public int getReadNumber() {
        return readNumber;
    }

    public String getBrief() {
        return brief;
    }

    public String getAuthor() {
        return author;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    public BmobFile getPicture() {

        return picture;
    }


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {

        return bitmap;
    }
}
