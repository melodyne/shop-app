package com.yunsu.chen.slide;

import java.util.List;
import java.util.Map;



import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import com.yunsu.chen.R;


/**
 * 广告轮播adapter
 *@author dong
 *@data 2015年3月8日下午3:46:35
 *@contance dong854163@163.com
 */
public class AdapterSlide extends PagerAdapter {

	private Context context;
	private List<View> views;
	List<Map<String, Object>> listBanner;
	
	public AdapterSlide() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AdapterSlide(Context context, List<View> views, List<Map<String, Object>> listBanner) {
		this.context = context;
		this.views = views;
		this.listBanner = listBanner;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return views.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView(views.get(position));
	}

	@Override
	public Object instantiateItem(View container, int position) {
		((ViewPager) container).addView(views.get(position), 0);
		final int POSITION = position;
		View view = views.get(position);
		try {
			String img =(String)listBanner.get(position).get("image");
			if (img==null){
				img =(String)listBanner.get(position).get("popup");
			}

			ImageView ivAdvertise = (ImageView) view.findViewById(R.id.ivAdvertise);
			int arrImg[] = {R.drawable.slide1,R.drawable.slide2,R.drawable.slide3,R.drawable.slide4,R.drawable.slide5};
			// 加载网络图片
			ImageLoaderUtil.getImage(context,ivAdvertise,img, arrImg[POSITION], R.drawable.ic_launcher,0,0);
			// item的点击监听
			ivAdvertise.setOnClickListener(new OnClickListener() {
				@SuppressLint("ShowToast")
				@Override
				public void onClick(View v) {
					Toast.makeText(context, POSITION+"", Toast.LENGTH_SHORT).show();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}
	
}
