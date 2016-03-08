package com.yunsu.chen.handler;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by chen on 2016/1/28.
 *
 * 序列化map供Bundle传递map使用  Activity传值用
 * Created
 */
public class SerializableMap implements Serializable {

    private Map<String,String> map;

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}