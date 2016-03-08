package com.yunsu.chen;




import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.yunsu.chen.ui.MyScrollLayout;
import com.yunsu.chen.interf.OnViewChangeListener;
import com.yunsu.chen.ui.YunsuActivity;

public class WelcomActivity extends YunsuActivity implements OnViewChangeListener {
	
	private MyScrollLayout mScrollLayout;
	private ImageView[] imgs;
	private int count;
	private int currentItem;
	private Button startBtn;
	private RelativeLayout mainRLayout;
	private LinearLayout pointLLayout;
	private LinearLayout leftLayout;
	private LinearLayout rightLayout;
	private LinearLayout animLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcom);
        initView();
    }
    
	private void initView() {
		mScrollLayout  = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		pointLLayout = (LinearLayout) findViewById(R.id.llayout);
		mainRLayout = (RelativeLayout) findViewById(R.id.mainRLayout);
		startBtn = (Button) findViewById(R.id.startBtn);
		startBtn.setOnClickListener(onClick);
		animLayout = (LinearLayout) findViewById(R.id.animLayout);
		leftLayout  = (LinearLayout) findViewById(R.id.leftLayout);
		rightLayout  = (LinearLayout) findViewById(R.id.rightLayout);
		count = mScrollLayout.getChildCount();
		imgs = new ImageView[count];
		for(int i = 0; i< count;i++) {
			imgs[i] = (ImageView) pointLLayout.getChildAt(i);
			imgs[i].setEnabled(true);
			imgs[i].setTag(i);
		}
		currentItem = 0;
		imgs[currentItem].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}
	
	private View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.startBtn:
				mScrollLayout.setVisibility(View.GONE);
				pointLLayout.setVisibility(View.GONE);
				animLayout.setVisibility(View.VISIBLE);
				mainRLayout.setBackgroundResource(R.drawable.whatsnew_bg);
				Animation leftOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_left);
				Animation rightOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_right);
//				Animation leftOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadedout_to_left_down);
//				Animation rightOutAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fadedout_to_right_down);
				leftLayout.setAnimation(leftOutAnimation);
				rightLayout.setAnimation(rightOutAnimation);
				leftOutAnimation.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						mainRLayout.setBackgroundColor(Color.BLACK);
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						leftLayout.setVisibility(View.GONE);
						rightLayout.setVisibility(View.GONE);
						Intent intent = new Intent(WelcomActivity.this,MainActivity.class);
						WelcomActivity.this.startActivity(intent);
						WelcomActivity.this.finish();
						overridePendingTransition(R.anim.zoom_out_enter, R.anim.zoom_out_exit);
					}
				});
				break;
			}
		}
	};

	@Override
	public void OnViewChange(int position) {
		setcurrentPoint(position);
	}

	private void setcurrentPoint(int position) {
		if(position < 0 || position > count -1 || currentItem == position) {
			return;
		}
		imgs[currentItem].setEnabled(true);
		imgs[position].setEnabled(false);
		currentItem = position;
	}
}