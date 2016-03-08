package com.yunsu.chen.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yunsu.chen.R;
import com.yunsu.chen.adapter.AdapterCollect;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.handler.Login;
import com.yunsu.chen.interf.NetIntf;
import com.yunsu.chen.ui.LoadingDialog;

import java.util.List;
import java.util.Map;

public class SecondTabFragment extends Fragment{

	private RequestQueue mRequestQueue;
	private Login login;
	private ListView listView;
	private Dialog loadingDialog;

	private String url= Config.basiSurl+"index.php?route=moblie/account/wishlist";


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.main_fragment2, container, false);

		loadingDialog= LoadingDialog.createLoadingDialog(getActivity(),getString(R.string.data_loading),true);
		loadingDialog.show();
		loadingDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND | WindowManager.LayoutParams.FLAG_DIM_BEHIND);

		mRequestQueue =  Volley.newRequestQueue(getActivity());

		listView=(ListView)view.findViewById(R.id.listview_collect);

		login=new Login(getActivity());
		login.chackLogin(new NetIntf() {
			@Override
			public void getNetMsg() {
				initViews();
				System.out.println("函数成功回掉");
			}
		});

		return view;
	}


	private void initViews(){
		//编辑事件绑定
		try{
			TextView edit=(TextView)getActivity().findViewById(R.id.tv_top_edit);
			edit.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Toast.makeText(getActivity(),"编辑",Toast.LENGTH_LONG).show();
				}
			});
		}catch (Exception e){
			Log.e("编辑按钮",e.toString());
		}


		StringRequest mStringRequest = new StringRequest(url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println("======收藏========" + response);
						JSONObject jsonObject = JSONObject.parseObject(response);

						/** 收藏列表  **/
						List<Map<String,Object>> listPosition = (List<Map<String, Object>>) jsonObject.get("products");
						if(listPosition==null||listPosition.toString().length()<10) {
							Log.e("收藏为空：", listPosition.toString());
						}else{
							Log.e("收藏：", listPosition.toString());
							AdapterCollect myAdapter = new AdapterCollect(getActivity(),listPosition);
							listView.setAdapter(myAdapter);
						}

						loadingDialog.dismiss();
					}

				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				loadingDialog.dismiss();
				//Log.i("Hanjh", "get请求错误:" + error.toString());
				Toast.makeText(getActivity(),getString(R.string.err_server),Toast.LENGTH_LONG).show();
			}
		}){
			//传登录Cookie
			public Map<String, String> getHeaders() throws AuthFailureError {
				return login.getCookie();
			}

		};
		// 3 将StringRequest添加到RequestQueue
		mRequestQueue.add(mStringRequest);
	}

}
