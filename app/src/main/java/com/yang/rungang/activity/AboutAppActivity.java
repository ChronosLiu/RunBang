package com.yang.rungang.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.yang.rungang.R;
import com.yang.rungang.model.biz.ActivityManager;

public class AboutAppActivity extends BaseActivity {

    private ImageView backImg;
    private TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about_app);
        ActivityManager.getInstance().pushOneActivity(this);

        backImg = (ImageView) findViewById(R.id.about_back_img);
        version = (TextView) findViewById(R.id.about_app_version);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutAppActivity.this.finish();
            }
        });
        version.setText(getVersion());
    }

    /**
     * 获取版本号
     * @return
     */
    public String getVersion() {
        String versionName = "";
         try {
             PackageManager manager = this.getPackageManager();
             PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
             String version = info.versionName;
             versionName = "V"+version;
         } catch (Exception e) {
             e.printStackTrace();
         }

        return versionName;
    }
}
