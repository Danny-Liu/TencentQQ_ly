package com.ly.tencentqq;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.ViewPropertyAnimatorCompatSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.CycleInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ly.tencentqq.utils.Constant;
import com.ly.tencentqq.view.MyLinearLayout;
import com.ly.tencentqq.view.SlidingMenu;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.Random;

public class QQActivity extends AppCompatActivity {

    private ListView menu_listview, main_listview;
    private SlidingMenu slidingMenu;
    private ImageView iv_head;
    private MyLinearLayout my_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initData();

    }

    private void initData() {
        menu_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.sCheeseStrings) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView text = (TextView) super.getView(position, convertView, parent);
                text.setTextColor(Color.WHITE);
                return text;
            }
        });
        main_listview.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Constant.NAMES){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = convertView==null?super.getView(position, convertView, parent):convertView;
                //先缩小view
                ViewHelper.setScaleX(view,0.5f);
                ViewHelper.setScaleY(view,0.5f);
                //再以属性动画放大
                ViewPropertyAnimator.animate(view).scaleX(1f).setDuration(350).start();
                ViewPropertyAnimator.animate(view).scaleY(1f).setDuration(350).start();
                return view;
            }
        });
        slidingMenu.setOnDragStateChangeListener(new SlidingMenu.OnDragStateChangeListener() {
            @Override
            public void onOpen() {
//                Log.e("tag", "onOpen");
                menu_listview.smoothScrollToPosition(new Random().nextInt(menu_listview.getCount()));
            }

            @Override
            public void onClose() {
//                Log.e("tag", "onClose");
                ViewPropertyAnimator.animate(iv_head)
                        .setInterpolator(new CycleInterpolator(4))
                        .translationX(15)
                        .setDuration(500)
                        .start();
            }


            @Override
            public void onDraging(float fraction) {
                //Log.e("tag", "onDraging");
                ViewHelper.setAlpha(iv_head, 1 - fraction);
            }
        });
        my_layout.setSlidingMenu(slidingMenu);
    }

    private void initViews() {
        setContentView(R.layout.activity_qq);
        menu_listview = (ListView) findViewById(R.id.menu_listview);
        main_listview = (ListView) findViewById(R.id.main_listview);
        slidingMenu = (SlidingMenu) findViewById(R.id.sliding_menu);
        my_layout = (MyLinearLayout) findViewById(R.id.my_layout);
        iv_head = (ImageView) findViewById(R.id.iv_head);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }


        });
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    Button button;
}
