package com.yunsu.chen.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yunsu.chen.R;
import com.yunsu.chen.adapter.AdapterShopcar;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.handler.Login;
import com.yunsu.chen.interf.NetIntf;
import com.yunsu.chen.listview.WaterDropListView;
import com.yunsu.chen.ui.LoadingDialog;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThirdTabFragment extends Fragment implements WaterDropListView.IWaterDropListViewListener{

	private Login login;
	private RequestQueue queue;
	private  TextView tol;
	private String url;//接口地址
	private Dialog loadingDialog;
	private View view;
	private double num,num1;
	private String a;
	private List<Map<String, Object>> listPosition;
	private WaterDropListView waterDropListView;
	private AdapterShopcar myAdapter;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what){
				case 1:
					waterDropListView.stopRefresh();
					break;
				case 2:
					waterDropListView.stopLoadMore();
					break;
			}

		}
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 view=inflater.inflate(R.layout.main_fragment3, container, false);

		loadingDialog= LoadingDialog.createLoadingDialog(getActivity(), getString(R.string.data_loading), true);
		loadingDialog.show();

		queue= Volley.newRequestQueue(getActivity());//初始化Volly框架
		url= Config.basiSurl+"index.php?route=moblie/checkout/cart";

		waterDropListView = (WaterDropListView)view.findViewById(R.id.listview_car);
		waterDropListView.setPullLoadEnable(true);
		waterDropListView.setWaterDropListViewListener(this);//刷新加载监听
		tol=(TextView)view.findViewById(R.id.tol);

		login=new Login(getActivity());
		login.chackLogin(new NetIntf() {
			@Override
			public void getNetMsg() {
				initialView();//初始化组件
				System.out.println("回调成功");

			}
		});




		return view;
	}


	@Override
	public void onRefresh() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					handler.sendEmptyMessage(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public void onLoadMore() {
		ExecutorService executorService = Executors.newSingleThreadExecutor();
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
					handler.sendEmptyMessage(2);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

//	public void init(){
//		addbt=(TextView) view.findViewById(R.id.add);
//		decbt=(TextView)view.findViewById(R.id.dec);
//		tol=(TextView)view.findViewById(R.id.tol);
//
//		addbt.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				if (num<50) {
//					num++;
//					tolMoney=price*num;
//					numTV.setText(num + "");
//					tol.setText("总计：￥"+tolMoney);
//				}else {
//					num=50;
//					tolMoney=price*num;
//					numTV.setText(num + "");
//					tol.setText("总计：￥"+tolMoney);
//				}
//			}
//		});
//
//		decbt.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				if (num > 1) {
//					num--;
//					tolMoney = price * num;
//					numTV.setText(num + "");
//					tolTV.setText("总计：￥" + tolMoney);
//				} else {
//					num = 1;
//					tolMoney = price * num;
//					numTV.setText(num + "");
//					tolTV.setText("总计：￥" + tolMoney);
//				}
//			}
//		});
//	}

	private void initialView() {
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
		/** volly网络处理 **/

		StringRequest mStringRequest = new StringRequest(url,
				new Response.Listener<String>() {

					@Override
					public void onResponse(String response) {
						System.out.println("购物车========" + response);
						JSONObject jsonObject=null;
						try{
							jsonObject = JSONObject.parseObject(response);


						if (jsonObject!=null){

							String state=jsonObject.getString("status");
							if (state.equals("success")){
								/** 购物车列表  **/
								 listPosition = (List<Map<String, Object>>) jsonObject.get("products");
								if (listPosition == null || listPosition.toString().length() < 10) {
									Log.e("购物车为空：", listPosition.toString());
								} else {

									Log.e("shopcar", listPosition.toString());
									 myAdapter = new AdapterShopcar(getActivity(), listPosition,tol);
									waterDropListView.setAdapter(myAdapter);

								}
							}else if(state.equals("empty")){
								Toast.makeText(getActivity(),"购物车为空！",Toast.LENGTH_LONG).show();
							}else {
								String tip=jsonObject.getString("tip");
								Toast.makeText(getActivity(),tip,Toast.LENGTH_LONG).show();
							}

						}
						}catch (Exception e){
							Log.e("解析出错",e.toString());
						}
						loadingDialog.dismiss();
					}

				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				loadingDialog.dismiss();
				Log.i("Hanjh", "get请求错误:" + error.toString());
				Toast.makeText(getActivity(),getString(R.string.err_server),Toast.LENGTH_LONG).show();
			}
		}
		) {
			//传递cookie
			public Map<String, String> getHeaders() throws AuthFailureError {

				return login.getCookie();
			}
		};
		// 将StringRequest添加到RequestQueue
		queue.add(mStringRequest);
	}

}
