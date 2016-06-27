package com.yang.runbang.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

/**
 * 轨迹监听service
 */
public class MonitorService extends Service {

    protected static boolean isCheck = false;

    protected static boolean isRunning = false;

    private static final String SERVICE_NAME = "com.baidu.trace.LBSTraceService";

    public MonitorService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                while (isCheck) {
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        System.out.println("thread sleep failed");
                    }

                    // 轨迹服务停止，重启轨迹服务
                    if (!isServiceWork(getApplicationContext(), SERVICE_NAME)) {


//                        if (null != MainActivity.client && null != MainActivity.trace) {
//                            MainActivity.client.startTrace(MainActivity.trace);
//                        } else {
//                            MainActivity.client = null;
//                            MainActivity.client = new LBSTraceClient(getApplicationContext());
//                            MainActivity.entityName = MainActivity.getImei(getApplicationContext());
//                            MainActivity.trace = new Trace(getApplicationContext(), MainActivity.serviceId,
//                                    MainActivity.entityName);
//                            MainActivity.client.startTrace(MainActivity.trace);
//                        }


                    } else {
                        System.out.println("轨迹服务正在运行");
                    }

                }
            }

        }.start();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 判断某个服务是否正在运行的方法
     *
     * @param mContext
     * @param serviceName 是包名+服务的类名（例如：com.baidu.trace.LBSTraceService）
     * @return true代表正在运行，false代表服务没有正在运行
     */
    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(80);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            System.out.println("serviceName : " + mName);
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

}
