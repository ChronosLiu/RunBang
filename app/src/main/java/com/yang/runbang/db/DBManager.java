package com.yang.runbang.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.yang.runbang.model.bean.WeatherCity;
import com.yang.runbang.model.bean.OLCity;
import com.yang.runbang.model.bean.RunRecord;
import com.yang.runbang.utils.JsonUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

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

    private List<RunRecord> runRecords = null; //runrecord表集合

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

    public List<RunRecord> getRunRecords() {
        if (runRecords == null) {

            runRecords = new ArrayList<>();
            //从数据库中获取
            runRecords = getAllRunRecord();

        }
        return runRecords;
    }

    /**
     * 插入城市数据
     */
    public void insertCitys(List<WeatherCity> cities){
        db = dbHelper.getWritableDatabase();
        //开启事务
        db.beginTransaction();

        try {
            for (WeatherCity city : cities) {
                ContentValues values = new ContentValues();
                values.put("id", city.getId());
                values.put("city", city.getCity());
                values.put("prov", city.getProv());
                values.put("cnty", city.getCnty());
                values.put("lat", city.getLat());
                values.put("lon", city.getLon());
                db.insert("weathercity", null, values);
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
        String sql = "select id from weathercity where city like '"+name+"%'";

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
            values.put("objectid",runRecord.getObjectId());
            values.put("recordid",runRecord.getRecordid());
            values.put("userid", runRecord.getUserId());
            values.put("time", runRecord.getTime());
            values.put("distance", runRecord.getDistance());
            values.put("mapshotpath", runRecord.getMapShotPath());
            values.put("points", JsonUtil.listTojson(runRecord.getPoints())); //转化为json字符串存入数据库
            values.put("speeds", JsonUtil.listTojson(runRecord.getSpeeds())); //转化为Json字符串存入数据库
            values.put("issync",runRecord.isSync());
            values.put("createtime", runRecord.getCreateTime());
            db.insert("runrecord", null, values);
            values.clear();
            runRecords.add(runRecord);
            db.setTransactionSuccessful();
            Log.i("TAG","成功插入数据库");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    /**
     * 从数据库中获取所有的跑步记录
     * @return
     */
    private List<RunRecord> getAllRunRecord(){

        List<RunRecord> points =  new ArrayList<>();

        String sql = "select * from runrecord ";
        db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {
            Cursor cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                do {
                    RunRecord record = new RunRecord();

                    record.setObjectId(cursor.getString(cursor.getColumnIndex("objectid")));
                    record.setRecordid(cursor.getString(cursor.getColumnIndex("recordid")));
                    record.setUserId(cursor.getString(cursor.getColumnIndex("userid")));
                    record.setTime(cursor.getInt(cursor.getColumnIndex("time")));
                    record.setDistance(cursor.getDouble(cursor.getColumnIndex("distance")));
                    record.setMapShotPath(cursor.getString(cursor.getColumnIndex("mapshotpath")));
                    record.setPoints(JsonUtil.jsonToListPoint(
                            cursor.getString(cursor.getColumnIndex("points"))));
                    record.setSpeeds(JsonUtil.jsonToListSpeed(
                            cursor.getString(cursor.getColumnIndex("speeds"))));
                    int issync = cursor.getInt(cursor.getColumnIndex("issync"));
                    if (issync == 1) {
                        record.setIsSync(true);
                    } else {
                        record.setIsSync(false);
                    }
                    record.setCreateTime(cursor.getString(cursor.getColumnIndex("createtime")));
                    points.add(record);
                } while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return points;

    }


    /**
     * 同步数据时，更新数据库信息
     * @param recordid
     * @param objectid
     * @param isSync
     */
    public void updateOneRunRecord(String recordid,String objectid,boolean isSync){

        db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {

            ContentValues values  = new ContentValues();
            values.put("objectid",objectid);
            values.put("issync",isSync);

            db.update("runrecord",values,"recordid = ?",new String[]{recordid});

            db.setTransactionSuccessful();

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 删除一条跑步记录
     * @param position
     */
    public void deleteOneRunRecord(int position){

        String recordid = runRecords.get(position).getRecordid();

        db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        try {

            String sql = "delete from runrecord where recordid ="+recordid;

            db.execSQL(sql);

            runRecords.remove(position);

            Log.i("TAG", "删除成功");
            db.setTransactionSuccessful();

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }



    }

    /**
     * 增加离线城市列表
     * @param olCities
     */
    public void insertOffline(List<OLCity> olCities) {
        if (olCities == null) {
            return;
        }
        db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {

            for(OLCity olCity:olCities) {

                ContentValues values = new ContentValues();
                values.put("cityid", olCity.getCityID());
                values.put("cityname", olCity.getCityName());
                values.put("citytype", olCity.getCityType());
                values.put("size", olCity.getSize());
                values.put("status", olCity.getStatus());
                values.put("ratio", olCity.getRatio());
                values.put("isupdate", olCity.isUpdate());
                values.put("childcities", JsonUtil.listTojson(olCity.getChildCities()));
                db.insert("offlinecity", null, values);

            }
            db.setTransactionSuccessful();
            Log.i("TAG", "插入成功");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    /**
     * 获取全部的离线城市
     * @return
     */
    public List<OLCity> getAllOfflineCity(){
        List<OLCity> olCityList = new ArrayList<>();

        db = dbHelper.getReadableDatabase();

        db.beginTransaction();

        try {
            String sql = "select * from offlinecity";

            Cursor cursor = db.rawQuery(sql,null);

            while(cursor.moveToNext()) {

                OLCity olCity = new OLCity();

                olCity.setCityID(cursor.getInt(cursor.getColumnIndex("cityid")));
                olCity.setCityName(cursor.getString(cursor.getColumnIndex("cityname")));
                olCity.setCityType(cursor.getInt(cursor.getColumnIndex("citytype")));
                olCity.setSize(cursor.getInt(cursor.getColumnIndex("size")));
                olCity.setStatus(cursor.getInt(cursor.getColumnIndex("status")));
                olCity.setRatio(cursor.getInt(cursor.getColumnIndex("ratio")));
                olCity.setChildCities(JsonUtil.jsonToListOfflineCity(
                        cursor.getString(cursor.getColumnIndex("childcities"))));

                int isupdate = cursor.getInt(cursor.getColumnIndex("isupdate"));

                if(isupdate  ==1) {
                    olCity.setUpdate(true);
                } else {
                    olCity.setUpdate(false);
                }
                olCityList.add(olCity);
            }

            db.setTransactionSuccessful();
            Log.i("TAG", "查询成功");
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

        return olCityList;
    }




}
