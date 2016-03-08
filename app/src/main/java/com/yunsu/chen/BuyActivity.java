package com.yunsu.chen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yunsu.chen.handler.Options;
import com.yunsu.chen.handler.SerializableMap;
import com.yunsu.chen.slide.ImageLoaderUtil;
import com.yunsu.chen.ui.YunsuActivity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by chen on 2016/1/23.
 */
public class BuyActivity extends YunsuActivity {



    private TextView titleTV;
    private TextView addbt;//增加商品
    private TextView numTV;//显示商品数量
    private TextView decbt;//减少商
    private TextView priceTV;//单价
    private TextView tolTV;//总价
    private String productName;
    private String Text;//测试1

    ///jdhfgkjdhfjkgdhfgjkhdkfjghdkfhgkczc


    private Map<String,Double> pricemap;//可选价格

    private RadioGroup optionGroup;

    private int num=1;//商品数量
    private Double price,tolMoney;

    private String jsonStr;//商品信息
    private String productId;
    //商品可选值
    private String product_option_id,product_option_value_id;//选项id
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        //选择的价格组
        pricemap=new HashMap<>();

        optionGroup=(RadioGroup)findViewById(R.id.option);
        priceTV=(TextView)findViewById(R.id.goods_price);
        tolTV=(TextView)findViewById(R.id.tv_buy_tol);

        titleTV=(TextView)findViewById(R.id.tv_title_top);
        titleTV.setText("立即购买");
        addbt=(TextView)findViewById(R.id.add);
        numTV=(TextView)findViewById(R.id.num);
        decbt=(TextView)findViewById(R.id.dec);

        Intent intent = getIntent();
        jsonStr = intent.getStringExtra("jsonStr");
        JSONObject jsonObj = JSON.parseObject(jsonStr);
        System.out.println("商品信息："+jsonObj);

        //商品id
        productId=jsonObj.getString("product_id");
        //商品名
        productName = jsonObj.getString("product_name");
        TextView productNameTV=(TextView)findViewById(R.id.goods_name);
        productNameTV.setText(productName);


        //商品图片
        String popup = jsonObj.getString("popup");
        ImageView goodsImg=(ImageView)findViewById(R.id.goods_img);
        ImageLoaderUtil.getImage(this, goodsImg, popup, 0, 0, 0, 0);

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
            int i=0;
            while(product_option_value_iterator.hasNext()){

                i++;
                Map<String, Object> product_option_value_iterator_value = product_option_value_iterator.next();
                //选择值ID
                product_option_value_id = (String) product_option_value_iterator_value.get("product_option_value_id");
                System.out.println("product_option_value_id:" + product_option_value_id);

                //选择姓名
                String product_option_value_name = (String) product_option_value_iterator_value.get("name");
                System.out.println("name:" + product_option_value_name);

                //可选规格项目的价格
                String optionPrice = (String) product_option_value_iterator_value.get("price");
                price=   Double.parseDouble(optionPrice.replace("￥","").replace(",", ""));
                System.out.println("price:" + optionPrice);
                pricemap.put(product_option_value_name, price);

                //new RadioGroup
                RadioGroup.LayoutParams params = new  RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 20, 0);
                RadioButton radioButton = (RadioButton) LayoutInflater.from(this).inflate(R.layout.goods_radiobutton_selector, null);
                radioButton.setText(product_option_value_name);
                optionGroup.addView(radioButton, params);
                if(i==1){
                    optionGroup.check(radioButton.getId());
                    priceTV.setText("￥" + price);//单价
                    tolTV.setText("总计：￥"+price); //总价
                }

                optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup rg, int checkedId) {
                        // TODO Auto-generated method stub
                        RadioButton radioChecked = (RadioButton) findViewById(rg.getCheckedRadioButtonId());
                        price = pricemap.get(radioChecked.getText());
                        priceTV.setText("￥" + price);//单价
                        tolTV.setText("总计：￥"+price*num);
                        Toast.makeText(BuyActivity.this, price + "", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }

        initViews();//初始化组件

    }

    public void initViews(){
        addbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num<50) {
                    num++;
                    tolMoney=price*num;
                    numTV.setText(num + "");
                    tolTV.setText("总计：￥"+tolMoney);
                }else {
                    num=50;
                    tolMoney=price*num;
                    numTV.setText(num + "");
                    tolTV.setText("总计：￥"+tolMoney);
                }
            }
        });

        decbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num > 1) {
                    num--;
                    tolMoney = price * num;
                    numTV.setText(num + "");
                    tolTV.setText("总计：￥" + tolMoney);
                } else {
                    num = 1;
                    tolMoney = price * num;
                    numTV.setText(num + "");
                    tolTV.setText("总计：￥" + tolMoney);
                }
            }
        });
    }
    public void doBack(View view){
        finish();
    }
    public void doPay(View view){

        Map<String,String> map=new HashMap<String, String>();
        map.put("product_id", productId);
        map.put("quantity", numTV.getText().toString());
        map.put("option[" + product_option_id + "]", product_option_value_id);
        map.put("address", "海南省海口海南师范大学");
        map.put("payment_method", "app");

        SerializableMap myMap=new SerializableMap();
        myMap.setMap(map);//将map数据添加到封装的myMap<span></span>中

        Bundle bundle=new Bundle();
        bundle.putSerializable("map", myMap);

        Intent intent=new Intent();
        intent.setClass(this, PayActivity.class);
        intent.putExtras(bundle);
        intent.putExtra("Total", tolMoney);
        intent.putExtra("price",price);
        intent.putExtra("name",productName);
        startActivity(intent);

    }

}
