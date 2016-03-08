package com.yunsu.chen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yunsu.chen.handler.Login;
import com.yunsu.chen.handler.SerializableMap;
import com.yunsu.chen.interf.NetIntf;
import com.yunsu.chen.ui.YunsuActivity;

import java.util.Map;

public class PersonalActivity extends YunsuActivity {

    private TextView titleTV;
    private TextView suernameTV,sexTV,emailTV,addrTV,jianjieTV;
    private Login login;
    private Map<String,String> userMsgMap=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        titleTV=(TextView)findViewById(R.id.tv_title_top);
        titleTV.setText(R.string.Personal);

        suernameTV=(TextView)findViewById(R.id.tv_personal_username);
        sexTV=(TextView)findViewById(R.id.tv_personal_sex);
        emailTV=(TextView)findViewById(R.id.tv_personal_email);
        addrTV=(TextView)findViewById(R.id.tv_personal_addr);
        jianjieTV=(TextView)findViewById(R.id.tv_personal_jianjie);

        login=new Login(this);

        login.chackLogin(new NetIntf() {
            @Override
            public void getNetMsg() {
                initViews();
            }
        });

    }

    private void initViews(){
        userMsgMap=login.getUserMsg();//获取用户信息
        suernameTV.setText(userMsgMap.get("fullname"));
        sexTV.setText("性别："+userMsgMap.get("sex"));
        emailTV.setText("电子邮箱："+userMsgMap.get("email"));
        addrTV.setText("地址：" + userMsgMap.get("address"));
        jianjieTV.setText("简介：" + userMsgMap.get("intro"));

    }
    public void doBack(View view){
        finish();
    }
    public void doChange(View view){

        SerializableMap myMap=new SerializableMap();
        myMap.setMap(userMsgMap);//将map数据添加到封装的myMap<span></span>中

        Bundle bundle=new Bundle();
        bundle.putSerializable("map", myMap);

        Intent intent=new Intent();
        intent.setClass(this, UpdateUserMsgActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }


}
