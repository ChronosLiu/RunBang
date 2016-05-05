package com.yang.rungang.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库帮助类
 *
 * Created by 洋 on 2016/5/4.
 */
public class MySQLiteOpenHelper extends SQLiteOpenHelper {

    private Context context;

    /**
     * 建表语句
     */
    public  static final String CREATE_TABLE_CITY = "create table city (" +
            "id text," +
            "city text," +
            "prov text," +
            "cnty text," +
            "lat text," +
            "lon text)";

    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建表
        db.execSQL(CREATE_TABLE_CITY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists city");

        onCreate(db);
    }
}