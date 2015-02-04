package com.qiandai.opensource.signaturelibrary.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/*自定义一个MySeekBarDialog*/
@SuppressWarnings("deprecation")
public class FontSizeDialog extends AlertDialog {
	private TextView textView;
	private SeekBar brightBar;//用于显示屏幕亮度
	private OnSeekbarChangedListener mListener;//监听SeekBar事件，比如拖动等
	/*获取监听对象*/
	public OnSeekbarChangedListener getmListener() {
		return mListener;
	}
	/*设置监听对象*/
	public void setmListener(OnSeekbarChangedListener mListener) {
		this.mListener = mListener;
	}

	/*自定义构造函数用于初始化*/

	public FontSizeDialog(Activity activity) {
		super(activity);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT
				);
		
		LinearLayout lin=new LinearLayout(activity);
		lin.setOrientation(LinearLayout.VERTICAL);
		lin.setGravity(Gravity.CENTER_HORIZONTAL);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				200,ViewGroup.LayoutParams.FILL_PARENT);
		textView=new TextView(activity);
		textView.setLayoutParams(p);
		textView.setTextColor(Color.YELLOW);
		brightBar =new SeekBar(activity);
		brightBar.setLayoutParams(p);
		brightBar.setMax(10);
		brightBar.setProgress(0);
		brightBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {


			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				textView.setText(String.valueOf(progress));
				Log.e("onProgressChanged", " "+progress);
				mListener.onChange(progress);

			}
		});
		lin.addView(brightBar, layoutParams);
		lin.addView(textView, layoutParams);
		setView(lin);
	}

	@Override
	public void setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		super.setTitle(title);
	}

	public interface OnSeekbarChangedListener{
		public void onChange(int progress);
	}
}