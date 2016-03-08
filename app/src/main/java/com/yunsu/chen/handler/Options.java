package com.yunsu.chen.handler;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 *选择商品类别的解析类
 *
 */
public class Options {

	private List<Map<String,Object>> options_return;

	public Options(String jsonStr){

		//返回的信息
		options_return = new ArrayList<Map<String,Object>>();

		JSONObject jsonObj = JSON.parseObject(jsonStr);
		List<Map<String,Object>> options = (List<Map<String, Object>>) jsonObj.get("options");
		Iterator<Map<String,Object>> optionsIterator = options.iterator();


		Map<String,Object> option_return;

		while (optionsIterator.hasNext()) {

			option_return = new HashMap<String, Object>();


			Map<String,Object> option= optionsIterator.next();


			List<Map<String,String>> product_option_value_return_list = new ArrayList<Map<String,String>>();

			List<Map<String,Object>> product_option_value = (List<Map<String, Object>>) option.get("product_option_value");
			Iterator<Map<String,Object>> product_option_value_no = product_option_value.iterator();
			while (product_option_value_no.hasNext()) {

				Map<String,String> map = new HashMap<String, String>();

				Map<String, Object> product_option_value_no_value =  product_option_value_no.next();
				map.put("product_option_value_id", (String) product_option_value_no_value.get("product_option_value_id"));
				map.put("name", (String) product_option_value_no_value.get("name"));
				map.put("price", (String) product_option_value_no_value.get("price"));

				product_option_value_return_list.add(map);
			}


			option_return.put("product_option_id", option.get("product_option_id"));
			option_return.put("name", option.get("name"));
			option_return.put("product_option_value", product_option_value_return_list);

			options_return.add(option_return);

		}

	}



	public List<Map<String, Object>> getOptions_return() {
		return options_return;
	}



	public void setOptions_return(List<Map<String, Object>> options_return) {
		this.options_return = options_return;
	}


}
