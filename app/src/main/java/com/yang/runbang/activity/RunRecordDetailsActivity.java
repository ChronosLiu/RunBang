package com.yang.runbang.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

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
import com.yang.runbang.R;
import com.yang.runbang.db.DBManager;
import com.yang.runbang.model.bean.IBmobCallback;
import com.yang.runbang.model.bean.RunRecord;
import com.yang.runbang.model.biz.ActivityManager;
import com.yang.runbang.utils.BmobUtil;
import com.yang.runbang.utils.FileUtil;
import com.yang.runbang.utils.GeneralUtil;
import com.yang.runbang.utils.IdentiferUtil;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;


public class RunRecordDetailsActivity extends BaseActivity implements View.OnClickListener, Toolbar.OnMenuItemClickListener {


    private MapView mapView;

    private TextView createtiemText;
    private TextView distanceText;
    private TextView timeText;


    private BaiduMap baiduMap;

    private RunRecord runRecord = null;

    private List<LatLng> points = null; //定位点集合

    private int position; //位置

    private String snapShotPath = null;


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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_run_record_details);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setTitle("详情");
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(this);

        mapView = (MapView) findViewById(R.id.record_details_mapview);
        mapView.showZoomControls(false);

        createtiemText = (TextView) findViewById(R.id.record_details_create_time_text);
        distanceText = (TextView) findViewById(R.id.record_details_distance_text);
        timeText = (TextView) findViewById(R.id.record_details_time_text);

    }

    /**
     * 绘制轨迹
     */
    private void drawTrack(){
        baiduMap.clear();

        LatLng  startLatLng = points.get(0);
        LatLng  endLatLng = points.get(points.size() - 1);

        //地理范围
//        LatLngBounds bounds = new LatLngBounds.Builder().include(startLatLng).include(endLatLng).build();

        MapStatus mapStatus = new MapStatus.Builder().target(startLatLng).zoom(17).build();

        update = MapStatusUpdateFactory.newMapStatus(mapStatus);

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

    /**
     * 地图截屏
     */

    private void mapScreenShot(){

        baiduMap.snapshot(new BaiduMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {

                //将bitmap存储到文件中
                Log.i("TAG", "截图成功");
                snapShotPath = FileUtil.saveBitmapToFile(bitmap, "mapshot");

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_run_record_details,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_delete_record://删除记录
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
            case R.id.item_share_record://分享记录
                mapScreenShot();//截图

                ShareSDK.initSDK(context);
                OnekeyShare oks= new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();
                // title标题：微信、QQ（新浪微博不需要标题）
                oks.setTitle("今天，你跑了吗？");  //最多30个字符
                // text是分享文本：所有平台都需要这个字段
                oks.setText(runRecord.getCreateTime()+"我跑了"+GeneralUtil.distanceToKm(runRecord.getDistance())+"千米"
                +",花费了"+GeneralUtil.secondsToHourString(runRecord.getTime())+"小时");  //最多40个字符

                // imagePath是图片的本地路径：除Linked-In以外的平台都支持此参数
                if (snapShotPath!=null) {
                    oks.setImagePath(snapShotPath);//确保SDcard下面存在此张图片
                }

//                oks.setImageUrl(dynamic1.getImage().get(0));//网络图片rul

//                // url：仅在微信（包括好友和朋友圈）中使用
                oks.setUrl("http://runbang.bmob.cn");   //网友点进链接后，可以看到分享的详情
//                // Url：仅在QQ空间使用
                oks.setTitleUrl("http://runbang.bmob.cn");  //网友点进链接后，可以看到分享的详情
                // 启动分享GUI
                oks.show(context);


                break;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (snapShotPath!=null) {
            FileUtil.deleteBitmapByPath(snapShotPath);
        }
    }
}
