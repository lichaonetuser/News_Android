package com.mynews.app.news.page.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mynews.app.news.R;
import com.mynews.app.news.bean.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：New_Android
 * 类名：MyGridAdapter
 * 创建人：Heaven.li
 * 创建时间：2021/1/11
 * 备注：
 */
public class MyGridAdapter extends BaseAdapter {

    private static final String TAG = "MyGridAdapter";
    private Context mContext;
    private List<Channel> mListBean;

    private int Type;

    private boolean isMove = false;

    private int movePosition = -1;

    public MyGridAdapter(Context context,int type){
        mListBean = new ArrayList<>();
        this.mContext = context;
        this.Type = type;
    }
    public void setListBean(List<Channel> ListBean) {
        movePosition = -1;
        isMove = false;
        this.mListBean = ListBean;
        notifyDataSetChanged();
    }


    public List<Channel> getListBean(){
        return mListBean;
    }

    private String getString(int ID){
        return mContext.getResources().getString(ID);
    }

    @Override
    public int getCount() {
        return mListBean.size();
    }

    @Override
    public Object getItem(int position) {
        return mListBean.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = mListBean.get(position).getName();
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gridview, null);
        if (position == movePosition && isMove) {
            convertView.setVisibility(View.INVISIBLE);
        }else {
            convertView.setVisibility(View.VISIBLE);
        }
        TextView tv_text = convertView.findViewById(R.id.tv_name);
        ImageView iv_ico = convertView.findViewById(R.id.iv_ico);
        ImageView delete_image_view = convertView.findViewById(R.id.delete_image_view);
        if (Type == 0){
            delete_image_view.setBackgroundResource(R.mipmap.close);
            delete_image_view.setVisibility(View.INVISIBLE);
        }else {
            delete_image_view.setBackgroundResource(R.mipmap.add_to);
            delete_image_view.setVisibility(View.VISIBLE);
        }
        delete_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnItemClick.onItemClick(Type,position);
            }
        });
        tv_text.setText(name);
        if (name.equals(getString(R.string.channel_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.recommend_h);
        }else if (name.equals(getString(R.string.girl_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.girl_h);
        }else if (name.equals(getString(R.string.music_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.music_h);
        }else if (name.equals(getString(R.string.cartoon_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.cartoon_h);
        }else if (name.equals(getString(R.string.entertainment_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.entertainment_h);
        }else if (name.equals(getString(R.string.economics_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.economics_h);
        }else if (name.equals(getString(R.string.car_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.car_h);
        }else if (name.equals(getString(R.string.politics_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.politics_h);
        }else if (name.equals(getString(R.string.sports_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.sports_h);
        }else if (name.equals(getString(R.string.gif_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.gif_h);
        }else if (name.equals(getString(R.string.illustration_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.illustration_h);
        }else if (name.equals(getString(R.string.headlines_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.headlines_h);
        }else if (name.equals(getString(R.string.restaurant_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.restaurant_h);
        }else if (name.equals(getString(R.string.international_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.international_h);
        }else if (name.equals(getString(R.string.domestic_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.domestic_h);
        }else if (name.equals(getString(R.string.travel_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.travel_h);
        }else if (name.equals(getString(R.string.summary_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.whole_h);
        }else if (name.equals(getString(R.string.it_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.it_technology_h);
        }else if (name.equals(getString(R.string.report_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.quick_report_h);
        }else if (name.equals(getString(R.string.woman_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.female_h);
        }else if (name.equals(getString(R.string.healing_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.be_cured_h);
        }else if (name.equals(getString(R.string.beauty_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.beauty_h);
        }else if (name.equals(getString(R.string.interesting_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.international_h);
        }else if (name.equals(getString(R.string.beauty18_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.prohibit18_h);
        }else if (name.equals(getString(R.string.story_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.funny_h);
        }else if (name.equals(getString(R.string.healed_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.cure_h);
        }else if (name.equals(getString(R.string.bytheway_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.comic_h);
        }else if (name.equals(getString(R.string.life_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.life_h);
        }else if (name.equals(getString(R.string.video_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.video_h);
        }else if (name.equals(getString(R.string.game_ForYou))){
            iv_ico.setBackgroundResource(R.mipmap.game_h);
        }
        return convertView;
    }

    /**
     * 给item交换位置
     *
     * @param originalPosition item原先位置
     * @param nowPosition      item现在位置
     */
    public void exchangePosition(int originalPosition, int nowPosition, boolean isMove) {
        Channel mChannel = mListBean.get(originalPosition);
        mListBean.remove(originalPosition);
        mListBean.add(nowPosition, mChannel);
        movePosition = nowPosition;
        this.isMove = isMove;
        notifyDataSetChanged();
    }

    public int changePosition (){
        return mListBean.size();
    }

    private OnItemClick mOnItemClick;

    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    public interface OnItemClick {
        void onItemClick(int type,int position);
    }
}
