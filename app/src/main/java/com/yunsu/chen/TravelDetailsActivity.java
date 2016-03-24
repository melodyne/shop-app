package com.yunsu.chen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ListView;
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
import com.yunsu.chen.slide.Advertisements;
import com.yunsu.chen.slide.ImageLoaderUtil;
import com.yunsu.chen.slide.RequestManager;
import com.yunsu.chen.adapter.AdapterTese;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.handler.YunsuMap;
import com.yunsu.chen.handler.YunsuUI;
import com.yunsu.chen.ui.YunsuActivity;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class TravelDetailsActivity extends YunsuActivity {

    /** 旅游幻灯片布局  地图导航 **/
    private LinearLayout llAdvertiseBoard;
    private LayoutInflater lyflater;
    private TextView titleTV;
    private WebView webView;
    private ListView travelRecommendLV;
    private ImageView mapImageView;
    private Button jiaoshaoBT,nearBT,mapBT;

    private String product_id,url_list;
    private String mapEnd;//目的地坐标

    /** volly框架 **/
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_details);

        llAdvertiseBoard = (LinearLayout)findViewById(R.id.slide_travel);
        webView=(WebView)findViewById(R.id.wv_product_description);
        travelRecommendLV=(ListView)findViewById(R.id.listview_travel_recommend);
        mapImageView=(ImageView)findViewById(R.id.imgv_map);
        titleTV=(TextView)findViewById(R.id.tv_title_top);
        jiaoshaoBT=(Button)findViewById(R.id.bt_travel_jieshao);
        nearBT=(Button)findViewById(R.id.bt_travel_near);
        mapBT=(Button)findViewById(R.id.bt_travel_map);

        RequestManager.init(this);
        lyflater = LayoutInflater.from(this);

        mRequestQueue =  Volley.newRequestQueue(this);//载入框架

        Intent intent=getIntent();
        product_id=intent.getStringExtra("product_id");
        url_list= Config.basiSurl+"index.php?route=moblie/product&product_id="+product_id;
        Toast.makeText(this,product_id,Toast.LENGTH_LONG).show();

        initViews();//初始化UI
    }

    public void doBack(View view){
        finish();
    }

    private void initViews(){

        /** volly网络请求  **/
        StringRequest request = new StringRequest(Request.Method.GET, url_list, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonString) {



                JSONObject jsonObj = JSON.parseObject(jsonString);
                /** 产品名  **/
                String productName = jsonObj.getString("product_name");
                /** 产品详述  **/
                String productDescription = jsonObj.getString("description");
                /** 地图坐标  **/
                mapEnd = jsonObj.getString("map");
                /** 图片组  **/
                List<Map<String,Object>> listImages = (List<Map<String, Object>>) jsonObj.get("images");
                /** 特色服务  **/
                List<Map<String,Object>> listTese = (List<Map<String, Object>>) jsonObj.get("featureService");



                if(mapEnd!=null) {
                    Log.e("地图坐标：", mapEnd.toString());
                    ImageLoaderUtil.getImage(TravelDetailsActivity.this, mapImageView, YunsuMap.getMapimgAPI(mapEnd), R.drawable.chen, 0, 0, 0);
                }

                if(listTese!=null){
                    Log.e("特色服务：", listTese.toString());
                    AdapterTese myAdapter = new AdapterTese(TravelDetailsActivity.this,listTese);
                    travelRecommendLV.setAdapter(myAdapter);
                    YunsuUI.setListViewHeightBasedOnChildren(travelRecommendLV);
                }

                //添加css让图片自适应组件
                productDescription="<style>img{max-width:100%;height:auto}" +
                 "video{max-width:100%;height:auto}</style>"+productDescription;

                webView.loadDataWithBaseURL(null, productDescription, "text/html", "utf-8", null);
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//优先使用缓存
                webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setJavaScriptEnabled(true);//允许执行js

                titleTV.setText(productName);

                /** 头部幻灯片 **/
                if(listImages==null||listImages.toString().length()<5) {
                    Log.e("图片组为空：", listImages.toString());
                    llAdvertiseBoard.setVisibility(View.GONE);
                }else{
                    llAdvertiseBoard.addView(new Advertisements(TravelDetailsActivity.this, true, lyflater, 3000).initView(listImages));
                }

                jiaoshaoBT.setVisibility(View.VISIBLE);
                nearBT.setVisibility(View.VISIBLE);
                mapBT.setVisibility(View.VISIBLE);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(TravelDetailsActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueue.add(request);

    }


    public void doMap(View view){

        if(mapEnd==null){
            Toast.makeText(TravelDetailsActivity.this,"服务端没有设置目的地地理坐标！",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,mapEnd,Toast.LENGTH_LONG).show();
            try {
                mapEnd= YunsuMap.getZuobiao(mapEnd);//坐标转换

                String bd="intent://map/direction?" +
                        "origin=我的位置&" +
                        "destination=latlng:"+mapEnd+"|name:明珠广场&" +
                        "mode=driving&" +
                        "coord_type=bd09ll&"+
                        "region=海口&" +
                        "src=yourCompanyName|yourAppName#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end";
                Intent intent = Intent.getIntent(bd);

                if(isInstallByread("com.baidu.BaiduMap")){
                    startActivity(intent); //启动调用
                    Log.e("GasStation", "百度地图客户端已经安装") ;
                }else{
                    Toast.makeText(this,"没有安装百度地图客户端,请下载安装！",Toast.LENGTH_LONG);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

    }

    //判断应用是否存在
    private boolean isInstallByread(String packageName)
    {
        return new File("/data/data/" + packageName).exists();
    }


}
