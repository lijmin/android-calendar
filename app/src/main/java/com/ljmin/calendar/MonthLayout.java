package com.ljmin.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

import java.util.Calendar;

/**
 * 滚动的月份控件
 */
public class MonthLayout extends ViewGroup {
    /**
     * 用于完成滚动操作的实例
     */
    private Scroller mScroller;
    /**
     * 单个月份的宽度
     */
    private int childWidth;

    private OnMonthClickListener onMonthClickListener;


    public MonthLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        setupMonth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        childWidth = sizeWidth / 5;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            LayoutParams params = child.getLayoutParams();
            params.width = childWidth;
            child.setLayoutParams(params); //重新设置宽度
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
        }
        setMeasuredDimension(sizeWidth, MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            // 为ScrollerLayout中的每一个子控件在水平方向上进行布局
            childView.layout(i * childView.getMeasuredWidth(), 0, (i + 1) * childView.getMeasuredWidth(), childView.getMeasuredHeight());
        }
        //默认滚动两个位置
        scrollTo(childWidth * 2, 0);
    }

    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - mScroller.getFinalX();
        int dy = fy - mScroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {
        //设置mScroller的滚动偏移量，先滚动了两个宽度
        int starX = 2 * childWidth - dx;
        mScroller.startScroll(starX, mScroller.getFinalY(), dx, dy);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    @Override
    public void computeScroll() {
        // 重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 重新添加控件
     */
    public void setupMonth() {
        removeAllViews();
        Calendar calendar = (Calendar) Singleton.getInstance().getCalendar().clone();
        calendar.add(Calendar.MONTH, -4);
        for (int i = 0; i < 9; i++) {
            TextView textView = new TextView(getContext());
            textView.setTag(calendar.clone());
            textView.setGravity(Gravity.CENTER);
            LayoutParams params = new LayoutParams(
                    0,
                    LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(params);
            textView.setText((calendar.get(Calendar.MONTH) + 1) + "月");
            if (i == 4) {
                textView.setTextColor(Color.parseColor("#ffffff"));
            } else {
                textView.setTextColor(Color.parseColor("#333333"));
            }
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int x = (int) v.getX();
                    int dif = x - 4 * childWidth;
                    smoothScrollBy(dif, 0);

                    Singleton.getInstance().setCalendar((Calendar) v.getTag());
                    setupMonth();

                    if (onMonthClickListener != null) {
                        onMonthClickListener.onMonthClick(v);
                    }
                }
            });

            addView(textView, params);

            calendar.add(Calendar.MONTH, 1);
        }
    }

    /**
     * 月份的点击接口
     */
    public interface OnMonthClickListener {
        void onMonthClick(View view);
    }

    public OnMonthClickListener getOnMonthClickListener() {
        return onMonthClickListener;
    }

    public void setOnMonthClickListener(OnMonthClickListener onMonthClickListener) {
        this.onMonthClickListener = onMonthClickListener;
    }
}