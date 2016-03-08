package com.yunsu.chen.adapter;



import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunsu.chen.GoodsDetailsActivity;
import com.yunsu.chen.slide.ImageLoaderUtil;
import com.yunsu.chen.TravelDetailsActivity;
import com.yunsu.chen.R;

import java.util.List;
import java.util.Map;

public class AdapterIndex extends BaseAdapter {
    private Context mContent = null;
    private List<Map<String, Object>> listItems = null;

    public AdapterIndex(Context context, List<Map<String, Object>> listItems) {
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
    public View  getView(final int position, View view, ViewGroup parent){
        ViewHolder holder = null;

        // convertView的好处就是不用为所有数据项均生成view对象
        // 比如数据项100个，屏幕只能显示10个，那么只需要返回10个view对象（可能更多），然后修改view对象显示内容（如文字或图片等）， reuse view对象，优化
        if (view == null) {

            Log.i("null", " " + position);

            holder = new ViewHolder();

            view = LayoutInflater.from(mContent).inflate(R.layout.list_item_index, null);

            holder.title = (TextView)view.findViewById(R.id.tv_index_title);
            holder.addrTextView = (TextView)view.findViewById(R.id.tv_index_addr);
            holder.jianjieTextView = (TextView)view.findViewById(R.id.tv_index_jianjie);
            holder.img_index = (ImageView)view.findViewById(R.id.img_index);
            holder.lanMuImageView = (ImageView)view.findViewById(R.id.iv_index_category);

            view.setTag(holder);
        } else {
            Log.i("not-null", " " + position);

            holder = (ViewHolder)view.getTag();
        }


        //标题
        String product_name =(String)listItems.get(position).get("product_name");
        if(product_name==null){
            product_name=(String)listItems.get(position).get("name");
        }
        holder.title.setText(product_name);

        //地点
        String addr =(String)listItems.get(position).get("area");
        holder.addrTextView.setText(addr);

        //地点
        String intro =(String)listItems.get(position).get("intro");
        holder.jianjieTextView.setText(intro);


        //栏目图片设置，事件设置
        String lanMu =(String)listItems.get(position).get("class");
        if(lanMu.trim().equals("view")){
            holder.lanMuImageView.setImageResource(R.drawable.menu1_rural);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String product_id = (String) listItems.get(position).get("product_id");
                    Intent intent = new Intent();
                    intent.putExtra("product_id", product_id);
                    intent.setClass(mContent, TravelDetailsActivity.class);
                    mContent.startActivity(intent);

                }
            });
        }
        if(lanMu.trim().equals("hotel")){
            holder.lanMuImageView.setImageResource(R.drawable.menu2_hotel);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String product_id = (String) listItems.get(position).get("product_id");
                    Intent intent = new Intent();
                    intent.putExtra("product_id", product_id);
                    intent.setClass(mContent, GoodsDetailsActivity.class);
                    mContent.startActivity(intent);

                }
            });
        }
        if(lanMu.trim().equals("restaurant")){
            holder.lanMuImageView.setImageResource(R.drawable.menu3_restaurant);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String product_id = (String) listItems.get(position).get("product_id");
                    Intent intent = new Intent();
                    intent.putExtra("product_id", product_id);
                    intent.setClass(mContent, GoodsDetailsActivity.class);
                    mContent.startActivity(intent);

                }
            });
        }
        if(lanMu.trim().equals("shop")){
            holder.lanMuImageView.setImageResource(R.drawable.menu4_shop);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String product_id = (String) listItems.get(position).get("product_id");
                    Intent intent = new Intent();
                    intent.putExtra("product_id", product_id);
                    intent.setClass(mContent, GoodsDetailsActivity.class);
                    mContent.startActivity(intent);

                }
            });
        }
        if(lanMu.trim().equals("travel")){
            holder.lanMuImageView.setImageResource(R.drawable.menu5_travel);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String product_id = (String) listItems.get(position).get("product_id");
                    Intent intent = new Intent();
                    intent.putExtra("product_id", product_id);
                    intent.setClass(mContent, TravelDetailsActivity.class);
                    mContent.startActivity(intent);

                }
            });
        }


        //大图设置
        String thumbUrl =(String)listItems.get(position).get("thumb");//图片地址
        try {
            // 加载网络图片
            ImageLoaderUtil.getImage(mContent, holder.img_index, thumbUrl,0, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    class ViewHolder {
        TextView title,addrTextView,jianjieTextView;
        ImageView img_index,lanMuImageView;
    }
}

