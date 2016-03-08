package com.yunsu.chen;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.chen.fragment.FirstTabFragment;
import com.yunsu.chen.fragment.FourthTabFragment;
import com.yunsu.chen.fragment.SecondTabFragment;
import com.yunsu.chen.fragment.ThirdTabFragment;
import com.yunsu.chen.ui.CustomScrollView;
import com.yunsu.chen.ui.MyRadioButton;
import com.yunsu.chen.ui.YunsuFragmentActivity;


public class MainActivity extends YunsuFragmentActivity
{
    private DrawerLayout mDrawerLayout = null;
    private static FragmentManager fMgr;
    private TextView titleTV,editTV;
    private FirstTabFragment tabFragment1;
    private FourthTabFragment tabFragment4;
    private MyRadioButton tab1,tab2,tab3,tab4;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        titleTV=(TextView)findViewById(R.id.tv_index_title);
        editTV=(TextView)findViewById(R.id.tv_top_edit);
        fMgr = getSupportFragmentManager();

        tab1=(MyRadioButton)findViewById(R.id.rbWeiXin);
        tab2=(MyRadioButton)findViewById(R.id.rbAddress);
        tab3=(MyRadioButton)findViewById(R.id.rbFind);
        tab4=(MyRadioButton)findViewById(R.id.rbMe);

        initFragment();
        dealBottomButtonsClickEvent();

        ImageView imgv = (ImageView) findViewById(R.id.imgv_main);
        imgv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 按钮按下，将抽屉打开
                mDrawerLayout.openDrawer(Gravity.LEFT);

            }
        });
    }

    /**
     * 初始化首个Fragment
     */
    private void initFragment() {
        FragmentTransaction ft = fMgr.beginTransaction();
        FirstTabFragment weiXinFragment = new FirstTabFragment();
        ft.add(R.id.fragmentRoot, weiXinFragment, "weiXinFragment");
        ft.addToBackStack("weiXinFragment");
        ft.commit();
    }
    /**
     * 处理底部点击事件
     */
    private void dealBottomButtonsClickEvent() {
        findViewById(R.id.rbWeiXin).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(fMgr.findFragmentByTag("weiXinFragment")!=null && fMgr.findFragmentByTag("weiXinFragment").isVisible()) {
                    return;
                }
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = fMgr.beginTransaction();
                ft.hide(fMgr.findFragmentByTag("weiXinFragment"));

                tabFragment1 = new FirstTabFragment();
                ft.add(R.id.fragmentRoot, tabFragment1, "WeiXinFragment");
                ft.addToBackStack("WeiXinFragment");
                ft.commit();

                titleTV.setText("云宿商城");
                editTV.setVisibility(View.GONE);

                setClickAble(1);//屏蔽重复点击

                CustomScrollView scrollView = (CustomScrollView) findViewById(R.id.custom_scroll_view);
                scrollView.smoothScrollTo(0, 20);
                //tabFragment1.onActivityResult(1, 1, new Intent());
            }
        });
        findViewById(R.id.rbAddress).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = fMgr.beginTransaction();
                ft.hide(fMgr.findFragmentByTag("weiXinFragment"));
                SecondTabFragment fragment = new SecondTabFragment();
                ft.add(R.id.fragmentRoot, fragment, "AddressFragment");
                ft.addToBackStack("AddressFragment");
                ft.commit();

                titleTV.setText("我的收藏");
                editTV.setVisibility(View.VISIBLE);
                editTV.setText("编辑");

                setClickAble(2);//屏蔽重复点击
            }
        });
        findViewById(R.id.rbFind).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = fMgr.beginTransaction();
                ft.hide(fMgr.findFragmentByTag("weiXinFragment"));

                ThirdTabFragment sf = new ThirdTabFragment();
                ft.add(R.id.fragmentRoot, sf, "AddressFragment");
                ft.addToBackStack("FindFragment");
                ft.commit();

                titleTV.setText("购物车");
                editTV.setVisibility(View.VISIBLE);
                editTV.setText("编辑");

                setClickAble(3);//屏蔽重复点击
            }
        });
        findViewById(R.id.rbMe).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popAllFragmentsExceptTheBottomOne();
                FragmentTransaction ft = fMgr.beginTransaction();
                ft.hide(fMgr.findFragmentByTag("weiXinFragment"));

                tabFragment4 = new FourthTabFragment();
                ft.add(R.id.fragmentRoot, tabFragment4, "MeFragment");
                ft.addToBackStack("MeFragment");
                ft.commit();

                titleTV.setText("我的");
                editTV.setVisibility(View.GONE);

                setClickAble(4);//屏蔽重复点击
            }
        });
    }

    /**
     * 从back stack弹出所有的fragment，保留首页的那个
     */
    public static void popAllFragmentsExceptTheBottomOne() {
        for (int i = 0, count = fMgr.getBackStackEntryCount() - 1; i < count; i++) {
            fMgr.popBackStack();
        }
    }
    //点击返回按钮
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setTitle("确认退出吗？")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        MainActivity.this.finish();

                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();
    }


    public void doCar(View view){
        tab3.setChecked(true);
        setClickAble(3);

        popAllFragmentsExceptTheBottomOne();
        FragmentTransaction ft = fMgr.beginTransaction();
        ft.hide(fMgr.findFragmentByTag("weiXinFragment"));

        ThirdTabFragment sf = new ThirdTabFragment();
        ft.add(R.id.fragmentRoot, sf, "AddressFragment");
        ft.addToBackStack("FindFragment");
        ft.commit();

        titleTV.setText("购物车");
        editTV.setVisibility(View.VISIBLE);
        editTV.setText("编辑");

    }

    //个人信息按钮点击事件
    public void doUserMsg(View view){
        Intent intent=new Intent();
        intent.setClass(this, PersonalActivity.class);
        this.startActivity(intent);

    }

    //订单点击事件
    public void doOrder(View view){
        Intent intent=new Intent();
        intent.setClass(this, OrderActivity.class);
        this.startActivity(intent);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(this,"回到主界面"+requestCode,Toast.LENGTH_LONG).show();
        /*然后在碎片中调用重写的onActivityResult方法*/
        if(tabFragment4!=null) {
            tabFragment4.onActivityResult(requestCode, resultCode, data);
        }

    }

    //设置选种按钮不可以再点击
    private void setClickAble(int i){
        if(i==1){
            tab1.setClickable(false);
        }else{
            tab1.setClickable(true);
        }

        if(i==2){
            tab2.setClickable(false);
        }else{
            tab2.setClickable(true);
        }

        if(i==3){
            tab3.setClickable(false);
        }else{
            tab3.setClickable(true);
        }

        if(i==4){
            tab4.setClickable(false);
        }else{
            tab4.setClickable(true);
        }
    }
}