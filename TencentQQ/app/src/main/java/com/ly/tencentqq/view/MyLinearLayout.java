package com.ly.tencentqq.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

/**
 * 解决当slidingMenu打开的时候，拦截并消费掉触摸事件,然后他的子控件就不能触摸了
 * Created by 12758 on 2016/6/10.
 */
public class MyLinearLayout extends LinearLayout {
    public MyLinearLayout(Context context) {
        super(context);
    }

    public MyLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private SlidingMenu slidingMenu;

    public void setSlidingMenu(SlidingMenu slidingMenu) {
        this.slidingMenu = slidingMenu;
    }

    /**
     * 拦截触摸事件
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (slidingMenu != null && slidingMenu.getCurrentState() == SlidingMenu.DragState.Open) {
            //如果slidingMenu打开则应该拦截并消费事件
            //return true,事件传给onTouchEvent
            return true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (slidingMenu != null && slidingMenu.getCurrentState() == SlidingMenu.DragState.Open) {
            Log.e("tag1","currentState:"+slidingMenu.getCurrentState());
            //如果slidingMenu打开则应该拦截并消费事件
            if(event.getAction()==MotionEvent.ACTION_UP){
                //抬起要关闭slidingmenu
                slidingMenu.close();
            }
            //return true,事件传给onTouchEvent
            return true;

        }
        return super.onTouchEvent(event);
    }
}
