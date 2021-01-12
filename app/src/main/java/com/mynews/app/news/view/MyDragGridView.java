package com.mynews.app.news.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.mynews.app.news.R;
import com.mynews.app.news.page.Adapter.MyGridAdapter;

/**
 * 项目名称：New_Android
 * 类名：MyDragGridView
 * 创建人：Heaven.li
 * 创建时间：2021/1/4
 * 备注：
 */
public class MyDragGridView extends GridView implements AdapterView.OnItemLongClickListener{

        private static final String TAG = "DragGridView";
        private WindowManager mWindowManager;

        private static final int MODE_DRAG = 1;
        private static final int MODE_NORMAL = 2;

        private int mode = MODE_NORMAL;
        private View view;
        private View dragView;
        // 要移动的item原先位置
        private int index;

        private int position;
        private int tempPosition;
        private boolean isTouch = false;

        private WindowManager.LayoutParams layoutParams;
        // view的x差值
        private float mX;
        // view的y差值
        private float mY;
        // 手指按下时的x坐标(相对于整个屏幕)
        private float mWindowX;
        // 手指按下时的y坐标(相对于整个屏幕)
        private float mWindowY;

        private View mStartDragItemView = null;
        private int downRawX;
        private int downRawY;
        private int mDownX;
        private int mDownY;
        private int moveX;
        private int moveY;
        private int mOffset2Top;
        private int mOffset2Left;
        private int mPoint2ItemTop;
        private int mPoint2ItemLeft;
        private int mDragPosition;
        private int mStatusHeight;

        public MyDragGridView(Context context) {
            this(context, null);
        }

        public MyDragGridView(Context context, AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public MyDragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            setOnItemLongClickListener(this);
            mStatusHeight = getStatusHeight(context); // 获取状态栏的高度
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mWindowX = ev.getRawX();
                    mWindowY = ev.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                    MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
        }

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            if (mode == MODE_DRAG) {
                return false;
            }
            isTouch = true;
            this.view = view;
            this.index = position;
            this.position = position;
            this.tempPosition = position;
            mX = mWindowX - view.getLeft() - this.getLeft();
            mY = mWindowY - view.getTop() - this.getTop();
            // 如果是Android 6.0 要动态申请权限
            if (Build.VERSION.SDK_INT >= 23) {
                if (Settings.canDrawOverlays(getContext())) {
                    initWindow();
                } else {
                    // 跳转到悬浮窗权限管理界面
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    getContext().startActivity(intent);
                }
            } else {
                // 如果小于Android 6.0 则直接执行
                initWindow();
            }
            return true;
        }

        /**
         * 初始化window
         */
        private void initWindow() {
            TextView tv_text = null;
            ImageView iv_ico = null;
            ImageView ivico = null;
            if (dragView == null) {
                dragView = View.inflate(getContext(), R.layout.item_gridview, null);
                tv_text = dragView.findViewById(R.id.tv_name);
                iv_ico = dragView.findViewById(R.id.iv_ico);
                ivico = view.findViewById(R.id.iv_ico);
                iv_ico.setBackground(ivico.getBackground());
                tv_text.setText(((TextView) view.findViewById(R.id.tv_name)).getText());
            }
            if (layoutParams == null) {
                layoutParams = new WindowManager.LayoutParams();
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                layoutParams.format = PixelFormat.RGBA_8888;
                layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;  //悬浮窗的行为，比如说不可聚焦，非模态对话框等等
                layoutParams.width = view.getWidth();
                layoutParams.height = view.getHeight();

                layoutParams.x = mDownX - mPoint2ItemLeft + mOffset2Left;
                layoutParams.y = mDownY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
            }
            startShakeByViewAnim(view,ivico,1.5f,1.0f,2,100);
        }



        private void startShakeByViewAnim(View mView,View view, float scaleSmall, float scaleLarge, float shakeDegrees, long duration) {
            if (view == null) {
                return;
            }
            //TODO 验证参数的有效性

            //由小变大
            Animation scaleAnim = new ScaleAnimation(scaleSmall, scaleLarge, scaleSmall, scaleLarge);
            //从左向右
            Animation rotateAnim = new RotateAnimation(-shakeDegrees, shakeDegrees, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

            scaleAnim.setDuration(duration);
            rotateAnim.setDuration(duration / 10);
            rotateAnim.setRepeatMode(Animation.REVERSE);
            rotateAnim.setRepeatCount(10);

            AnimationSet smallAnimationSet = new AnimationSet(false);
            smallAnimationSet.addAnimation(scaleAnim);
            smallAnimationSet.addAnimation(rotateAnim);
            view.startAnimation(smallAnimationSet);
            smallAnimationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mode = MODE_DRAG;
                    mWindowManager.addView(dragView, layoutParams);
                    mView.setVisibility(INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            switch(ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getParent().requestDisallowInterceptTouchEvent(true);
                    //todo 记录点击初始位置
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (isTouch) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    downRawX = (int)ev.getRawX();
                    downRawY = (int)ev.getRawY();

                    mDownX = (int) ev.getX();
                    mDownY = (int) ev.getY();

                    mDragPosition = pointToPosition(mDownX, mDownY);
                    mStartDragItemView = getChildAt(mDragPosition
                            - getFirstVisiblePosition());
                    if (mStartDragItemView != null){
                        mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                        mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();
                    }
                    mOffset2Top = (int) (downRawY - mDownY);
                    mOffset2Left = (int) (downRawX - mDownX);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mode == MODE_DRAG) {
                        updateWindow(ev);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mode == MODE_DRAG) {
                        isTouch = false;
                        closeWindow(ev.getX(), ev.getY());
                    }
                    break;
            }
            return super.onTouchEvent(ev);
        }

        /**
         * 触摸移动时，window更新
         *
         * @param ev
         */
        private void updateWindow(MotionEvent ev) {
            if (mode == MODE_DRAG) {
                if (layoutParams != null) {
                    moveX = (int) ev.getX();
                    moveY = (int) ev.getY();
                    layoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
                    layoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
                    mWindowManager.updateViewLayout(dragView, layoutParams);
                }
                float mx = ev.getX();
                float my = ev.getY();
                int dropPosition = pointToPosition((int) mx, (int) my);
                Log.i(TAG, "dropPosition : " + dropPosition + " , tempPosition : " + tempPosition);
                if (dropPosition == tempPosition || dropPosition == GridView.INVALID_POSITION) {
                    return;
                }
                itemMove(dropPosition);
            }
        }

        /**
         * 判断item移动
         *
         * @param dropPosition
         */
        private void itemMove(int dropPosition) {
            TranslateAnimation translateAnimation;
            if (dropPosition < tempPosition) {
                for (int i = dropPosition; i < tempPosition; i++) {
                    View view = getChildAt(i);
                    View nextView = getChildAt(i + 1);
                    float xValue = (nextView.getLeft() - view.getLeft()) * 1f / view.getWidth();
                    float yValue = (nextView.getTop() - view.getTop()) * 1f / view.getHeight();
                    translateAnimation =
                            new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, xValue, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, yValue);
                    translateAnimation.setInterpolator(new LinearInterpolator());
                    translateAnimation.setFillAfter(true);
                    translateAnimation.setDuration(300);
                    if (i == tempPosition - 1) {
                        translateAnimation.setAnimationListener(animationListener);
                    }
                    view.startAnimation(translateAnimation);
                }
            } else {
                for (int i = tempPosition + 1; i <= dropPosition; i++) {
                    View view = getChildAt(i);
                    View prevView = getChildAt(i - 1);
                    float xValue = (prevView.getLeft() - view.getLeft()) * 1f / view.getWidth();
                    float yValue = (prevView.getTop() - view.getTop()) * 1f / view.getHeight();
                    translateAnimation =
                            new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, xValue, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, yValue);
                    translateAnimation.setInterpolator(new LinearInterpolator());
                    translateAnimation.setFillAfter(true);
                    translateAnimation.setDuration(300);
                    if (i == dropPosition) {
                        translateAnimation.setAnimationListener(animationListener);
                    }
                    view.startAnimation(translateAnimation);
                }
            }
            tempPosition = dropPosition;
        }

        /**
         * 动画监听器
         */
        Animation.AnimationListener animationListener = new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 在动画完成时将adapter里的数据交换位置
                ListAdapter adapter = getAdapter();
                if (adapter != null && adapter instanceof MyGridAdapter) {
                    ((MyGridAdapter) adapter).exchangePosition(position, tempPosition, true);
                }
                position = tempPosition;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        };

        /**
         * 关闭window
         *
         * @param x
         * @param y
         */
        private void closeWindow(float x, float y) {
            if (dragView != null) {
                mWindowManager.removeView(dragView);
                dragView = null;
                layoutParams = null;
            }
            itemDrop();
            mode = MODE_NORMAL;
        }

        /**
         * 手指抬起时，item下落
         */
        private void itemDrop() {
            if (tempPosition == position || tempPosition == GridView.INVALID_POSITION) {
                View mView = getChildAt(position);
                mView.setVisibility(VISIBLE);
                if (index == tempPosition){
                    mView.findViewById(R.id.delete_image_view).setVisibility(VISIBLE);
                }else {
                    mView.findViewById(R.id.delete_image_view).setVisibility(INVISIBLE);
                }
            } else {
                ListAdapter adapter = getAdapter();
                if (adapter != null && adapter instanceof MyGridAdapter) {
                    ((MyGridAdapter) adapter).exchangePosition(position, tempPosition, false);
                }
            }
        }
        /**
         * 获取状态栏的高度
         *
         * @param context
         * @return
         */
        private static int getStatusHeight(Context context) {
            int statusHeight = 0;
            Rect localRect = new Rect();
            ((Activity) context).getWindow().getDecorView()
                    .getWindowVisibleDisplayFrame(localRect);
            statusHeight = localRect.top;
            if (0 == statusHeight) {
                Class<?> localClass;
                try {
                    localClass = Class.forName("com.android.internal.R$dimen");
                    Object localObject = localClass.newInstance();
                    int i5 = Integer.parseInt(localClass
                            .getField("status_bar_height").get(localObject)
                            .toString());
                    statusHeight = context.getResources().getDimensionPixelSize(i5);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return statusHeight;
        }
}
