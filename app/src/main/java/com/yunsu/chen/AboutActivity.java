package com.yunsu.chen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yunsu.chen.ui.YunsuActivity;

/**
 * Created by chen on 2016/1/27.
 */
public class AboutActivity extends YunsuActivity{

    private TextView titleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        titleTV=(TextView)findViewById(R.id.tv_title_top);
        titleTV.setText(R.string.user_about);

    }

    public void doBack(View view){
        finish();
    }


}