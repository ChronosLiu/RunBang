package com.yang.runbang.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.utils.DistanceUtil;
import com.yang.runbang.R;
import com.yang.runbang.db.DBManager;
import com.yang.runbang.model.bean.RunRecord;
import com.yang.runbang.model.bean.User;
import com.yang.runbang.utils.FileUtil;
import com.yang.runbang.utils.GeneralUtil;
import com.yang.runbang.utils.IdentiferUtil;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;

public class RunActivity extends BaseActivity implements View.OnClickListener {

    private MapView mapView;
    private TextView timeText;
    private TextView distanceText;

    private ImageView startOrPauseImg;
    private ImageView continueImg;
    private ImageView stopImg;
    private RelativeLayout startRelative;
    private LinearLayout pauseLinear;
    private TextView stateText;

    private TextView dialogMessage;
    private TextView dialogContinue;
    private TextView dialogEnd;

    private Dialog dialog; // 弹窗提示

    private User user;

    private RunRecord runRecord = null ; // 跑步记录

    private String picPath = null ; // 截屏路径

    private boolean isStart = false; // 标示 是否开始运动，默认false，未开始

    private double distance = 0.0; // 跑步总距离
    private int time = 0; //跑步用时，单位秒
    private Timer timer = null;
    private TimerTask timerTask = null;

    /**
     * 定位客户端
     */
    public LocationClient mLocationClient = null;
    /**
     * 定位监听器
     */
    private BDLocationListener locationListener = new MyLocationListener();

    private List<LatLng> pointList = new ArrayList<>(); //坐标点集合

    private List<Float> speedList = new ArrayList<>(); // 速度集合

    private BaiduMap baiduMap;  //地图对象

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

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case IdentiferUtil.TIME_TASK:

                    timeText.setText(GeneralUtil.secondsToString(msg.arg1));
                    break;

                case IdentiferUtil.SAVE_DATA_TO_BMOB_SUCCESS:

                    runRecord.setIsSync(true);

                    DBManager.getInstance(context).insertRunRecord(runRecord);

                    Log.i("TAG", "objecid" + runRecord.getObjectId());

                    break;

                case IdentiferUtil.SAVE_DATA_TO_BMOB_FAILURE:
                    runRecord.setIsSync(false);
                    DBManager.getInstance(context).insertRunRecord(runRecord);
                    break;

            }

            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        SDKInitializer.initialize(context);
        setContentView(R.layout.activity_run);
        initToolBar();

        user = BmobUser.getCurrentUser(context,User.class);

        initComponent();
        baiduMap = mapView.getMap();

        mLocationClient = new LocationClient(context);
        mLocationClient.registerLocationListener(locationListener);
        initLocation();

    }

    private void initToolBar(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_run);
        toolbar.setTitle("跑步");
        toolbar.setNavigationIcon(R.drawable.back);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map_set:
                        Intent mapset = new Intent(RunActivity.this,OLMapActivity.class);
                        startActivity(mapset);
                        break;
                }
                return true;
            }
        });

    }

    /**
     * 初始化组件
     */
    private void initComponent() {


        mapView = (MapView) findViewById(R.id.run_mapview);

        timeText = (TextView) findViewById(R.id.run_time_text);
        distanceText = (TextView) findViewById(R.id.run_distance_text);

        stateText = (TextView) findViewById(R.id.run_state_text);
        startRelative = (RelativeLayout) findViewById(R.id.run_init_run_relative);
        pauseLinear = (LinearLayout) findViewById(R.id.run_pause_run_linear);

        continueImg = (ImageView) findViewById(R.id.run_continue_img);
        startOrPauseImg = (ImageView) findViewById(R.id.run_start_or_pause_img);
        stopImg = (ImageView) findViewById(R.id.run_stop_img);

        continueImg.setOnClickListener(this);
        startOrPauseImg.setOnClickListener(this);
        stopImg.setOnClickListener(this);
        mapView.setOnClickListener(this);

    }

    /**
     * 绘制轨迹
     * @param latLng
     */
    private void drawTrace(LatLng latLng){

        Log.i("TAG", "绘制实时点");

        baiduMap.clear(); //清除覆盖物

        MapStatus mapStatus = new MapStatus.Builder().target(latLng).zoom(17).build();

        update = MapStatusUpdateFactory.newMapStatus(mapStatus);

        //实时点
        realtimeBitmap = BitmapDescriptorFactory.fromResource(R.drawable.point);

        if(isStart) {
            realtimeOptions = new MarkerOptions().position(latLng).icon(realtimeBitmap)
                    .zIndex(9).draggable(true);
        }
        // 开始点
        BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromResource(R.drawable.startpoint);

        if (pointList.size()>1) {
            startOptions = new MarkerOptions().position(pointList.get(0)).
                    icon(startBitmap).zIndex(9).draggable(true);
        }

        // 路线
        if (pointList.size()>=2) {

            polyLine = new PolylineOptions().width(10).color(Color.GREEN).points(pointList);
        }
        addMarker();
    }

    /**
     * 添加地图覆盖物
     */
    private void addMarker() {

        Log.i("TAG", "添加覆盖物");
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


    /**
     * 初始化定位，设置定位参数
     */
    private void initLocation(){
        //用来设置定位sdk的定位方式
        LocationClientOption option = new LocationClientOption();

        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=5000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(false);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要

        mLocationClient.setLocOption(option);

    }

    /**
     * 定位监听器
     */
    private class MyLocationListener implements  BDLocationListener{

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation ||
                        bdLocation.getLocType() == BDLocation.TypeNetWorkLocation ){ //gps,网络定位成功定位

                    double latitude = bdLocation.getLatitude(); //纬度
                    double longitude = bdLocation.getLongitude(); // 经度
                    double radius = bdLocation.getRadius(); //精度
                    float speed = 0f;
                    if(bdLocation.hasSpeed()){
                        speed = bdLocation.getSpeed();
                        speedList.add(speed);

                        Log.i("TAG","速度"+speed);
                    }
                    LatLng latLng = new LatLng(latitude,longitude); //坐标点

                    if (Math.abs(latitude - 0.0) < 0.000001 && Math.abs(longitude - 0.0) < 0.000001) {

                    } else {
                        if (pointList.size()<1) { //初次定位

                            pointList.add(latLng);

                        }else {
                            LatLng lastPoint = pointList.get(pointList.size()-1);//上一次定位坐标点
                            double rang = DistanceUtil.getDistance(lastPoint,latLng); // 两次定位的距离
                            if(rang>10 ) {
                                distance = distance + rang;
                                pointList.add(latLng);
                            }
                        }
                        distanceText.setText(GeneralUtil.doubleToString(distance));

                    }
                    drawTrace(latLng);

                }  else if(bdLocation.getLocType() == BDLocation.TypeServerError ) { //服务器错误
                    Toast.makeText(context,"服务器错误，请稍后重试",Toast.LENGTH_SHORT).show();
                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException ) {
                    Toast.makeText(context,"网络错误，请连接网络",Toast.LENGTH_SHORT).show();
                } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException ) {
                    Toast.makeText(context,"定位错误，请设置手机模式",Toast.LENGTH_SHORT).show();
                }
        }

    }

    /**
     *绘制最终完成地图
     *
     */
    private void drawFinishMap(){
        baiduMap.clear();

        LatLng  startLatLng = pointList.get(0);
        LatLng  endLatLng = pointList.get(pointList.size() - 1);

        //地理范围
        LatLngBounds bounds = new LatLngBounds.Builder().include(startLatLng).include(endLatLng).build();

        update = MapStatusUpdateFactory.newLatLngBounds(bounds);

        if (pointList.size()>=2) {

            // 开始点
            BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromResource(R.drawable.startpoint);
            startOptions = new MarkerOptions().position(startLatLng).
                    icon(startBitmap).zIndex(9).draggable(true);

            // 终点
            BitmapDescriptor endBitmap = BitmapDescriptorFactory.fromResource(R.drawable.endpoint);
            endOptions = new MarkerOptions().position(endLatLng)
                    .icon(endBitmap).zIndex(9).draggable(true);

            polyLine = new PolylineOptions().width(10).color(Color.GREEN).points(pointList);
        }else {
            //实时点
            realtimeBitmap = BitmapDescriptorFactory.fromResource(R.drawable.point);
            realtimeOptions = new MarkerOptions().position(startLatLng).icon(realtimeBitmap)
                        .zIndex(9).draggable(true);

        }

        addMarker();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

        mLocationClient.stop();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 开始计时
     */
    private void startTimer(){
        if(timer == null) {
            timer = new Timer();
        }
        if(timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {

                    time++;
                    Message msg = new Message();
                    msg.what = IdentiferUtil.TIME_TASK;
                    msg.arg1 = time;
                    handler.sendMessage(msg);
                }
            };
        }

        if(timer != null && timerTask != null) {
            timer.schedule(timerTask,1000,1000);
        }

    }

    /**
     * 结束计时
     */
    private void stopTimer(){

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.run_mapview:

                break;

            case R.id.run_start_or_pause_img:
                if (isStart) {//已开始，暂停按钮

                    stopTimer(); //停止计时
                    startRelative.setVisibility(View.GONE);
                    pauseLinear.setVisibility(View.VISIBLE);
                    mLocationClient.stop();

                } else { //未开始，开始按钮

                    isStart = true ;
                    startTimer();
                    startOrPauseImg.setImageResource(R.drawable.run_stop);
                    stateText.setText("暂停");
                    mLocationClient.start(); // 开始定位
                }
                break;
            case R.id.run_continue_img:// 继续

                startTimer(); // 开始计时

                startRelative.setVisibility(View.VISIBLE);
                pauseLinear.setVisibility(View.GONE);
                break;
            case R.id.run_stop_img: //停止,完成

                mLocationClient.stop();
                stopTimer(); // 停止计时
                showDialog();
                break;

            case R.id.dialog_continue_run :
                dialog.dismiss();
                break;
            case R.id.dialog_end_run :
                //绘制完成轨迹图
                drawFinishMap();
                //截屏
                mapScreenShot();

                break;

        }
    }


    /**
     * 地图截屏
     */

    private void mapScreenShot(){

        baiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                //将bitmap存储到文件中
                Log.i("TAG","截图成功");
                picPath = FileUtil.saveBitmapToFile(bitmap,"mapshot");
                //保存记录
                saveRunRecord();
            }
        });
    }

    /**
     * 保存跑步记录
     */
    private void saveRunRecord(){

        runRecord = new RunRecord();

        String id= new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        Log.i("TAG", "id" + id);
        runRecord.setRecordid(id);
        runRecord.setPoints(pointList);
        runRecord.setDistance(distance);
        runRecord.setTime(time);
        runRecord.setUserId(user.getObjectId());
        runRecord.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        runRecord.setMapShotPath(picPath);
        runRecord.setSpeeds(speedList);

        if (GeneralUtil.isNetworkAvailable(context)) { //网络连接

            runRecord.setIsSync(true);
            //存储到服务器端
            runRecord.save(context, new SaveListener() {
                @Override
                public void onSuccess() {

                    Log.i("TAG", "成功上传到云端");
                    Message msg = new Message();
                    msg.what = IdentiferUtil.SAVE_DATA_TO_BMOB_SUCCESS;
                    handler.sendMessage(msg);
                }

                @Override
                public void onFailure(int i, String s) {

                    Message msg = new Message();
                    msg.what = IdentiferUtil.SAVE_DATA_TO_BMOB_FAILURE;
                    handler.sendMessage(msg);
                }
            });
        } else {
            runRecord.setIsSync(false);
            DBManager.getInstance(context).insertRunRecord(runRecord);
        }

        Log.i("TAG","objecid111"+runRecord.getObjectId());
        RunActivity.this.finish();

    }

    /**
     * 展示dialog
     */
    private void showDialog(){

        View view = getLayoutInflater().inflate(R.layout.dialog_run_finish_layout, null);

        dialog = new AlertDialog.Builder(RunActivity.this).create();

        dialog.show();
        dialog.setContentView(view);

        dialogMessage = (TextView) view.findViewById(R.id.dialog_message_text);
        dialogContinue = (TextView) view.findViewById(R.id.dialog_continue_run);
        dialogEnd = (TextView) view.findViewById( R.id.dialog_end_run);

        dialogContinue.setOnClickListener(this);
        dialogEnd.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_run,menu);
        return true;
    }
}
