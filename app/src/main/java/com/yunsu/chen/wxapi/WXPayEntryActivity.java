package com.yunsu.chen.wxapi;






import com.yunsu.chen.R;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.yunsu.chen.ui.YunsuActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class WXPayEntryActivity extends YunsuActivity implements IWXAPIEventHandler{
	

    private IWXAPI api;
	private TextView payResult;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);

		TextView titleTV=(TextView)findViewById(R.id.tv_title_top);
		titleTV.setText("微信支付结果");
		payResult=(TextView)findViewById(R.id.pay_result);

    	api = WXAPIFactory.createWXAPI(this, "wx72596a4779b51c00");
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.d("微信支付", "onPayFinish, errCode = " + resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("支付结果");
			builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
			builder.show();

			if(String.valueOf(resp.errCode)=="0"){
				payResult.setText("支付成功哦！");
			}
			if(String.valueOf(resp.errCode)=="-1"){
				payResult.setText("支付失败！");
			}
			if (String.valueOf(resp.errCode)=="-2"){
				payResult.setText("你已取消支付哦！");
			}
		}
	}

	public void doBack(View view){
		finish();
	}
}