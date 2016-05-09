package com.yang.rungang.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.yang.rungang.R;
import com.yang.rungang.db.DBManager;
import com.yang.rungang.model.bean.IBmobCallback;
import com.yang.rungang.model.bean.RunRecord;
import com.yang.rungang.model.biz.ActivityManager;
import com.yang.rungang.utils.BmobUtil;
import com.yang.rungang.utils.GeneralUtil;
import com.yang.rungang.utils.IdentiferUtil;

import java.util.List;


public class RunRecordDetailsActivity extends BaseActivity implements View.OnClickListener {

    private ImageView backImg;
    private ImageView deleteImg;
    private ImageView shareImg;

    private MapView mapView;

    private TextView createtiemText;
    private TextView distanceText;
    private TextView timeText;


    private BaiduMap baiduMap;

    private RunRecord runRecord = null;

    private List<LatLng> points = null; //定位点集合

    private int position; //位置


    /**
     * 图标
     */
    private static BitmapDescriptor realtimeBitmap;

    /**
     * 地图状态更新
     */
    private MapStatusUpdate update = null;

    /**
     * 实时点覆盖物
     */
    private OverlayOptions realtimeOptions = null;

    /**
     * 开始点覆盖物
     */
    private OverlayOptions startOptions = null;

    /**
     * 结束点覆盖物
     */
    private OverlayOptions endOptions = null;

    /**
     * 路径覆盖物
     */
    private PolylineOptions polyLine = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(context);
        setContentView(R.layout.activity_run_record_details);
        ActivityManager.getInstance().pushOneActivity(this);
        initComponent();

        baiduMap = mapView.getMap();

        position = getIntent().getIntExtra("position", 0);
        //获取到目标记录
        runRecord = DBManager.getInstance(context).getRunRecords().get(position);

        //获取到记录坐标集合
        points = runRecord.getPoints();

        if(points!=null && points.size()>0) {
            //绘制轨迹
            drawTrack();

            setData();
        }


    }

    /**
     * 初始化
     */
    private void initComponent() {
        backImg = (ImageView) findViewById(R.id.record_details_back_img);
        deleteImg = (ImageView) findViewById(R.id.record_details_delete_img);
        shareImg = (ImageView) findViewById(R.id.record_details_share_img);

        mapView = (MapView) findViewById(R.id.record_details_mapview);
        mapView.showZoomControls(false);

        createtiemText = (TextView) findViewById(R.id.record_details_create_time_text);
        distanceText = (TextView) findViewById(R.id.record_details_distance_text);
        timeText = (TextView) findViewById(R.id.record_details_time_text);

        backImg.setOnClickListener(this);
        deleteImg.setOnClickListener(this);
        shareImg.setOnClickListener(this);
    }

    /**
     * 绘制轨迹
     */
    private void drawTrack(){
        baiduMap.clear();

        LatLng  startLatLng = points.get(0);
        LatLng  endLatLng = points.get(points.size() - 1);

        //地理范围
        LatLngBounds bounds = new LatLngBounds.Builder().include(startLatLng).include(endLatLng).build();

        update = MapStatusUpdateFactory.newLatLngBounds(bounds);

        if (points.size()>=2) {

            // 开始点
            BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromResource(R.drawable.startpoint);
            startOptions = new MarkerOptions().position(startLatLng).
                    icon(startBitmap).zIndex(9).draggable(true);

            // 终点
            BitmapDescriptor endBitmap = BitmapDescriptorFactory.fromResource(R.drawable.endpoint);
            endOptions = new MarkerOptions().position(endLatLng)
                    .icon(endBitmap).zIndex(9).draggable(true);

            polyLine = new PolylineOptions().width(10).color(Color.GREEN).points(points);
        }else {
            //实时点
            realtimeBitmap = BitmapDescriptorFactory.fromResource(R.drawable.point);
            realtimeOptions = new MarkerOptions().position(startLatLng).icon(realtimeBitmap)
                    .zIndex(9).draggable(true);

        }

        addMark();

    }

    /**
     * 添加覆盖物
     */
    private void addMark(){

        if (null != update) {
            baiduMap.setMapStatus(update);
        }
        //开始点覆盖物
        if (null != startOptions ) {
            baiduMap.addOverlay(startOptions);
        }
        // 路线覆盖物
        if (null != polyLine) {
            baiduMap.addOverlay(polyLine);
        }
        // 实时点覆盖物
        if (null != realtimeOptions) {
            baiduMap.addOverlay(realtimeOptions);
        }
        //结束点覆盖物
        if (null != endOptions ) {
            baiduMap.addOverlay(endOptions);
        }
    }

    private void setData(){
        createtiemText.setText(runRecord.getCreateTime());
        distanceText.setText(GeneralUtil.doubleToString(runRecord.getDistance()));
        timeText.setText(GeneralUtil.secondsToString(runRecord.getTime()));
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.record_details_back_img:
                this.finish();
                break;
            case R.id.record_details_delete_img: //删除

                new AlertDialog.Builder(RunRecordDetailsActivity.this)
                        .setMessage("确定删除这条记录吗？")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteRecord();
                            }
                        }).create().show();

                break;
            case R.id.record_details_share_img: //分享

                break;
        }
    }

    /**
     * 删除记录
     */
    private void deleteRecord() {


        boolean isSync = false;

        isSync = runRecord.isSync();

        if(GeneralUtil.isNetworkAvailable(context)) { //有网络
            if (isSync) { //已同步

                //从服务器端删除

                BmobUtil.deleteDataFromRunRecord(context, runRecord.getObjectId(), new IBmobCallback() {
                    @Override
                    public void onFinish(int identifier, Object object) {
                        Message msg = new Message();
                        msg.what = identifier;
                        handler.sendMessage(msg);
                    }

                    @Override
                    public void onFailure(int identifier) {

                    }
                });

            }
            DBManager.getInstance(context).deleteOneRunRecord(position);

        } else { //无网络
            if(isSync) {
                Toast.makeText(context,"数据已同步，请联网后删除",Toast.LENGTH_SHORT).show();
            } else {
                DBManager.getInstance(context).deleteOneRunRecord(position);
            }
        }

        RunRecordDetailsActivity.this.finish();

    }

    private void share(){

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IdentiferUtil.DELETE_RUN_RECORD_SUCCESS : //删除成功

                    Toast.makeText(context,"删除成功！",Toast.LENGTH_SHORT).show();
                    break;
                case IdentiferUtil.DELETE_RUN_RECORD_FAILURE: //删除失败

                    break;
            }
            super.handleMessage(msg);
        }
    };

}
