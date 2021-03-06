package com.mynews.common.extension.widget.bar.indicator.buildins.commonnavigator.abs;

/**
 * 可测量内容区域的指示器标题
 * 博客: http://hackware.lucode.net
 */
public interface IMeasurablePagerTitleView extends IPagerTitleView {
    int getContentLeft();

    int getContentTop();

    int getContentRight();

    int getContentBottom();
}
