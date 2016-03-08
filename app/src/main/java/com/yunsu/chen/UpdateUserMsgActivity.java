package com.yunsu.chen;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
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
import com.yunsu.chen.handler.Login;
import com.yunsu.chen.handler.SerializableMap;
import com.yunsu.chen.ui.YunsuActivity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class UpdateUserMsgActivity extends YunsuActivity {

    private RequestQueue requestQueue;

    private TextView titleTV;
    private EditText suernameET, emailET, addrET, jianjieET;
    private Spinner sexSpinner;
    private Login login;
    private Map<String, String> userMsgMap = null;

    private String httpurl = Config.basiSurl + "index.php?route=moblie/account/edit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_msg);

        requestQueue = Volley.newRequestQueue(this);//加载volley

        titleTV = (TextView) findViewById(R.id.tv_title_top);
        titleTV.setText("修改个人信息");

        suernameET = (EditText) findViewById(R.id.et_update_name);
        emailET = (EditText) findViewById(R.id.et_update_emil);
        addrET = (EditText) findViewById(R.id.et_update_addr);
        jianjieET = (EditText) findViewById(R.id.et_update_jianjie);

        sexSpinner = (Spinner) findViewById(R.id.sp_update_sex);


        //获取上一个页面传来的map
        Bundle bundle = getIntent().getExtras();
        SerializableMap serializableMap = (SerializableMap) bundle.get("map");
        userMsgMap = serializableMap.getMap();

        System.out.println("------"+userMsgMap);

        if (userMsgMap != null) {

            int index = -1;
            if (userMsgMap.get("sex").equals("0")) {
                index = 1;

            } else if (userMsgMap.get("sex").equals("1")) {
                index = 2;

            } else {
                index = 0;
            }

            suernameET.setText(userMsgMap.get("fullname"));
            sexSpinner.setSelection(index);
            emailET.setText(userMsgMap.get("email"));
            addrET.setText(userMsgMap.get("address"));
            jianjieET.setText(userMsgMap.get("intro"));
        }

    }

    //修改个人信息
    private void update() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, httpurl,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        System.out.println("===修改===" + response);
                        try {
                            JSONObject jsonObject = JSONObject.parseObject(response);
                            Map<String, String> map = new HashMap<>();
                            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                                System.out.println(entry.getKey() + "-" + entry.getValue() + "\t");
                                map.put(entry.getKey(), entry.getValue().toString().trim());
                            }
                            if (map.get("status").equals("success")) {
                                Toast.makeText(UpdateUserMsgActivity.this, "修改成功！", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                String msg = map.get("error_fullname").toString() + map.get("error_email") + map.get("error_idcard");
                                Toast.makeText(UpdateUserMsgActivity.this, msg, Toast.LENGTH_LONG).show();
                            }

                        } catch (Exception e) {
                            Log.e("解析错误：", e.toString());
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("网络出错：", error.getMessage(), error);
                        Toast.makeText(UpdateUserMsgActivity.this, "网络不可用！", Toast.LENGTH_LONG);
                    }
                }) {

            //传递cookie
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences preferences = getSharedPreferences("cookies", Context.MODE_PRIVATE);
                String cookie = preferences.getString("cookie", "");
                HashMap cookieMap = new HashMap();
                cookieMap.put("Cookie", cookie);
                return cookieMap;
            }

            //传递参数
            @Override
            protected Map<String, String> getParams() {
                String sex = "-1";
                if (sexSpinner.getSelectedItem().toString().equals("男")) {
                    sex = "0";
                } else {
                    sex = "1";
                }
                HashMap userMsgMap = new HashMap();
                userMsgMap.put("fullname", suernameET.getText().toString().trim());
                userMsgMap.put("email", emailET.getText().toString().trim());
                userMsgMap.put("address", addrET.getText().toString().trim());
                userMsgMap.put("intro", jianjieET.getText().toString().trim());
                userMsgMap.put("sex", sex);
                return userMsgMap;
            }

        };
        requestQueue.add(stringRequest);
    }


    public void doUpdateUserMsg(View view) {
        update();//调用修改信息
    }
}
