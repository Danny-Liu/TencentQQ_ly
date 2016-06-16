package com.ly.tencentqq.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by 12758 on 2016/6/13.
 */
public class SlidingMenuTest extends FrameLayout {

    private ViewDragHelper viewDragHelper;
    private View menuView;
    private View mainView;
    private float dragRange;//拖拽范围


    public SlidingMenuTest(Context context) {
        this(context, null);
    }

    public SlidingMenuTest(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenuTest(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, cb);
    }

    private ViewDragHelper.Callback cb = new ViewDragHelper.Callback() {

        /**
         *捕获child的触摸事件  true:拦截并且解析
         * @param child
         * @param pointerId
         * @return
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == menuView || child == mainView;
        }

        /**
         * 获取子view水平方向的拖拽范围  但是目前不能限制边界  一般用于当手指抬起的时候  view缓慢移动动画计算
         * 不要返回0
         * @param child
         * @return
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        /**
         * 控制子view在水平方向的移动
         * @param child
         * @param left
         * @param dx
         * @return
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //在这里可以限制left的值
            if (child == mainView) {
                if (left < 0) left = 0;
                if (left > dragRange) left = (int) dragRange;
            }
            //不能这么写   移动mainView要做伴随移动
            /* else if (child == menuView) {
                //menuView应该移动不了   所以这么写
                left = left - dx;
            }*/
            return left;
        }

        /**
         * 当child位置改变的时候执行，一般用来做其他子view的伴随移动
         * @param changedView
         * @param left child最新的left
         * @param top  child最新的top
         * @param dx   本次水平移动了dx
         * @param dy   本次垂直移动了dy
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if(changedView==menuView){
                menuView.layout(0,0,menuView.getMeasuredWidth(),menuView.getMeasuredHeight());
                //这里menuView让mainView没有界限了，所以必须在这里限制

                mainView.layout(mainView.getLeft()+dx,mainView.getTop()+dy,mainView.getRight()+dx,mainView.getBottom()+dy);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
        }
    };

    /**
     * onMeasure方法执行后调用  可以初始化自己和子view的宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int width = getMeasuredWidth();
        dragRange = width * 0.7f;
    }

    //使用ViewDragHelper必须要重写onTouchEvent和onInterceptTouchEvent,交给ViewDragHelper处理事件和判断是否需要拦截事件


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件交给viewDragHelper处理
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * xml解析完成后   会执行该方法  所以可以用来初始化布局
     * 因为我们做的事类似qq的效果  所以只能有两个子view
     * 手动抛异常 来处理下
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SlidingMenu only has 2 children");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }


}
