package com.mynews.app.news.item.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.mynews.app.news.R;
import com.mynews.app.news.bean.date.ClassificationDataBean;
import com.mynews.app.news.page.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：New_Android
 * 类名：CustomPagerAdapter
 * 创建人：Heaven.li
 * 创建时间：2020/12/26
 * 备注：
 */
public class HomePagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<ClassificationDataBean> mData;
    private OnItemClick mOnItemClick;

    public HomePagerAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public void addAll(List<ClassificationDataBean> list){
        mData = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = View.inflate(mContext, R.layout.item_home_navigation,null);
        TextView tv = view.findViewById(R.id.tv_home_cardview);
        CardView cv_home_cardview = view.findViewById(R.id.cv_home_cardview);
        ImageView iv_home_cardview = view.findViewById(R.id.iv_home_cardview);
        view.setTag(position);
        tv.setTag("tv"+position);
        iv_home_cardview.setBackgroundResource(mData.get(position).viewid);
        tv.setText(mData.get(position).name);
        container.addView(view);
        cv_home_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClick.onItemItemClick();
            }
        });
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }


    public interface OnItemClick {
        public void onItemItemClick();
    }
}
