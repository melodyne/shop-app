package com.yunsu.chen.handler;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chen on 2016/1/28.
 */
public class JsonDispose {
    public static Map<String,String> toMapStr(String JsonString){
        Map<String,String> map1=new HashMap<String,String>();

        try {
            JSONObject jsonObject = JSONObject.parseObject(JsonString);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                map1.put(entry.getKey(), entry.getValue().toString().trim());
            }
        }catch (Exception e){
            Log.e("解析错误",e.toString());
        }
        return map1;
    }

    public static Map<String,Object> toMapObj(String JsonString){
        Map<String,Object> map2=new HashMap<String,Object>();

        try {
            JSONObject jsonObject = JSONObject.parseObject(JsonString);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                map2.put(entry.getKey(), entry.getValue());
            }
        }catch (Exception e){
            Log.e("解析错误",e.toString());
        }
        return map2;
    }
}
