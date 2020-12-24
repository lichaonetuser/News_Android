package com.box.common.extension.widget.bar.indicator.buildins.commonnavigator.abs;

import java.util.List;

import com.box.common.extension.widget.bar.indicator.buildins.commonnavigator.model.PositionData;

/**
 * 抽象的viewpager指示器，适用于CommonNavigator
 * 博客: http://hackware.lucode.net
 */
public interface IPagerIndicator {
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onPositionDataProvide(List<PositionData> dataList);
}
