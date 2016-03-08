package com.yunsu.chen;

/**
 * 产品列表adapter
 * Created by chen on 2016/1/18.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yunsu.chen.adapter.AdapterIndex;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.listview.WaterDropListView;
import com.yunsu.chen.ui.YunsuActivity;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TravelActivity extends YunsuActivity implements WaterDropListView.IWaterDropListViewListener  {

    private RequestQueue queue;
    private String leibieId,leibieName;

    private String url;

    private TextView title;

    private WaterDropListView waterDropListView;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    waterDropListView.stopRefresh();
                    break;
                case 2:
                    waterDropListView.stopLoadMore();
                    break;
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);

        Intent intent=getIntent();
        leibieId=intent.getStringExtra("id").trim();
        leibieName=intent.getStringExtra("name").trim();
        url=Config.basiSurl+"index.php?route=moblie/productList&path="+leibieId+"&sort=p.price&order=asc&limit=100&page=1";

        title=(TextView)findViewById(R.id.tv_title_top);
        title.setText(leibieName);

        queue= Volley.newRequestQueue(this);//初始化Volly框架

        waterDropListView = (WaterDropListView) findViewById(R.id.listview_travel);
        waterDropListView.setPullLoadEnable(true);
        waterDropListView.setWaterDropListViewListener(this);//刷新加载监听
        initialView();//初始化组件
    }


    @Override
    public void onRefresh() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onLoadMore() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    handler.sendEmptyMessage(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void doBack(View view){
        finish();
    }

    private void initialView(){
        /** volly网络处理 **/
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonString) {

                JSONObject jsonObj = JSON.parseObject(jsonString);

                /** 旅游列表  **/
                List<Map<String,Object>> listPosition = (List<Map<String, Object>>) jsonObj.get("products");
                if(listPosition==null||listPosition.toString().length()<10) {
                    Log.e("图片组：", listPosition.toString());
                }else{
                    Log.e("图片组为空：", listPosition.toString());
                    AdapterIndex myAdapter = new AdapterIndex(TravelActivity.this,listPosition);
                    waterDropListView.setAdapter(myAdapter);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(TravelActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        queue.add(request);

    }
}
