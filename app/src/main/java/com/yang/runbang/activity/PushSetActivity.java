package com.yang.runbang.activity;

import android.os.Bundle;
import android.view.Window;

import com.yang.runbang.R;

public class PushSetActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_push_set);
    }
}
