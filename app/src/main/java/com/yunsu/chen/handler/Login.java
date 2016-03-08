package com.yunsu.chen.handler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
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
import com.yunsu.chen.LoginActivity;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.interf.NetIntf;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen on 2016/1/21.
 */
public class Login {

    public NetIntf loginIntf;
    private Activity activity;
    private Map<String, String> loginMap;
    private  RequestQueue requestQueue;

    private Map<String, String> userMsgMap=null;
    private String loginUrl = Config.basiSurl+"index.php?route=moblie/login";//登录
    private String url_usermsg = Config.basiSurl+"index.php?route=moblie/account/edit";

    public  Login(Activity activity){

        this.activity=activity;
        requestQueue = Volley.newRequestQueue(activity);//加载volley
    }

    /** 检查登录 **/
    public void chackLogin(NetIntf intf){
        this.loginIntf=intf;
        //取出用户和密码
        SharedPreferences preferences = activity.getSharedPreferences("login", Context.MODE_PRIVATE);
        if (preferences.getString("user", "").length()>1) {
            loginMap = new HashMap<String, String>();
            loginMap.put("telephone", preferences.getString("user", ""));
            loginMap.put("password", preferences.getString("password", ""));

            System.out.println("用户："+preferences.getString("user", ""));
            System.out.println("密码："+preferences.getString("password", ""));
        }else{
            Toast.makeText(activity,"你是第一次登录！",Toast.LENGTH_LONG).show();
            System.out.println("你是第一次登录！");
            Intent intent=new Intent();
            intent.setClass(activity, LoginActivity.class);
            activity.startActivityForResult(intent,0);
            return;
        }

        //没有cookie执行登录
        SharedPreferences cookiePreferences = activity.getSharedPreferences("cookies", Context.MODE_PRIVATE);
        if(cookiePreferences.getString("logintype", "").length()<1){
            Intent intent=new Intent();
            intent.setClass(activity, LoginActivity.class);
            activity.startActivityForResult(intent,0);
            return;
        }
        if (cookiePreferences.getString("cookie", "").length()<1) {
            System.out.println("无cookie值！");
            Toast.makeText(activity,"系统为你自动登录！",Toast.LENGTH_LONG).show();
            toLogin();
            return;
        }
        //检查登录
        StringRequest mStringRequest = new StringRequest(url_usermsg,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String josnString) {
                        System.out.println("验证登录状态:" + josnString);
                        userMsgMap=JsonDispose.toMapStr(josnString);

                        //如果没登陆则自动登录
                        if (userMsgMap.get("login").toString().equals("true")){
                            loginIntf.getNetMsg();//调用接口
                            System.out.println("登录会话还存在！");
                        }else {
                            System.out.println("登录会话不存在，自动重新登录！");
                            toLogin();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("网络出错", error.toString());
                Toast.makeText(activity,"服务器繁忙！",Toast.LENGTH_LONG).show();
            }
        }
        ) {
            //传递cookie
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getCookie();
            }

        };
        // 将StringRequest添加到RequestQueue
        requestQueue.add(mStringRequest);
    }


    //请求登录post
    private void toLogin(){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("登录中",response);
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        for (java.util.Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                            System.out.print(entry.getKey() + "-" + entry.getValue() + "\t");
                            System.out.println();
                        }

                        loginIntf.getNetMsg();//调用接口
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("网络出错：", error.getMessage(), error);
                        Toast.makeText(activity, "网络不可用！", Toast.LENGTH_LONG);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的登录用户名，密码参数

                return loginMap;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // TODO Auto-generated method stub
                try {
                    Map<String, String> responseHeaders = response.headers;
                    String cookie = responseHeaders.get("Set-Cookie");

                    SharedPreferences preferences = activity.getSharedPreferences("cookies", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();
                    editor.putString("cookie", cookie);
                    editor.putString("logintype","aut");
                    editor.commit();

                    String dataString = new String(response.data, "UTF-8");
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

        };
        requestQueue.add(stringRequest);
    }

    //获取登录成功后的cookie
    public Map<String,String> getCookie(){
        SharedPreferences preferences = activity.getSharedPreferences("cookies", Context.MODE_PRIVATE);
        String cookie = preferences.getString("cookie", "");
        HashMap cookieMap = new HashMap();
        cookieMap.put("Cookie", cookie);
        return cookieMap;
    }

    //获取登录成功后的用户信息（在回调函数中利用）
    public Map<String, String> getUserMsg(){
        return userMsgMap;
    }
}
