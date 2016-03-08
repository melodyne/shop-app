package com.yunsu.chen;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunsu.chen.adapter.AdapterIndex;
import com.yunsu.chen.adapter.AdapterOrder;
import com.yunsu.chen.handler.JsonDispose;
import com.yunsu.chen.handler.Login;
import com.yunsu.chen.handler.YunsuHttp;
import com.yunsu.chen.interf.NetIntf;
import com.yunsu.chen.ui.YunsuActivity;

import java.util.List;
import java.util.Map;

public class OrderActivity extends YunsuActivity {

    private ListView listView;
    private YunsuHttp yunsuHttp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        listView=(ListView)findViewById(R.id.listview_order);
        TextView titleTV=(TextView)findViewById(R.id.tv_title_top);
        titleTV.setText("我的订单");

        Login login=new Login(this);
        login.chackLogin(new NetIntf() {
            @Override
            public void getNetMsg() {
                initViews();//调用初始化订单列表
            }
        });

    }

    private void initViews(){
        yunsuHttp=new YunsuHttp(this);

        yunsuHttp.doGet("index.php?route=moblie/account/order", new NetIntf() {
            @Override
            public void getNetMsg() {
                String jsonString=yunsuHttp.getJsonString();//获取到json
                JSONObject jsonObj = JSON.parseObject(jsonString);

                System.out.println("=====order=====");
                System.out.println("订单：" + jsonObj);

                //状态
                String status = jsonObj.getString("status").trim();

                if(status.equals("success")){

                    /** 订单列表  **/
                    List<Map<String,Object>> orderList = (List<Map<String, Object>>) jsonObj.get("orders");
                    if(orderList==null||orderList.toString().length()<10) {
                        Log.e("订单为空：", orderList.toString());
                    }else{
                        AdapterOrder myAdapter = new AdapterOrder(OrderActivity.this,orderList);
                        listView.setAdapter(myAdapter);
                    }
                }else {
                    Toast.makeText(OrderActivity.this,"你还没有订单哦！",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void doBack(View view){
        finish();
    }
}
