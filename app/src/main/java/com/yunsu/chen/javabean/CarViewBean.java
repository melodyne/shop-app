package com.yunsu.chen.javabean;

import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by chen on 2016/3/24.
 * 购物车界面的组件
 */
public class CarViewBean {
    private TextView editBt;//编辑
    private LinearLayout carBar;
    private TextView tolprice;

    public TextView getEditBt() {
        return editBt;
    }

    public void setEditBt(TextView editBt) {
        this.editBt = editBt;
    }

    public LinearLayout getCarBar() {
        return carBar;
    }

    public void setCarBar(LinearLayout carBar) {
        this.carBar = carBar;
    }

    public TextView getTolprice() {
        return tolprice;
    }

    public void setTolprice(TextView tolprice) {
        this.tolprice = tolprice;
    }
}
