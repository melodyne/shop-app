package com.yunsu.chen.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yunsu.chen.slide.Advertisements;

import com.yunsu.chen.slide.RequestManager;
import com.yunsu.chen.R;
import com.yunsu.chen.GoodsActivity;
import com.yunsu.chen.TravelActivity;
import com.yunsu.chen.adapter.AdapterIndex;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.handler.YunsuUI;
import com.yunsu.chen.ui.CustomScrollView;
import com.yunsu.chen.ui.LoadingDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FirstTabFragment extends Fragment {

    private RequestQueue mRequestQueue;
    private CustomScrollView scrollView = null;
    private LinearLayout llAdvertiseBoard;
    private ListView listView;
    private LayoutInflater lyflater;
    private Dialog loadingDialog;

    private TextView menuTV1,menuTV2,menuTV3,menuTV4,menuTV5;
    private ImageView menuIV1,menuIV2,menuIV3,menuIV4,menuIV5;


    private String url_list = Config.basiSurl + "index.php?route=moblie/index";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = inflater.inflate(R.layout.main_fragment1, container, false);

        loadingDialog= LoadingDialog.createLoadingDialog(getActivity(),getString(R.string.data_loading),false);
        loadingDialog.show();

        llAdvertiseBoard = (LinearLayout) view.findViewById(R.id.llAdvertiseBoard);//幻灯片
        //幻灯片设置高度
        WindowManager wm = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);//获取系统管理
        int width = wm.getDefaultDisplay().getWidth();//获取屏幕宽度
        int hight = (width/100)*68;
        llAdvertiseBoard.setLayoutParams(new LinearLayout.LayoutParams(width, hight));

        listView = (ListView) view.findViewById(R.id.listview_index);

        menuIV1=(ImageView)view.findViewById(R.id.iv_menu1);
        menuIV2=(ImageView)view.findViewById(R.id.iv_menu2);
        menuIV3=(ImageView)view.findViewById(R.id.iv_menu3);
        menuIV4=(ImageView)view.findViewById(R.id.iv_menu4);
        menuIV5=(ImageView)view.findViewById(R.id.iv_menu5);

        menuTV1=(TextView)view.findViewById(R.id.tv_menu1);
        menuTV2=(TextView)view.findViewById(R.id.tv_menu2);
        menuTV3=(TextView)view.findViewById(R.id.tv_menu3);
        menuTV4=(TextView)view.findViewById(R.id.tv_menu4);
        menuTV5=(TextView)view.findViewById(R.id.tv_menu5);


        mRequestQueue = Volley.newRequestQueue(getActivity());

        RequestManager.init(getActivity());
        lyflater = LayoutInflater.from(getActivity());

        initViews();

        intScrollView(view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        scrollView.smoothScrollTo(0, 20);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    private void intScrollView(View view) {
        scrollView = (CustomScrollView) view.findViewById(R.id.custom_scroll_view);

        scrollView.smoothScrollTo(0, 20);
        scrollView.setOnRefreshListener(new CustomScrollView.OnRefreshListener() {
            public void onRefresh() {
                new AsyncTask<Void, Void, Void>() {
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(1500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        scrollView.onRefreshComplete();
                    }

                }.execute();
            }
        });

        scrollView.setOnLoadListener(new CustomScrollView.OnLoadListener() {
            public void onLoad() {
                new AsyncTask<Void, Void, Void>() {
                    protected Void doInBackground(Void... params) {
                        try {
                            Thread.sleep(3000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        scrollView.onLoadComplete();
                    }

                }.execute();
            }
        });
    }

    private void initViews() {

        /** volly网络请求  **/
        StringRequest request = new StringRequest(Request.Method.GET, url_list, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonString) {


                List<Map<String, Object>> listBanner = null;     /**  首页幻灯片图组  **/
                List<Map<String, Object>> listbCategory = null;  /** 栏目   **/
                List<Map<String, Object>> listFeatured = null;    /**  首页推荐  **/

                try {
                    JSONObject jsonObj = JSON.parseObject(jsonString);
                    /**  首页幻灯片图组  **/
                    listBanner = (List<Map<String, Object>>) jsonObj.get("banner");
                    /** 栏目   **/
                    listbCategory = (List<Map<String, Object>>) jsonObj.get("category");
                    /**  首页推荐  **/
                    listFeatured = (List<Map<String, Object>>) jsonObj.get("featured");
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "网络出错！", Toast.LENGTH_LONG);
                    System.out.println(e);
                }

                //栏目
                if(listbCategory==null||listbCategory.toString().length()<10) {
                    Log.e("栏目为空：", listbCategory.toString());
                }else {
                    Log.e("栏目：", listbCategory.toString());
                    String categoryName1=listbCategory.get(0).get("name").toString().trim();
                    String categoryName2=listbCategory.get(1).get("name").toString().trim();
                    String categoryName3=listbCategory.get(2).get("name").toString().trim();
                    String categoryName4=listbCategory.get(3).get("name").toString().trim();
                    String categoryName5=listbCategory.get(4).get("name").toString().trim();

                    menuTV1.setText(categoryName1);
                    menuTV2.setText(categoryName2);
                    menuTV3.setText(categoryName3);
                    menuTV4.setText(categoryName4);
                    menuTV5.setText(categoryName5);

                    //栏目类别
                    String leiBie1=listbCategory.get(0).get("class").toString().trim();
                    String leiBie2=listbCategory.get(1).get("class").toString().trim();
                    String leiBie3=listbCategory.get(2).get("class").toString().trim();
                    String leiBie4=listbCategory.get(3).get("class").toString().trim();
                    String leiBie5=listbCategory.get(4).get("class").toString().trim();

                    //栏目类别id
                    String leibieId1=listbCategory.get(0).get("path").toString().trim();
                    String leibieId2=listbCategory.get(1).get("path").toString().trim();
                    String leibieId3=listbCategory.get(2).get("path").toString().trim();
                    String leibieId4=listbCategory.get(3).get("path").toString().trim();
                    String leibieId5=listbCategory.get(4).get("path").toString().trim();

                    //调用设置栏目图片和事件的方法
                    setMenuImg(menuIV1,leibieId1,categoryName1,leiBie1);
                    setMenuImg(menuIV2,leibieId2,categoryName2,leiBie2);
                    setMenuImg(menuIV3,leibieId3,categoryName3,leiBie3);
                    setMenuImg(menuIV4,leibieId4,categoryName4,leiBie4);
                    setMenuImg(menuIV5,leibieId5,categoryName5,leiBie5);
                }

                //首页推荐列表
                if(listFeatured==null||listFeatured.toString().length()<10) {
                    Log.e("空：", listFeatured.toString());
                }else{
                    AdapterIndex myAdapter = new AdapterIndex(getActivity(), listFeatured);
                    listView.setAdapter(myAdapter);
                    try {
                        YunsuUI.setListViewHeightBasedOnChildren(listView);//设置ListView
                    }catch (Exception e){
                        Log.e("UI异常",e+"");
                    }

                }

                //首页幻灯片
                if (listBanner == null||listBanner.toString().length()<10) {
                    /** 无网络预处理  **/
                    Log.e("空：", listBanner.toString());
                    loadSlide();
                }else{

                    llAdvertiseBoard.addView(new Advertisements(getActivity(), true, lyflater, 3000).initView(listBanner));
                }
                scrollView.smoothScrollTo(0, 20);//滑动到顶部
                loadingDialog.dismiss();//取消加载对话框
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                /** 无网络预处理  **/
                loadSlide();
                loadingDialog.dismiss();//取消加载对话框
                Toast.makeText(getActivity(),getString(R.string.err_server),Toast.LENGTH_LONG).show();
            }

        });
        mRequestQueue.add(request);

    }

    //无网络下从本地加载首页幻灯片
    private void loadSlide(){
        List<Map<String, Object>> listBanner = new ArrayList<Map<String,Object>>();
        Map<String,Object> map1 = new HashMap<String,Object>();
        map1.put("product_id", "1");
        map1.put("image", "1");
        map1.put("title","");
        listBanner.add(map1);

        Map<String,Object> map2 = new HashMap<String,Object>();
        map2.put("product_id", "1");
        map2.put("image", "1");
        map2.put("title","");
        listBanner.add(map2);

        Map<String,Object> map3 = new HashMap<String,Object>();
        map3.put("product_id", "1");
        map3.put("image", "1");
        map3.put("title","");
        listBanner.add(map3);


        Map<String,Object> map4 = new HashMap<String,Object>();
        map4.put("product_id", "1");
        map4.put("image", "1");
        map4.put("title","");
        listBanner.add(map4);

        Map<String,Object> map5 = new HashMap<String,Object>();
        map5.put("product_id", "1");
        map5.put("image", "1");
        map5.put("title","");
        listBanner.add(map5);

        llAdvertiseBoard.addView(new Advertisements(getActivity(), true, lyflater, 3000).initView(listBanner));

    }

    //设置栏目图片
    private void setMenuImg(ImageView imageView,final String id, final String name,String liebie){
        if(liebie.equals("view")){
            imageView.setImageResource(R.drawable.menu1_rural);
        }
        if(liebie.equals("hotel")){
            imageView.setImageResource(R.drawable.menu2_hotel);
        }
        if(liebie.equals("restaurant")){
            imageView.setImageResource(R.drawable.menu3_restaurant);
        }
        if(liebie.equals("shop")){
            imageView.setImageResource(R.drawable.menu4_shop);
        }
        if(liebie.equals("travel")){
            imageView.setImageResource(R.drawable.menu5_travel);
        }

        //点击事件
        if(liebie.equals("shop")){
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.setClass(getActivity(), GoodsActivity.class);
                    getActivity().startActivity(intent);
                }
            });
        }else {

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("id", id);
                    intent.putExtra("name", name);
                    intent.setClass(getActivity(), TravelActivity.class);
                    getActivity().startActivity(intent);
                }
            });

        }

    }


}
