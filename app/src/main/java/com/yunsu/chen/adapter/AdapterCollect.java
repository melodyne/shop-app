package com.yunsu.chen.adapter;


/**
 * Created by chen on 2016/1/27.
 * 收藏listview的适配器
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.chen.GoodsActivity;
import com.yunsu.chen.GoodsDetailsActivity;
import com.yunsu.chen.handler.YunsuHttp;
import com.yunsu.chen.interf.NetIntf;
import com.yunsu.chen.slide.ImageLoaderUtil;
import com.yunsu.chen.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterCollect extends BaseAdapter {
    Context mContent = null;
    List<Map<String, Object>> listItems = null;

    String product_name;
    private String url="index.php?route=moblie/account/wishlist";
    public AdapterCollect(Context context, List<Map<String, Object>> listItems) {
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

            view = LayoutInflater.from(mContent).inflate(R.layout.list_item_collect, null);

            holder.pnameTV = (TextView) view.findViewById(R.id.tv_collect_title);
            holder.describeTV = (TextView) view.findViewById(R.id.tv_collect_jianjie);
            holder.priceTV = (TextView) view.findViewById(R.id.tv_collect_price);
            holder.lanmuTV=(TextView)view.findViewById(R.id.tv_collect_lanmu);
            holder.imgIV = (ImageView) view.findViewById(R.id.iv_collect_img);
            holder.decTV=(Button)view.findViewById(R.id.test);

            view.setTag(holder);
        } else {
            Log.i("not-null", " " + position);
            holder = (ViewHolder) view.getTag();
        }

        Log.e("map", listItems + "");
        product_name = (String) listItems.get(position).get("name");
        String product_desc = (String) listItems.get(position).get("type");
        String product_price = (String) listItems.get(position).get("price");
        String lanmu = (String) listItems.get(position).get("type");//所属栏目
        String thumbUrl = (String) listItems.get(position).get("thumb");//图片地址

        holder.pnameTV.setText(product_name);
        holder.describeTV.setText(product_name);
        holder.priceTV.setText(product_price);
        holder.lanmuTV.setText(lanmu);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String product_id = (String) listItems.get(position).get("product_id");//id
                Intent intent = new Intent();
                intent.putExtra("product_id", product_id);
                intent.putExtra("title", mContent.getString(R.string.mycollect));
                Toast.makeText(mContent, product_id + "", Toast.LENGTH_LONG).show();
                intent.setClass(mContent, GoodsDetailsActivity.class);
                mContent.startActivity(intent);

            }
        });

        holder.decTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remove = (String) listItems.get(position).get("product_id");


                //删除收藏

//                网络请求
                final YunsuHttp yunsuHttp=new YunsuHttp(mContent);
               url="index.php?route=moblie/account/wishlist&amp;"+"remove="+remove;
                yunsuHttp.doGet(url, new NetIntf() {
                    @Override
                    public void getNetMsg() {
                        String httpJson = yunsuHttp.getJsonString();
                        System.out.println("删除收藏>>>>>>>" + httpJson);
//                        Log.e("collect", httpJson);
                        //更新ListVie
                        listItems.remove(position);
                        notifyDataSetChanged();//通知ListView数据有更新
                    }
                });
            }
        });

        try {
            // 加载网络图片
            ImageLoaderUtil.getImage(mContent, holder.imgIV, thumbUrl,0, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    class ViewHolder {
        TextView pnameTV, describeTV, priceTV,lanmuTV;
        ImageView imgIV;
        Button decTV;
    }
}

