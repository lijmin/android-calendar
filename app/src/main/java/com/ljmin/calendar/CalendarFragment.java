package com.ljmin.calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.ljmin.calendar.CalendarFragment.CalendarEntity.DATE_STATUS_DISABLE;
import static com.ljmin.calendar.CalendarFragment.CalendarEntity.DATE_STATUS_NORMAL;



public class CalendarFragment extends Fragment {
    /**
     * 当前日期
     */
    private String currentDay;

    private GridView gridView;
    private List<CalendarEntity> calendarEntities;
    private CalendarAdapter calendarAdapter;


    private View rootView;

    public static CalendarFragment newInstance() {
        return new CalendarFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(
                R.layout.fragment_calendar, container, false);

        initView();
        initData();
        return rootView;
    }

    protected void initView() {
        gridView = rootView.findViewById(R.id.gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CalendarEntity entity = calendarEntities.get(position);
                calendarAdapter.setSeclection(entity.date);
                calendarAdapter.notifyDataSetChanged();
            }
        });

    }


    private void initData() {
        currentDay = format(System.currentTimeMillis(), "yyyy-MM-dd");

        calendarEntities = new ArrayList<>();
        gridView.setAdapter(calendarAdapter = new CalendarAdapter());
        calendarAdapter.setSeclection(currentDay);

        updateCalendar();

    }

    public void updateCalendar() {
        ArrayList<CalendarEntity> tempList = new ArrayList<>();
        CalendarEntity entity;
        String date;//日历时间
        Calendar cal = (Calendar) Singleton.getInstance().getCalendar().clone();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        String ymd = format(Singleton.getInstance().getCalendar().getTimeInMillis(), "yyyy-MM-dd");
        String nymd = format(System.currentTimeMillis(), "yyyy-MM-dd");
        if (ymd.equals(nymd)) {
            this.calendarAdapter.setSeclection(ymd);
        } else {
            this.calendarAdapter.setSeclection(format(cal.getTimeInMillis(), "yyyy-MM-dd"));
        }

        int dateOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int preSpacing = dateOfWeek - 1;
        Calendar preMonth = (Calendar) cal.clone();
        preMonth.add(Calendar.MONTH, -1); //前一月
        preMonth.set(Calendar.DAY_OF_MONTH, preMonth.getActualMaximum(Calendar.DAY_OF_MONTH) - preSpacing + 1);

        for (int i = 0; i < preSpacing; i++) { //前面部分
            date = format(preMonth.getTimeInMillis(), "yyyy-MM-dd");
            entity = new CalendarEntity(date, DATE_STATUS_DISABLE);
            tempList.add(entity);
            preMonth.add(Calendar.DAY_OF_MONTH, 1);
        }

        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        int lastDay = cal.get(Calendar.DAY_OF_MONTH);

        cal.set(Calendar.DAY_OF_MONTH, 1);
        for (int i = 0; i < lastDay; i++) { //中间部分
            entity = new CalendarEntity(format(cal.getTimeInMillis(), "yyyy-MM-dd"), DATE_STATUS_NORMAL);
            tempList.add(entity);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        cal.add(Calendar.DAY_OF_MONTH, -1); //多加的一天减掉，防止跳过下一月
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        int nextMonthSpacing = 7 - cal.get(Calendar.DAY_OF_WEEK);
        for (int i = 0; i < nextMonthSpacing; i++) { //后面一个月
            cal.add(Calendar.DAY_OF_MONTH, 1);
            entity = new CalendarEntity(format(cal.getTimeInMillis(), "yyyy-MM-dd"), DATE_STATUS_DISABLE);
            tempList.add(entity);
        }

        calendarEntities.clear();
        calendarEntities.addAll(tempList);

        calendarAdapter.notifyDataSetChanged();
    }


    private class CalendarAdapter extends BaseAdapter {
        private String clickDate;

        //标识选择的Item
        void setSeclection(String clickDate) {
            this.clickDate = clickDate;
        }

        String getClickDate() {
            return clickDate;
        }

        @Override
        public int getCount() {
            return calendarEntities.size();
        }

        @Override
        public Object getItem(int position) {
            return calendarEntities.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder;
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.item_calendar, null);
                holder = new Holder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            CalendarEntity entity = calendarEntities.get(position);

            //设置数字
            int date = 0;
            try {
                date = Integer.parseInt(entity.date.split("-")[2]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvDate.setText(String.valueOf(date));

            if (clickDate != null && entity.date.equals(clickDate)) { //选中
                holder.tvDate.setTextColor(Color.parseColor("#ffffff")); //设置白色字体
                holder.tvDate.setBackgroundResource(R.drawable.calendar_item_ic_selected);//设置背景
            } else { //未选中
                //设置字体
                if (entity.date.equals(currentDay)) { //当前日期
                    holder.tvDate.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                } else if (entity.dateStatus == DATE_STATUS_NORMAL) { // 正常文字
                    holder.tvDate.setTextColor(Color.parseColor("#666666"));
                } else { //弱字体
                    holder.tvDate.setTextColor(Color.parseColor("#aaaaaa"));
                }

                //不设置背景
                holder.tvDate.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }

        class Holder {
            TextView tvDate;
            Holder(View itemView) {
                tvDate = itemView.findViewById(R.id.tv_date);
            }
        }

    }



    class CalendarEntity {
        final static int DATE_STATUS_NORMAL = 0;
        final static int DATE_STATUS_DISABLE = 1;

        String date;
        int dateStatus;  //日期状态：0本月非周末，1其它

        CalendarEntity(String date, int dateStatus) {
            this.date = date;
            this.dateStatus = dateStatus;
        }
    }


    public static String format(Object date, String pattern) {
        if (date == null) {
            return null;
        }
        String strDate = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
            strDate = sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

}
