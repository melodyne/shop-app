package com.yunsu.chen.handler;

/**
 * Created by chen on 2016/1/18.
 */
public class YunsuMap {

    public static String getZuobiao(String baiduZuobiao){

        try {
            String[] mapArray = baiduZuobiao.split(",");
            return mapArray[1]+","+mapArray[0];
        }catch (Exception e){
            return null;
        }

    }

    public static String getMapimgAPI(String baiduZuobiao){
        String API="http://api.map.baidu.com/staticimage?" +
                "center=&" +
                "zoom=14&" +
                "markers="+baiduZuobiao+"&" +
                "qq-pf-to=pcqq.group";
        System.out.println(API);
        return API;
    }
}
