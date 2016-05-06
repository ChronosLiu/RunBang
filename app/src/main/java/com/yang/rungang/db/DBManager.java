package com.yang.rungang.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yang.rungang.model.bean.City;
import com.yang.rungang.model.bean.RunRecord;
import com.yang.rungang.utils.JsonUtil;

import java.util.List;

/**
 * 数据库管理类，单例类
 *
 * Created by 洋 on 2016/5/4.
 */
public class DBManager {

    private Context context;

    private static DBManager dbManager;

    private static MySQLiteOpenHelper dbHelper;

    private SQLiteDatabase db;

    private DBManager(Context context) {
        this.context = context;
    }

    public static DBManager getInstance(Context context){

        if(dbManager == null) {
            dbManager = new DBManager(context);
            dbHelper = new MySQLiteOpenHelper(context,"RunGang.db",null,1);

        }

        return dbManager;
    }


    /**
     * 插入城市数据
     */
    public void insertCitys(List<City> cities){
        db = dbHelper.getWritableDatabase();
        //开启事务
        db.beginTransaction();

        try {
            for (City city : cities) {
                ContentValues values = new ContentValues();
                values.put("id", city.getId());
                values.put("city", city.getCity());
                values.put("prov", city.getProv());
                values.put("cnty", city.getCnty());
                values.put("lat", city.getLat());
                values.put("lon", city.getLon());
                db.insert("city", null, values);
                Log.i("TAG",city.getCity());
            }
            db.setTransactionSuccessful(); //事务执行成功
            Log.i("TAG","成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction(); //结束事务

        }
    }

    /**
     * 通过城市名称查询城市id
     * @param name
     * @return
     */
    public String queryIdByName(String name) {
        db = dbHelper.getReadableDatabase();
        String id = null;

        // 模糊查询
        String sql = "select id from city where city like '"+name+"%'";

        Cursor cursor = db.rawQuery(sql,new String[]{});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex("id"));
            }
            cursor.close();
        }

        return id;
    }

    /**
     * 增加跑步记录
     * @param runRecord
     */
    public void insertRunRecord(RunRecord runRecord) {
        if (runRecord == null) {
            return;
        }
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put("userid", runRecord.getUserId());
            values.put("time", runRecord.getTime());
            values.put("distance", runRecord.getDistance());
            values.put("mapshotpath", runRecord.getMapShotPath());
            values.put("points", JsonUtil.listTojson(runRecord.getPoints())); //转化为json字符串存入数据库
            values.put("speeds", JsonUtil.listTojson(runRecord.getSpeeds())); //转化为Json字符串存入数据库
            values.put("createtime", runRecord.getCreateTime());
            db.insert("runrecord", null, values);
            values.clear();
            db.setTransactionSuccessful();
            Log.i("TAG","成功插入数据库");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }


}
