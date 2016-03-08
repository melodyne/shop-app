package com.yunsu.chen.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yunsu.chen.AboutActivity;
import com.yunsu.chen.LoginActivity;
import com.yunsu.chen.PayActivity;
import com.yunsu.chen.R;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.handler.Login;
import com.yunsu.chen.handler.YunsuHttp;
import com.yunsu.chen.interf.NetIntf;

import java.util.HashMap;

public class FourthTabFragment extends Fragment{

	private View view;
	private LinearLayout aboutBut,exitBut;
	private Button loginBt;
	private YunsuHttp yunsuHttp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view=inflater.inflate(R.layout.main_fragment4, container, false);

		aboutBut=(LinearLayout)view.findViewById(R.id.ly_about_but);
		exitBut=(LinearLayout)view.findViewById(R.id.ly_exit_but);
		loginBt=(Button)view.findViewById(R.id.bt_login);

		//取出用户和密码
		SharedPreferences preferences = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
		if (preferences.getString("user", "").length()>1) {
			//没有cookie执行登录
			SharedPreferences cookiePreferences = getActivity().getSharedPreferences("cookies", Context.MODE_PRIVATE);
			if(cookiePreferences.getString("logintype", "").length()>1){
				exitBut.setVisibility(View.VISIBLE);
				loginBt.setVisibility(View.GONE);
			}
		}


		initViews();
		return view;
	}

	private void initViews(){
		loginBt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent();
				intent.setClass(getActivity(),LoginActivity.class);
				getActivity().startActivityForResult(intent,1);
			}
		});
		aboutBut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent();
				intent.setClass(getActivity(), AboutActivity.class);
				getActivity().startActivity(intent);
			}
		});
		exitBut.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				yunsuHttp = new YunsuHttp(getActivity());
				yunsuHttp.doGet("index.php?route=moblie/account/logout", new NetIntf() {
					@Override
					public void getNetMsg() {
						String jsonStr = yunsuHttp.getJsonString();
						if (!jsonStr.trim().equals("")) {
							loginBt.setText(R.string.login_regist);
							loginBt.setEnabled(true);
							loginBt.setVisibility(View.VISIBLE);
							exitBut.setVisibility(View.GONE);
							SharedPreferences preferences = getActivity().getSharedPreferences("cookies", Context.MODE_PRIVATE);
							SharedPreferences.Editor editor = preferences.edit();
							editor.clear();
							editor.commit();

							Toast.makeText(getActivity(), "退出成功！", Toast.LENGTH_LONG).show();
						}
					}
				});

			}
		});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		try {
			String state=data.getStringExtra("loginstate");
			loginBt.setText(state);
			loginBt.setEnabled(false);
			exitBut.setVisibility(View.VISIBLE);
			switch (requestCode) {
				case 0:
					if (resultCode != Activity.RESULT_OK) {
						System.out.println("返回到tab4结果：" + resultCode);
						System.out.println("返回到tab4数据：" + data);
						return;
					}
					break;
				case 1:
					if (resultCode != Activity.RESULT_OK) {
						System.out.println("返回到tab4结果：" + resultCode);
						System.out.println("返回到tab4数据：" + data);
						return;
					}
					break;

				default:
					break;
			}
		}catch (Exception e){
			Log.e("空指针异常",""+e);
		}
	}
}
