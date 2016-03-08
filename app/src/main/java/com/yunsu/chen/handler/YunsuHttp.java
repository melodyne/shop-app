package com.yunsu.chen.handler;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.interf.NetIntf;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen on 2016/1/28.
 * <p/>
 * 用volley网络请求
 */
public class YunsuHttp {
    private Context context;
    private RequestQueue requestQueue;

    private String basiSurl = Config.basiSurl;
    private String httpJson = null;

    /**
     * 构造函数
     * @param context
     */
    public YunsuHttp(Context context) {
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);//加载volley
    }

    /**
     * GET方式请求
     *
     * @param path 请求路径
     * @param netIntf 回调对象
     */
    public void doGet(String path, final NetIntf netIntf) {
        String url = basiSurl + path;
        StringRequest mStringRequest = new StringRequest(url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String jsonString) {
                        httpJson = jsonString;
                        netIntf.getNetMsg();//调用回调函数接口
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("网络错误", error.toString());
                Toast.makeText(context, "服务器繁忙！", Toast.LENGTH_LONG).show();
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

    /**
     * 执行POST请求
     * @param path 请求资源路径
     * @param parameter  post 参数
     * @param netIntf   回调对象
     */
    public void doPost(String path,final Map<String, String> parameter,final NetIntf netIntf) {

        String url = basiSurl + path;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String jsonString) {
                        httpJson = jsonString;
                        netIntf.getNetMsg();//调用回调函数接口
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("网络出错：", error.getMessage(), error);
                        Toast.makeText(context, "网络不可用！", Toast.LENGTH_LONG);
                    }
                }
        ) {
            //传递cookie
            public Map<String, String> getHeaders() throws AuthFailureError {
                return getCookie();
            }

            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的登录用户名，密码参数

                return parameter;
            }
        };
        requestQueue.add(stringRequest);
    }

    /**
     * 请求后，必须在回调函数中调用此方法 获取请求结果Json字符串
     *
     * @return
     */
    public String getJsonString() {
        if (httpJson == null) {
            return "0";//0表示请求失败
        } else {
            return httpJson;
        }
    }

    /**
     * 获取登录成功后的cookie
     *
     * @return 字符串
     */
    public Map<String, String> getCookie() {
        SharedPreferences preferences = context.getSharedPreferences("cookies", Context.MODE_PRIVATE);
        String cookie = preferences.getString("cookie", "");
        HashMap cookieMap = new HashMap();
        cookieMap.put("Cookie", cookie);
        return cookieMap;
    }
}
