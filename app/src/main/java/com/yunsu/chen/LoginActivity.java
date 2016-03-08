package com.yunsu.chen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;


public class LoginActivity extends YunsuActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     * Id来标识READ_CONTACTS请求许可。
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
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

        loadingDialog=LoadingDialog.createLoadingDialog(this, "正在登录中……",false);

        requestQueue = Volley.newRequestQueue(getApplicationContext());//加载volley

        // Set up the login form.
        input_user = (AutoCompleteTextView) findViewById(R.id.email);
        populateAutoComplete();

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

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(input_user, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     * 回调收到权限请求时已经完成。
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


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

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            input_password.setError(getString(R.string.error_invalid_password));
            focusView = input_password;
            cancel = true;
        }

        // Check for a valid email address.
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
            //验证通过后，登录
            userLogin();
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            loadingDialog.show();


        }
    }

    private boolean isEmailValid(String user) {
        //TODO: Replace this with your own logic
        //return email.contains("@");
        return user.length() == 11;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 6;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            //登录成功时


        } else {

        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        input_user.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
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

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

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
            showProgress(false);
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
                        Log.e("登录结果", response);
                        Map<String, String> map = new HashMap<String, String>();
                        JSONObject jsonObject = JSONObject.parseObject(response);
                        for (java.util.Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                            System.out.println(entry.getKey() + "-" + entry.getValue() + "\t");
                            map.put(entry.getKey(), entry.getValue().toString());
                        }

                        if (map.get("status").trim().equals("success")) {
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
                            Toast.makeText(LoginActivity.this, map.get("tip"), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("err==", error.getMessage(), error);
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
                        Log.e("登录状态", response);
                        if (response.trim().equals("1")) {

                            Toast.makeText(getApplicationContext(), username + "登录成功！", Toast.LENGTH_LONG).show();
                            //取消加载对话框
                            loadingDialog.dismiss();

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

