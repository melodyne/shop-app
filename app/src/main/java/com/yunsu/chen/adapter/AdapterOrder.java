package com.yunsu.chen.adapter;


/**
 * Created by chen on 2016/1/28.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.chen.handler.YunsuUI;
import com.yunsu.chen.slide.ImageLoaderUtil;
import com.yunsu.chen.R;

import java.util.List;
import java.util.Map;

public class AdapterOrder extends BaseAdapter {
    Context mContent = null;
    List<Map<String, Object>> listItems = null;

    public AdapterOrder(Context context, List<Map<String, Object>> listItems) {
        this.mContent = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        ViewHolder holder = null;

        // convertView的好处就是不用为所有数据项均生成view对象
        // 比如数据项100个，屏幕只能显示10个，那么只需要返回10个view对象（可能更多），然后修改view对象显示内容（如文字或图片等）， reuse view对象，优化
        if (view == null) {

            Log.i("null", " " + position);

            holder = new ViewHolder();

            view = LayoutInflater.from(mContent).inflate(R.layout.list_item_order, null);

            holder.orderNumTV = (TextView) view.findViewById(R.id.tv_order_num);
            holder.orderStateTV = (TextView) view.findViewById(R.id.tv_order_state);
            holder.moneyTV = (TextView) view.findViewById(R.id.tv_order_money);
            holder.goodsLV=(ListView)view.findViewById(R.id.listview_order_goods);

            view.setTag(holder);
        } else {
            Log.i("not-null", " " + position);
            holder = (ViewHolder) view.getTag();
        }


        String orderNum = (String) listItems.get(position).get("order_no");
        String orderState = (String) listItems.get(position).get("status");
        String money = (String) listItems.get(position).get("total");
        String orderTime = (String) listItems.get(position).get("date_added");
        //String goods = (String) listItems.get(position).get("orderInfo");//图片地址

        /** 商品列表  **/
        List<Map<String,Object>> goodsList = (List<Map<String, Object>>) listItems.get(position).get("orderInfo");
        System.out.println("订单里的商品"+goodsList);
        if(goodsList==null||goodsList.toString().length()<10) {
            Log.e("商品为空：", goodsList.toString());
        }else{
            AdapterOrderGoods myAdapter = new AdapterOrderGoods(mContent,goodsList);
            holder.goodsLV.setAdapter(myAdapter);
            YunsuUI.setListViewHeightBasedOnChildren(holder.goodsLV);
        }

        holder.orderNumTV.setText("订单号"+orderNum);
        holder.orderStateTV.setText(orderState);
        holder.moneyTV.setText(money);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String order_id = (String) listItems.get(position).get("order_id");//id
                Intent intent = new Intent();
                intent.putExtra("shopcar_id", order_id);
                Toast.makeText(mContent,order_id+"",Toast.LENGTH_LONG).show();
                //intent.setClass(mContent, TravelDetailsActivity.class);
                //mContent.startActivity(intent);

            }
        });
//
//
//        try {
//            // 加载网络图片
//            ImageLoaderUtil.getImage(mContent, holder.imgIV, thumbUrl,0, 0, 0, 0);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//

        return view;
    }

    class ViewHolder {
        TextView orderNumTV, orderStateTV, moneyTV;
        ListView goodsLV;
    }
}

