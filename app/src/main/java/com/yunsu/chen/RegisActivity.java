package com.yunsu.chen;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.ui.YunsuActivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class RegisActivity extends YunsuActivity {

    private RequestQueue requestQueue;

    private EditText edtUser,edtPassword,edtConpassword;
    private Button btnRegis=null;
    private String   user=null,password=null,conpassword=null;
    private String urlRegis= Config.basiSurl+"index.php?route=moblie/account/register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        requestQueue = Volley.newRequestQueue(getApplicationContext());//加载volley

        edtUser=(EditText)findViewById(R.id.regis_edt_user);
        edtPassword=(EditText)findViewById(R.id.regis_edt_password);
        edtConpassword=(EditText)findViewById(R.id.regis_edt_conpassword);

        btnRegis=(Button)findViewById(R.id.regis_bnt);
        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user=edtUser.getText().toString();
                password=edtPassword.getText().toString();
                conpassword=edtConpassword.getText().toString();

                regis();
            }
        });
    }

    public void doBack(View view){

        finish();
    }

    //注册
    private void regis(){
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                urlRegis,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.d("json", "response -> " + response);
                        JSONObject jsonObject = JSONObject.parseObject(response) ;

                        Map<String, String> mapMsg = new HashMap<String, String>();
                        for(java.util.Map.Entry<String,Object> entry:jsonObject.entrySet()){

                            mapMsg.put(entry.getKey(),entry.getValue().toString());
                            System.out.print(entry.getKey()+"-"+entry.getValue() + "\t");
                            System.out.println();
                        }
                        Toast.makeText(getApplicationContext(), mapMsg.get("tip") + " 注册"+mapMsg.get("status")+"！", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err==", error.getMessage(), error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的参数
                Map<String, String> map = new HashMap<String, String>();
                map.put("telephone",user );
                map.put("password",password);
                map.put("confirm",conpassword);
                map.put("fullname","陈万洲");

                return map;
            }
            @Override
            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                // TODO Auto-generated method stub
                try {

                    Map<String, String> responseHeaders = response.headers;
                    String dataString = new String(response.data, "UTF-8");
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

        };
        requestQueue.add(stringRequest);
    }
}
