package com.yunsu.chen;


/**
 * Created by chen on 2016/1/29.
 */

import org.json.JSONObject;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yunsu.chen.handler.SerializableMap;
import com.yunsu.chen.handler.YunsuHttp;
import com.yunsu.chen.interf.NetIntf;
import com.yunsu.chen.ui.LoadingDialog;
import com.yunsu.chen.ui.YunsuActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;


public class PayActivity extends YunsuActivity {

    private IWXAPI api;
    private YunsuHttp yunsuHttp;
    private Button payBtn;
    private Map<String, String> buyMap;
    private LinearLayout wenxin,zhifubao;
    private RadioButton wenxinpay,zhifubaopay;
    private Dialog loadingDialog;
    private int paychoose;
    private TextView goodsname,goodsprice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        goodsname=(TextView)findViewById(R.id.goods_name);
        goodsprice=(TextView)findViewById(R.id.goods_price);
        wenxin=(LinearLayout)findViewById(R.id.wenxin);
        zhifubao=(LinearLayout)findViewById(R.id.zhifubao);
        wenxinpay=(RadioButton)findViewById(R.id.wenxinpay);
        zhifubaopay=(RadioButton)findViewById(R.id.zhifubaopay);

        Intent intent=getIntent();
        double price1=intent.getDoubleExtra("price", 1);
        double price=intent.getDoubleExtra("Total", 1);
        if (price==1.0){
            goodsprice.setText("订单金额："+price1);
        }else {
            goodsprice.setText("订单金额："+price);
        }

        String goods=intent.getStringExtra("name");
        goodsname.setText("订单详情："+goods);
        paychoose=0;
        wenxin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paychoose==0){
                    wenxinpay.setChecked(true);
                    paychoose=1;
                }else {
                    zhifubaopay.setChecked(false);
                    wenxinpay.setChecked(true);
                }
            }
        });
        zhifubao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paychoose==0){
                    zhifubaopay.setChecked(true);
                    paychoose=1;
                }else{
                    wenxinpay.setChecked(false);
                    zhifubaopay.setChecked(true);
                }
            }
        });
        wenxinpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paychoose=1;
                zhifubaopay.setChecked(false);
                wenxinpay.setChecked(true);

            }
        });
        zhifubaopay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                paychoose=1;
                wenxinpay.setChecked(false);
                zhifubaopay.setChecked(true);
            }
        });
        TextView titleTV = (TextView) findViewById(R.id.tv_title_top);
        titleTV.setText("支付订单");

        //获取上一个页面传来的map
        buyMap = new HashMap<String, String>();
        Bundle bundle = getIntent().getExtras();
        SerializableMap serializableMap = (SerializableMap) bundle.get("map");
        buyMap = serializableMap.getMap();

        loadingDialog=LoadingDialog.createLoadingDialog(this, "正在调起支付，请稍等...", false);

        payBtn = (Button) findViewById(R.id.appay_btn);
        payBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (paychoose==0){
                    Toast.makeText(PayActivity.this,"请选择支付方式",Toast.LENGTH_LONG).show();
                }else {
                    payBtn.setClickable(false);
                    loadingDialog.show();
                    yunsuHttp = new YunsuHttp(PayActivity.this);
                    yunsuHttp.doPost("index.php?route=moblie/checkout/confirmOfSingle", buyMap, new NetIntf() {
                        @Override
                        public void getNetMsg() {
                            Log.e("回调", "pp");
                            String jsonString = yunsuHttp.getJsonString();
                            Log.e("支付", jsonString);

                            Toast.makeText(PayActivity.this, "获取订单中...", Toast.LENGTH_SHORT).show();
                            try {
                                JSONObject json = new JSONObject(jsonString);
                                String state = json.getString("status");

                                if (null != json && state.equals("success")) {

                                    api = WXAPIFactory.createWXAPI(PayActivity.this, null);
                                    api.registerApp("wx72596a4779b51c00");

                                    PayReq req = new PayReq();
                                    req.appId = "wx72596a4779b51c00";//appid
                                    req.partnerId = "1312330701";//商户id
                                    req.prepayId = json.getString("prepayId");//预支付交易会话ID
                                    req.nonceStr = json.getString("nonceStr");//随机字符串
                                    req.timeStamp = json.getString("timeStamp");//时间戳
                                    req.packageValue = json.getString("packageValue");//应用包名
                                    req.sign = json.getString("sign");//签名
                                    req.extData = "app data"; // optional
                                    api.sendReq(req);
                                    loadingDialog.dismiss();

                                } else {
                                    loadingDialog.dismiss();
                                    Log.d("PAY_GET", "返回错误" + json.getString("tip"));
                                    Toast.makeText(PayActivity.this, "返回错误" + json.getString("tip"), Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                Log.e("PAY_GET", "异常：" + e.getMessage());
                                Toast.makeText(PayActivity.this, "异常：" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    payBtn.setClickable(true);
                }

            }
        });
    }


    public void doBack(View view) {
        finish();
    }

}

