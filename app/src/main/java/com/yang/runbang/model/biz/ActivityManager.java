package com.yang.runbang.model.biz;

import android.app.Activity;

import java.util.Stack;

/**
 * Activity管理类，单例类
 * 管理所有Activity
 * Created by 洋 on 2016/4/27.
 */
public class ActivityManager {


    private Stack<Activity> activityStack; // activity管理栈

    private static ActivityManager activityManager;

    /**
     * 私有构造函数
     */
    private ActivityManager(){
        activityStack=new Stack<>();
    }

    /**
     * 获取ActivityStack的实例
     * @return
     */
    public static ActivityManager getInstance(){
        if(activityManager == null){
            activityManager =new ActivityManager();
        }
        return activityManager;
    }

    /**
     * 弹出栈顶Activity
     */
    public void popTopActivity(){
        if(activityStack!=null){
            Activity activity=activityStack.peek();
            activity.finish();
            activityStack.remove(activity);
        }
    }

    /**
     * 移除一个Activity
     * @param activity
     */
    public void popOneActivity(Activity activity){

        if(activityStack!=null && activityStack.size()>0){
            if(activity != null){
                activity.finish();
                activityStack.remove(activity);
                activity=null;
            }
        }
    }

    /**
     * 将一个Activity压入栈
     * @param activity
     */
    public void pushOneActivity( Activity activity) {
        if(activityStack==null){
            activityStack=new Stack<>();
        }
        activityStack.add(activity);

    }

    /**
     * 退出时，移除所有Activity
     */
    public void popAllActivity(){
        if( activityStack!=null ) {
            while(activityStack.size()>0){
                Activity activity=activityStack.peek();
                if(activity!=null) {
                    activity.finish();
                }
                activityStack.remove(activity);
            }
        }
    }


}
