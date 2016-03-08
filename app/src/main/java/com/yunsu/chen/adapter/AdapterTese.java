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
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsu.chen.slide.ImageLoaderUtil;
import com.yunsu.chen.TravelDetailsActivity;
import com.yunsu.chen.R;

import java.util.List;
import java.util.Map;

public class AdapterTese extends BaseAdapter {
    Context mContent = null;
    List<Map<String, Object>> listItems = null;

    public AdapterTese(Context context, List<Map<String, Object>> listItems) {
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

            view = LayoutInflater.from(mContent).inflate(R.layout.list_item_recommend_tese, null);

            holder.pnameTV = (TextView) view.findViewById(R.id.tv_recommend_pname);
            holder.describeTV = (TextView) view.findViewById(R.id.tv_recommend_describe);
            holder.priceTV = (TextView) view.findViewById(R.id.tv_recommend_price);
            holder.teseIV = (ImageView) view.findViewById(R.id.img_recommend);

            view.setTag(holder);
        } else {
            Log.i("not-null", " " + position);

            holder = (ViewHolder) view.getTag();
        }


        String product_name = (String) listItems.get(position).get("name");
        String product_desc = (String) listItems.get(position).get("description");
        String product_price = (String) listItems.get(position).get("price");
        String thumbUrl = (String) listItems.get(position).get("thumb");//图片地址

        holder.pnameTV.setText(product_name);
        holder.describeTV.setText(product_desc);
        holder.priceTV.setText(product_price);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String product_id = (String) listItems.get(position).get("product_id");//id
                Intent intent = new Intent();
                intent.putExtra("product_id", product_id);
                intent.setClass(mContent, TravelDetailsActivity.class);
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
        TextView pnameTV, describeTV, priceTV;
        ImageView teseIV;
    }
}

