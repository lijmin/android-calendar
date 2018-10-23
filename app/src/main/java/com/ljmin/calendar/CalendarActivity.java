package com.ljmin.calendar;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import static com.ljmin.calendar.CalendarFragment.format;


public class CalendarActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private ViewPagerAdapter viewPagerAdapter;

    private TextView tvYear;
    private MonthLayout monthLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_calendar);
        initView();
        initData();
    }


    private void initView() {
        viewPager = findViewById(R.id.vp);
        tvYear = findViewById(R.id.tv_year);
        monthLayout = findViewById(R.id.scrollLayout);
    }

    private void initData() {
        setupMonth();
        setupCalendar();

        monthLayout.setOnMonthClickListener(new MonthLayout.OnMonthClickListener() {
            @Override
            public void onMonthClick(View view) {
                Calendar calendar = (Calendar) view.getTag();
                Singleton.getInstance().getCalendar().set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                ((CalendarFragment) viewPagerAdapter.getRegisteredFragment(viewPager.getCurrentItem())).updateCalendar();
                setupMonth();
            }
        });
    }

    /**
     * 设置viewpager
     */
    private void setupCalendar() {
        FragmentManager fm = getSupportFragmentManager();
        viewPagerAdapter = new ViewPagerAdapter(fm);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setCurrentItem(Integer.MAX_VALUE / 2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position > Singleton.getInstance().getPosition()) { //判断向前还是向后翻页
                    Singleton.getInstance().getCalendar().add(Calendar.MONTH, 1);
                } else {
                    Singleton.getInstance().getCalendar().add(Calendar.MONTH, -1);
                }
                ((CalendarFragment) viewPagerAdapter.getRegisteredFragment(position)).updateCalendar(); //更新日历
                Singleton.getInstance().setPosition(position);
                setupMonth(); //重新设置月份
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 设置月份
     */
    public void setupMonth() {
        String year = format(Singleton.getInstance().getCalendar().getTimeInMillis(), "yyyy");
        tvYear.setText(year);
        monthLayout.setupMonth();
    }


     class ViewPagerAdapter extends FragmentPagerAdapter {
        private SparseArray<Fragment> registeredFragments = new SparseArray<>();


        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return CalendarFragment.newInstance();
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //退出时清空日历
        Singleton.getInstance().clear();
    }


}
