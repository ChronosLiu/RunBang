package com.yang.runbang.db;

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
    public  static final String CREATE_TABLE_CITY = "create table weathercity (" +
            "id text," +
            "city text," +
            "prov text," +
            "cnty text," +
            "lat text," +
            "lon text)";

    /**
     * 创建表runrecord
     */
    public static final String CREATE_TABLE_RUNRECORD ="create table runrecord ("+
            "recordid text,"+
            "objectid text,"+
            "userid text,"+
            "time real,"+
            "distance real,"+
            "mapshotpath text,"+
            "points text,"+
            "speeds text,"+
            "issync numeric,"+
            "createtime text)";

    /**
     * 创建表 offlinecity
     */
    public static final String CREATE_TABLE_OFFLINECITY = "create table offlinecity ("+
            "cityid integer,"+
            "cityname text,"+
            "citytype integer,"+
            "size integer,"+
            "status integer,"+
            "ratio integer,"+
            "isupdate numeric,"+
            "childcities text)";


    public MySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //创建城市表
        db.execSQL(CREATE_TABLE_CITY);
        //创建记录表
        db.execSQL(CREATE_TABLE_RUNRECORD);
        //创建离线地图城市表
        db.execSQL(CREATE_TABLE_OFFLINECITY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("drop table if exists city");

        db.execSQL("drop table if exists runrecord");

        db.execSQL("drop table if exists offlinecity");

        onCreate(db);
    }
}
