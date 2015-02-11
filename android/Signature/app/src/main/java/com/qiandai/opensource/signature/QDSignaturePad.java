package com.qiandai.opensource.signature;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.qiandai.opensource.signature.com.qiandai.opensource.signature.utils.SignList;
import com.qiandai.opensource.signaturelibrary.views.SignaturePad;

import java.math.BigDecimal;

/**
 * 继承SignaturePad
 * Created by hcl on 2015/2/10.
 */
public class QDSignaturePad extends SignaturePad {
    private SignList signList;
    public QDSignaturePad(Context context, AttributeSet attrs) {
        super(context, attrs);
        signList=new SignList();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX=event.getX();
        float eventY=event.getY();
        long timeInMilliesLong = System.currentTimeMillis();//得到时间戳
        String timeInMilliesStr =setBigDecimal(timeInMilliesLong);//类型转换
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                signList.getSignList().add((int)eventX);
                signList.getSignList().add((int)eventY);
                signList.getTimeInMilliesList().add(timeInMilliesStr);
                Log.d("onStart", "x:" + eventX + "  y:" + eventY + "  timestamp:" + timeInMilliesStr);
            case MotionEvent.ACTION_MOVE:
                Log.d("onMove", "x:"+eventX+"  y:"+eventY+"  timestamp:"+timeInMilliesStr);
                signList.getSignList().add((int)eventX);
                signList.getSignList().add((int)eventY);
                signList.getTimeInMilliesList().add(timeInMilliesStr);
                break;
            case MotionEvent.ACTION_UP:
                Log.d("onUp", "--------------------------");
                signList.getSignList().add(-1);
                signList.getSignList().add(0);
                signList.getTimeInMilliesList().add(timeInMilliesStr);
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            default:
                return false;
        }
        return super.onTouchEvent(event);
    }

    /**
     * Long 转 String
     * @param str
     * @return
     */
    public String setBigDecimal(Long str){
        BigDecimal bd = new BigDecimal(str);
        System.out.println(bd.toPlainString());
        return bd.toPlainString();
    }

    public SignList getSignList() {
        return signList;
    }

    public void setSignList(SignList signList) {
        this.signList = signList;
    }
}
