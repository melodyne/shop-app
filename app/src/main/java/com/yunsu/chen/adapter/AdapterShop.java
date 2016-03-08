package com.yunsu.chen.adapter;

/**
 * Created by chen on 2016/1/19.
 */
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsu.chen.slide.ImageLoaderUtil;
import com.yunsu.chen.R;
import com.yunsu.chen.GoodsDetailsActivity;

import java.util.List;
import java.util.Map;

public class AdapterShop extends BaseAdapter {
    Context mContent = null;
    List<Map<String, Object>> listItems = null;

    public AdapterShop(Context context, List<Map<String, Object>> listItems) {
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

            view = LayoutInflater.from(mContent).inflate(R.layout.list_item_goods, null);

            holder.pnameTV = (TextView) view.findViewById(R.id.tv_product_name);
            holder.areaTV = (TextView) view.findViewById(R.id.tv_product_area);
            holder.priceTV = (TextView) view.findViewById(R.id.tv_product_pice);
            holder.buynunTV = (TextView) view.findViewById(R.id.tv_product_buynum);
            holder.teseIV = (ImageView) view.findViewById(R.id.img_product);

            view.setTag(holder);
        } else {
            Log.i("not-null", " " + position);

            holder = (ViewHolder) view.getTag();
        }


        String product_name = (String) listItems.get(position).get("name");
        String product_area;
        if(listItems.get(position).get("area").getClass().isInstance(false)){
            product_area="";
        }else{
            product_area = (String) listItems.get(position).get("area");
        }

        String product_price = (String) listItems.get(position).get("price");
        String product_buynum = (String) listItems.get(position).get("buyNum")+"人付款";
        String thumbUrl = (String) listItems.get(position).get("thumb");//图片地址

        holder.pnameTV.setText(product_name);
        if(product_area.equals("")){
            holder.areaTV.setVisibility(View.GONE);
        }else{
            holder.areaTV.setText(product_area);
        }

        holder.priceTV.setText(product_price);
        holder.buynunTV.setText(product_buynum);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String product_id = (String) listItems.get(position).get("product_id");//id
                Intent intent = new Intent();
                intent.putExtra("product_id", product_id);
                intent.setClass(mContent, GoodsDetailsActivity.class);
                mContent.startActivity(intent);

            }
        });


        try {
            // 加载网络图片
            ImageLoaderUtil.getImage(mContent, holder.teseIV, thumbUrl,0, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    class ViewHolder {
        TextView pnameTV, areaTV, priceTV,buynunTV;
        ImageView teseIV;
    }
}


