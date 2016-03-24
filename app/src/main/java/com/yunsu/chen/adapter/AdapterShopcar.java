package com.yunsu.chen.adapter;


/**
 * Created by chen on 2016/1/17.
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yunsu.chen.GoodsDetailsActivity;
import com.yunsu.chen.config.Config;
import com.yunsu.chen.handler.YunsuHttp;
import com.yunsu.chen.interf.NetIntf;
import com.yunsu.chen.slide.ImageLoaderUtil;
import com.yunsu.chen.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdapterShopcar extends BaseAdapter {
    Context mContent = null;
    List<Map<String, Object>> listItems = null;

    String product_name;
    private ListView buycar;
    private TextView tolTv;//总价
    private Double price;
    private  Double tolMoney;
    private String url;
    public AdapterShopcar(Context context, List<Map<String, Object>> listItems,TextView tol) {
        this.mContent = context;
        this.listItems = listItems;
        this.tolTv=tol;
    }
    public AdapterShopcar() {

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

            view = LayoutInflater.from(mContent).inflate(R.layout.list_item_buycar, null);
            holder.pnameTV = (TextView) view.findViewById(R.id.tv_shopcar_title);
            holder.describeTV = (TextView) view.findViewById(R.id.tv_shopcar_jianjie);
            holder.priceTV = (TextView) view.findViewById(R.id.tv_shopcar_price);
            holder.imgIV = (ImageView) view.findViewById(R.id.iv_shopcar_img);
            holder.tx=(Button)view.findViewById(R.id.test);
            holder.addbt=(TextView)view.findViewById(R.id.add);
            holder.decbt=(TextView)view.findViewById(R.id.dec);


            view.setTag(holder);
        } else {
            Log.i("not-null", " " + position);

            holder = (ViewHolder) view.getTag();
        }

        final TextView numTV=(TextView)view.findViewById(R.id.num);


        product_name = (String) listItems.get(position).get("name");
        String product_desc = (String) listItems.get(position).get("type");
        String product_price = (String) listItems.get(position).get("price");
        String thumbUrl = (String) listItems.get(position).get("thumb");//图片地址

        holder.pnameTV.setText(product_name);
        holder.describeTV.setText(product_desc);
        holder.priceTV.setText(product_price);

        Log.e("hhhhhh", listItems + "");
        //初始化总价
        String aaa = (String) listItems.get(position).get("total");//id
        aaa=aaa.replace("￥", "");
        double tol1=Double.parseDouble(aaa);
        if (tolMoney==null){
            tolMoney=0.00;
            tolMoney=tolMoney+tol1;
            Log.e("tol", tolMoney + "");
            tolTv.setText(tolMoney+"");
        }else {
            tolMoney=tolMoney+tol1;
            Log.e("tol", tolMoney + "");
            tolTv.setText(tolMoney+"");
        }

        //初始化数量
        String a = (String) listItems.get(position).get("quantity");
        Log.e("aaa", a);
        int num = Integer.parseInt(a);
        numTV.setText(num + "");
        holder.tx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = (String) listItems.get(position).get("cart_id");

               // int b= Integer.parseInt(a);
                Log.e("tx", "" + a);
                HashMap<String ,String > txmap=new HashMap<String ,String>();
                txmap.put("key", a);
                final YunsuHttp  tx=new YunsuHttp(mContent);
                url="index.php?route=moblie/checkout/cart/remove";
               tx.doPost(url, txmap, new NetIntf() {
                   @Override
                   public void getNetMsg() {
                       String httpJson = tx.getJsonString();
                      // Toast.makeText(mContent, httpJson, Toast.LENGTH_LONG).show();

                   }
               });

                listItems.remove(position);
                AdapterShopcar Shopcar = new AdapterShopcar();
                Shopcar.notifyDataSetChanged();

            }
        });

        holder.addbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置数量
                int i=Integer.parseInt(numTV.getText().toString());
                i++;
                numTV.setText(i+"");
                //设置总价
                String a=(String) listItems.get(position).get("price");
                a=a.replace("￥", "");
                double tol1=Double.parseDouble(a);
                tolMoney=tolMoney+tol1;
                tolTv.setText(tolMoney+"");

            }
        });
        holder.decbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int i=Integer.parseInt(numTV.getText().toString());
                //判断不能小于0
                if (i>0) {
                    //设置数量
                    i--;
                    numTV.setText(i+"");
                    //设置总价
                    String a=(String) listItems.get(position).get("price");
                    a=a.replace("￥", "");
                    double tol1=Double.parseDouble(a);
                    tolMoney=tolMoney-tol1;
                    tolTv.setText(tolMoney+"");}

                //tolMoney = tolMoney + price;
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String product_id = (String) listItems.get(position).get("product_id");//id
                Intent intent = new Intent();
                intent.putExtra("product_id", product_id);
                intent.putExtra("name", product_name);
                Toast.makeText(mContent, product_id + "", Toast.LENGTH_LONG).show();
                intent.setClass(mContent, GoodsDetailsActivity.class);
                mContent.startActivity(intent);
                Log.e("test", "事件已经被点击");
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
    public  void touch(int num, final double price, final double tolMoney){



    }
    class ViewHolder {
        TextView pnameTV, describeTV, priceTV;
        ImageView imgIV;
        TextView addbt;//增加商品s
        TextView decbt;//减少商
        Button tx;
    }
}

