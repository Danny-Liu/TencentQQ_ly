package com.ly.tencentqq.test;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ly.tencentqq.utils.ColorUtil;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by 12758 on 2016/6/9.
 */
public class DragLayout extends FrameLayout {
    private View redView;
    private View blueView;
    private ViewDragHelper viewDragHelper;


    public DragLayout(Context context) {
        this(context, null);
    }

    public DragLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, cb);
    }

    private ViewDragHelper.Callback cb = new ViewDragHelper.Callback() {

        /**
         * 用于判断是否捕获当前child的触摸事件 child: 当前触摸的子View return: true:就捕获并解析 false：不处理
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == blueView || child == redView;
        }

        /**
         * 当view被开始捕获和解析的回调 capturedChild:当前被捕获的子view
         */
        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            // Log.e("tag", "onViewCaptured");
        }

        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界,
         * 返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面;
         * 最好不要返回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        /**
         * 获取view垂直方向的拖拽范围，最好不要返回0
         */
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

        ;

        /**
         * 控制child在水平方向的移动 left:
         * 表示ViewDragHelper认为你想让当前child的left改变的值,left=chile.getLeft()+dx dx:
         * 本次child水平方向移动的距离 return: 表示你真正想让child的left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (left < 0) {
                // 限制左边界
                left = 0;
            } else if (left > (getMeasuredWidth() - child.getMeasuredWidth())) {
                // 限制右边界
                left = getMeasuredWidth() - child.getMeasuredWidth();
            }
            return left;
        }

        /**
         * 控制child在垂直方向的移动
         * top:表示ViewDragHelper认为你想让当前child的top改变的值,top=chile.getTop()+dy
         * dy:本次child垂直方向移动的距离
         * return: 表示你真正想让child的top变成的值
         */
        public int clampViewPositionVertical(View child, int top, int dy) {
            if (top < 0) {
                top = 0;
            } else if (top > getMeasuredHeight() - child.getMeasuredHeight()) {
                top = getMeasuredHeight() - child.getMeasuredHeight();
            }
            return top;
        }

        ;

        /**
         * 当child的位置改变的时候执行,一般用来做其他子View的伴随移动 changedView：位置改变的child
         * left：child当前最新的left
         * top: child当前最新的top
         * dx: 本次水平移动的距离
         * dy: 本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top,
                                          int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == blueView) {
                // blueView移动的时候需要让redView跟随移动
                redView.layout(redView.getLeft() + dx, redView.getTop() + dy, redView.getRight() + dx, redView.getBottom() + dy);
            } else if (changedView == redView) {
                blueView.layout(blueView.getLeft() + dx, blueView.getTop() + dy, blueView.getRight() + dx, blueView.getBottom() + dy);
            }
            //1.计算view移动的百分比
            float fraction = changedView.getLeft() * 1f / (getMeasuredWidth() - changedView.getMeasuredWidth());
            Log.e("tag","fraction:"+fraction);
            //2.执行一系列的伴随动画
            executeAnim(fraction);
        }


        /**
         * 手指抬起的执行该方法， releasedChild：当前抬起的view xvel: x方向的移动的速度 正：向右移动， 负：向左移动
         * yvel: y方向移动的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            int centerLeft = getMeasuredWidth()/2-releasedChild.getMeasuredWidth()/2;
            if(releasedChild.getLeft()<centerLeft){
                //在左半边，应该向左边缓慢移动
                viewDragHelper.smoothSlideViewTo(releasedChild,0,releasedChild.getTop());
                //用这个方法刷新
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }else{
                //在右半边，应该向右边缓慢移动
                viewDragHelper.smoothSlideViewTo(releasedChild,getMeasuredWidth() - releasedChild.getMeasuredWidth(),releasedChild.getTop());
                ViewCompat.postInvalidateOnAnimation(DragLayout.this);
            }
        }
    };

    private void executeAnim(float fraction) {
        //fraction: 0-1;
        //缩放动画
//        ViewHelper.setScaleX(redView, 1 + 0.5f * fraction);
//        ViewHelper.setScaleY(redView, 1 + 0.5f * fraction);
        //旋转
        ViewHelper.setRotation(redView,360*fraction);//围绕z轴旋转
        //ViewHelper.setRotationX(redView,360*fraction);//围绕x轴旋转
        //ViewHelper.setRotationY(redView,360*fraction);//围绕y轴旋转
        //平移
        //ViewHelper.setTranslationX(redView,80*fraction);
        //透明
        //ViewHelper.setAlpha(redView,1-fraction);
        //设置过度颜色的渐变
        redView.setBackgroundColor((Integer) ColorUtil.evaluateColor(fraction, Color.RED,Color.GREEN));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 将触摸事件交给ViewDragHelper来解析处理
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // 让ViewDragHelper帮我们判断是否应该拦截
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int l = getPaddingLeft();
        int t = getPaddingTop();
        redView.layout(l, t, l + redView.getMeasuredWidth(), t
                + redView.getMeasuredHeight());
        blueView.layout(l, redView.getBottom(),
                l + blueView.getMeasuredWidth(), redView.getBottom()
                        + blueView.getMeasuredHeight());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        redView = getChildAt(0);
        blueView = getChildAt(1);
    }

    /**
     * 动画没完成时
     */
    @Override
    public void computeScroll() {
        if(viewDragHelper.continueSettling(true)){
            ViewCompat.postInvalidateOnAnimation(DragLayout.this);
        }
    }
}
