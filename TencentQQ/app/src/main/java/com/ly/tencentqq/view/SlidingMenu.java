package com.ly.tencentqq.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.ly.tencentqq.utils.ColorUtil;
import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.view.ViewHelper;

/**
 * Created by 12758 on 2016/6/9.
 */
public class SlidingMenu extends FrameLayout {

    private View menuView;
    private View mainView;
    private ViewDragHelper viewDragHelper;
    private int width;
    private float dragRange;//拖拽范围
    FloatEvaluator floatEvaluator;
    IntEvaluator intEvaluator;

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //定义状态常量
    public enum DragState {
        Open, Close;
    }

    public DragState getCurrentState() {
        return currentState;
    }

    private DragState currentState = DragState.Close;//默认是关闭

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, callback);
        floatEvaluator = new FloatEvaluator();
        intEvaluator = new IntEvaluator();
    }

    private ViewDragHelper.Callback callback = new ViewDragHelper.Callback() {
        /**
         * 用于判断是否捕获当前child的触摸事件 child: 当前触摸的子View return: true:就捕获并解析 false：不处理
         */
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == menuView || child == mainView;
        }

        /**
         * 获取view水平方向的拖拽范围,但是目前不能限制边界,
         * 返回的值目前用在手指抬起的时候view缓慢移动的动画世界的计算上面;
         * 最好不要返回0
         */
        @Override
        public int getViewHorizontalDragRange(View child) {
            return (int) dragRange;
        }

        /**
         * 控制child在水平方向的移动 left:
         * 表示ViewDragHelper认为你想让当前child的left改变的值,left=child.getLeft()+dx dx:
         * 本次child水平方向移动的距离 return: 表示你真正想让child的left变成的值
         */
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == mainView) {
                if (left < 0) left = 0;
                if (left > dragRange) left = (int) dragRange;
            } /*else if (child == menuView) {
                left = left - dx;
            }*/
            return left;
        }

        /**
         * 当child的位置改变的时候执行,一般用来做其他子View的伴随移动 changedView：位置改变的child
         * left：child当前最新的left
         * top: child当前最新的top
         * dx: 本次水平移动的距离
         * dy: 本次垂直移动的距离
         */
        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == menuView) {
                //想要menuView不动，而mainView动起来，我们需要手动固定meanView
                menuView.layout(0, 0, menuView.getMeasuredWidth(), menuView.getMeasuredHeight());
                Log.e("tag", "dx:" + dx);
                //我们发现menuView移动，让mainView没有界限了，所以在这里限制一下mainView的left,刚刚只是在上面方法限制了left,这里又变了
                int newLeft = mainView.getLeft() + dx;
                if (newLeft < 0) newLeft = 0;
                if (newLeft > dragRange) newLeft = (int) dragRange;
                mainView.layout(newLeft, mainView.getTop() + dy, newLeft + mainView.getMeasuredWidth(), mainView.getBottom() + dy);
            }
            //1.计算滑动的百分比
            float fraction = mainView.getLeft() / dragRange;
            //2.执行伴随动画
            executionAnim(fraction);
            //3.更改状态回调方法
            if (fraction == 0 && currentState != DragState.Close) {//当前状态不等于close,如果等于的话  就不需要回调了
                //更改状态为关闭,并回调关闭的方法
                currentState = DragState.Close;
                if (listener != null) listener.onClose();
            } else if (fraction == 1f && currentState != DragState.Open) {
                //更改状态为打开,并回调打开的方法
                currentState = DragState.Open;
                if (listener != null) listener.onOpen();
            }
            if (listener != null) listener.onDraging(fraction);

        }

        /**
         * 手指抬起的执行该方法， releasedChild：当前抬起的view xvel: x方向的移动的速度 正：向右移动， 负：向左移动
         * yvel: y方向移动的速度
         */
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (mainView.getLeft() < dragRange / 2) {
                //在左半边
                close();
            } else {
                //在右半边
                open();
            }
            //速度>200，并且不是打开的状态 我才打开
            if (xvel > 200 && currentState != DragState.Open) open();
            else if (xvel < -200 && currentState != DragState.Close) close();

        }
    };

    public void open() {
        viewDragHelper.smoothSlideViewTo(mainView, (int) dragRange, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
    }

    public void close() {
        viewDragHelper.smoothSlideViewTo(mainView, 0, mainView.getTop());
        ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
    }

    /**
     * 执行伴随动画
     *
     * @param fraction
     */
    private void executionAnim(float fraction) {
        //缩放动画
        ViewHelper.setScaleX(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
        ViewHelper.setScaleY(mainView, floatEvaluator.evaluate(fraction, 1f, 0.8f));
        //移动menuView
        ViewHelper.setTranslationX(menuView, intEvaluator.evaluate(fraction, -menuView.getMeasuredWidth() / 2, 0));
        //放大menuView
        ViewHelper.setScaleX(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));
        ViewHelper.setScaleY(menuView, floatEvaluator.evaluate(fraction, 0.5f, 1f));

        //改变menuView的透明度
        ViewHelper.setAlpha(menuView, floatEvaluator.evaluate(fraction, 0.3f, 1f));
        //给SlidingMenu的背景添加黑色的遮罩效果
        getBackground().setColorFilter((Integer) ColorUtil.evaluateColor(fraction, Color.BLACK, Color.TRANSPARENT), PorterDuff.Mode.SRC_OVER);
    }

    /**
     * 重写computeScroll()的原因
     * <p>
     * 调用startScroll()是不会有滚动效果的，只有在computeScroll()获取滚动情况，做出滚动的响应
     */
    @Override
    public void computeScroll() {

        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(SlidingMenu.this);
        }
    }

    private OnDragStateChangeListener listener;

    public void setOnDragStateChangeListener(OnDragStateChangeListener listener) {
        this.listener = listener;
    }

    public interface OnDragStateChangeListener {
        /**
         * 打开的回调
         */
        void onOpen();

        /**
         * 关闭的回调
         */
        void onClose();

        /**
         * 正在拖拽中的回调
         */
        void onDraging(float fraction);
    }

    /**
     * 该方法在onMeasure执行完后执行，那么可以在该方法中初始化自己和子view的宽高
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getMeasuredWidth();
        dragRange = width * 0.7f;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //把事件处理交给ViewDragHelper
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //让viewdragHelper来判断是否需要拦截事件
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //处理下
        if (getChildCount() != 2) {
            throw new IllegalArgumentException("SlidingMenu only have 2 children");
        }
        menuView = getChildAt(0);
        mainView = getChildAt(1);
    }
}
