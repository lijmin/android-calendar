package com.ljmin.calendar;

import java.util.Calendar;

/**
 * 日历月份单例
 */
public class Singleton {
    private static Singleton mInstance = null;
    /**
     * 当前界面的日历
     */
    private Calendar calendar;
    /**
     * 当前点击的位置
     */
    private int position;

    private Singleton() {

    }


    public static Singleton getInstance() {
        if (mInstance == null) {
            mInstance = new Singleton();
        }
        return mInstance;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }


    public Calendar getCalendar() {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public void clear(){
        setCalendar(null);
        mInstance = null;
    }

}
