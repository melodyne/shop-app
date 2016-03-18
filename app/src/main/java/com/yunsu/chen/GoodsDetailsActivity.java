package com.yunsu.chen;

/**
 * Created by chen on 2016/1/19.
 */


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
import com.yunsu.chen.handler.JsonDispose;
import com.yunsu.chen.handler.Options;
import com.yunsu.chen.handler.YunsuHttp;
import com.yunsu.chen.interf.NetIntf;
import com.yunsu.chen.slide.Advertisements;
import com.yunsu.chen.slide.RequestManager;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.ui.YunsuActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GoodsDetailsActivity extends YunsuActivity {

    private LinearLayout llAdvertiseBoard;
    private LayoutInflater lyflater;
    private TextView productNameTV, productPriceTV;


    private String product_id, url_list;
    private String goodsNum="1";
    //商品可选值
    private String product_option_id,product_option_value_id;//选项id 选择id
    private String jsonStr;//商品的json
    private YunsuHttp yunsuHttp;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_details);

        llAdvertiseBoard = (LinearLayout) findViewById(R.id.slide_travel);
        productNameTV = (TextView) findViewById(R.id.tv_product_name);
        productPriceTV = (TextView) findViewById(R.id.tv_product_pice);


        RequestManager.init(this);
        lyflater = LayoutInflater.from(this);

        mRequestQueue = Volley.newRequestQueue(this);//载入框架

        Intent intent = getIntent();
        product_id = intent.getStringExtra("product_id");
        if(product_id==null){
            return;
        }
        try{
            //设置标题名称
            String title=intent.getStringExtra("title");
            TextView titleTV=(TextView)findViewById(R.id.tv_title_top);
            titleTV.setText(title);
        }catch (Exception e){

        }
        url_list = Config.basiSurl + "index.php?route=moblie/product&product_id=" + product_id;
        System.out.println(url_list);
        Toast.makeText(this, product_id, Toast.LENGTH_LONG).show();

        initViews();//初始化UI
    }

    public void doBack(View view) {
        finish();
    }

    private void initViews() {

        /** volly网络请求  **/
        StringRequest request = new StringRequest(Request.Method.GET, url_list, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonString) {

                jsonStr=jsonString;
                JSONObject jsonObj = JSON.parseObject(jsonString);

                /** 产品名  **/
                String productName = jsonObj.getString("product_name");
                System.out.println(productName+"");
                productNameTV.setText(productName);

                /** 价格  **/
                String productPrice = jsonObj.getString("price");
                System.out.println(productPrice+"");
                productPriceTV.setText(productPrice);

                /** 特价  **/
                String productSpecial = jsonObj.getString("special");


                //商品规格
                Options options = new Options(jsonStr);
                List<Map<String, Object>> list= options.getOptions_return();
                Iterator<Map<String, Object>> iterator =  list.iterator();
                while (iterator.hasNext()) {
                    Map<String, Object> option = iterator.next();

                    //选项ID
                    product_option_id =  (String) option.get("product_option_id");
                    System.out.println("product_option_id:"+product_option_id);
                    //选项名称
                    String name =  (String) option.get("name");
                    System.out.println("name:"+name);
                    //可选值
                    List<Map<String, Object>> product_option_value =  (List<Map<String, Object>>) option.get("product_option_value");

                    Iterator<Map<String, Object>> product_option_value_iterator = product_option_value.iterator();
                    while(product_option_value_iterator.hasNext()){

                        Map<String, Object> product_option_value_iterator_value = product_option_value_iterator.next();
                        //选择值ID
                        product_option_value_id = (String) product_option_value_iterator_value.get("product_option_value_id");
                        System.out.println("product_option_value_id:"+product_option_value_id);

                        //选择姓名
                        String product_option_value_name = (String) product_option_value_iterator_value.get("name");
                        System.out.println("name:"+product_option_value_name);
                    }
                }



                /** 图片组  幻灯片 **/
                List<Map<String, Object>> listImages = (List<Map<String, Object>>) jsonObj.get("images");
                if (listImages == null || listImages.toString().length() < 5) {
                    Log.e("图片组为空：", listImages.toString());
                    llAdvertiseBoard.setVisibility(View.GONE);
                } else {
                    llAdvertiseBoard.addView(new Advertisements(GoodsDetailsActivity.this, true, lyflater, 3000).initView(listImages));
                }

                /** 特色服务  **/
                List<Map<String, Object>> listTese = (List<Map<String, Object>>) jsonObj.get("featureService");


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(GoodsDetailsActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
            }
        });
        mRequestQueue.add(request);

    }


    //立即购买
    public void doBuy(View view) {
        Intent intent=new Intent();
        intent.setClass(this, BuyActivity.class);
        intent.putExtra("jsonStr", jsonStr);
        startActivity(intent);

        Toast.makeText(this, "立即购买", Toast.LENGTH_LONG).show();
    }

    //加入购物车
    public void doCar(View view) {
        Map<String,String> map=new HashMap<String,String>();
        map.put("product_id",product_id);
        map.put("quantity",goodsNum);
        map.put("option["+product_option_id+"]",product_option_value_id);

        yunsuHttp=new YunsuHttp(this);
        yunsuHttp.doPost("index.php?route=moblie/checkout/cart/add", map, new NetIntf() {
            @Override
            public void getNetMsg() {
                String jsonString = yunsuHttp.getJsonString();
                Map<String,String> map=JsonDispose.toMapStr(jsonString);
                System.out.println("map:" + map);
                Toast.makeText(GoodsDetailsActivity.this, map.get("tip"), Toast.LENGTH_LONG).show();

            }
        });

    }
}
