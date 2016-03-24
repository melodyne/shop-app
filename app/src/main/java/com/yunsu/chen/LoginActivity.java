package com.yunsu.chen;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.alibaba.fastjson.JSONObject;
import com.android.volley.*;

import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.ui.LoadingDialog;
import com.yunsu.chen.ui.YunsuActivity;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;



public class LoginActivity extends YunsuActivity  {

    private UserLoginTask mAuthTask = null;
    // UI references.
    private AutoCompleteTextView input_user;
    private EditText input_password;
    private RequestQueue requestQueue;
    private TextView titleTV,rigisTV;
    private Dialog loadingDialog;//加载对话框


    String cooke = null;
    String cookie2 = null;
    String user = null;
    String username = "";
    String password = null;
    String loginUrl = Config.basiSurl + "index.php?route=moblie/login";//登录
    String chackLoginUrl = Config.basiSurl + "index.php?route=moblie/login/loginStatus";//检查登录

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loadingDialog=LoadingDialog.createLoadingDialog(this, "正在登录中……", false);

        requestQueue = Volley.newRequestQueue(getApplicationContext());//加载volley

        // Set up the login form.
        input_user = (AutoCompleteTextView) findViewById(R.id.email);

        //标题
        titleTV=(TextView)findViewById(R.id.tv_title_top);
        rigisTV=(TextView)findViewById(R.id.tv_top_right);

        titleTV.setText(R.string.login);
        rigisTV.setText(R.string.regis);


        rigisTV.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, RegisActivity.class);
                LoginActivity.this.startActivity(intent);
            }
        });


        input_password = (EditText) findViewById(R.id.password);
        input_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        input_user.setText(preferences.getString("user", ""));
        input_password.setText(preferences.getString("password", ""));


        Button btn_login = (Button) findViewById(R.id.email_sign_in_button);
        btn_login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


    }

    //-----------------------onCreate()结束------------------------------------------------


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        input_user.setError(null);
        input_password.setError(null);

        //输入框的内容
        user = input_user.getText().toString().trim();
        password = input_password.getText().toString().trim();
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        editor.putString("user", user);
        editor.putString("password", password);
        editor.commit();


        boolean cancel = false;
        View focusView = null;

        // 检查密码
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            input_password.setError(getString(R.string.error_invalid_password));
            focusView = input_password;
            cancel = true;
        }

        // 检查手机号码
        if (TextUtils.isEmpty(user)) {
            input_user.setError(getString(R.string.error_input_empty));
            focusView = input_user;
            cancel = true;
        } else if (!isEmailValid(user)) {
            input_user.setError(getString(R.string.error_phone));
            focusView = input_user;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            userLogin();//登录
            loadingDialog.show();//显示网络等待图标


        }
    }

    private boolean isEmailValid(String user) {
        //TODO: Replace this with your own logic
        //return email.contains("@");
        return user.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >=4;
    }


    /**
     * 代表一个异步登录/注册任务用于验证
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // 账户存在,返回true,如果密码匹配。

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                Intent intent = new Intent();
                intent.putExtra("loginstate", "欢迎回来");
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                input_password.setError(getString(R.string.error_incorrect_password));
                input_password.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    //back

    public void doBack(View view) {

        finish();
    }


    //用户登录
    private void userLogin() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("登录结果>>>>>>>"+response);
                        Map<String, String> map = new HashMap<String, String>();
                        JSONObject jsonObject=null;
                        try {
                            jsonObject = JSONObject.parseObject(response);
                            for (java.util.Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                                System.out.println(entry.getKey() + "-" + entry.getValue() + "\t");
                                map.put(entry.getKey(), entry.getValue().toString());
                            }

                            if (map.get("status").trim().equals("success")) {
                                System.out.println("---------2--------");
                                SharedPreferences preferences = getSharedPreferences("cookies", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.commit();
                                editor.putString("cookie", cooke);
                                editor.putString("logintype","aut");
                                editor.commit();

                                username = map.get("name");
                                getUserMsg();//验证是否登录成功！
                            } else {
                                System.out.println("---------3--------");
                                Toast.makeText(LoginActivity.this, map.get("tip"), Toast.LENGTH_LONG).show();
                            }
                        }catch (Exception e){
                            System.out.println("解析出错>>>>>>>"+e);
                            loadingDialog.dismiss();//取消加载对话框
                            Toast.makeText(LoginActivity.this,R.string.nomatch_login, Toast.LENGTH_LONG).show();
                        }



                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("无法连接到服务器", error.getMessage(), error);
                        Toast.makeText(getApplicationContext(), R.string.err_server, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                //在这里设置需要post的参数
                Map<String, String> map = new HashMap<String, String>();
                Log.e("user", user);
                Log.e("password", password);
                map.put("telephone", user);
                map.put("password", password);
                return map;
            }

            @Override
            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                // TODO Auto-generated method stub
                try {

                    Map<String, String> responseHeaders = response.headers;
                    cooke = responseHeaders.get("Set-Cookie");
                    String dataString = new String(response.data, "UTF-8");
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }

        };
        requestQueue.add(stringRequest);
    }

    //用户登录后获取用户信息
    private void getUserMsg() {
        StringRequest mStringRequest = new StringRequest(chackLoginUrl,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        Log.e("登录状态", response+"ll");
                        if (response.trim().equals("1")) {

                            Toast.makeText(getApplicationContext(), username + "登录成功！", Toast.LENGTH_LONG).show();

                            loadingDialog.dismiss();//取消加载对话框

                            //结束该界面
                            mAuthTask = new UserLoginTask(user, password);
                            mAuthTask.execute((Void) null);
                        } else {
                            Toast.makeText(getApplicationContext(), "登录失败！", Toast.LENGTH_LONG).show();
                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("网络错误", error.toString());
                Toast.makeText(getApplicationContext(), R.string.err_server, Toast.LENGTH_LONG).show();
            }
        }
        ) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences preferences = getSharedPreferences("cookies", Context.MODE_PRIVATE);
                String cookie2 = preferences.getString("cookie", "");
                HashMap localHashMap = new HashMap();
                localHashMap.put("Cookie", cookie2);
                return localHashMap;
            }

            @Override
            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {
                // TODO Auto-generated method stub
                try {
                    Map<String, String> responseHeaders = response.headers;
                    cookie2 = responseHeaders.get("Set-Cookie");
                    String dataString = new String(response.data, "UTF-8");
                    return Response.success(dataString, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                }
            }
        };
        // 3 将StringRequest添加到RequestQueue
        requestQueue.add(mStringRequest);
    }

}

