package com.mynews.common.core.image.fresco.controller;

import android.graphics.drawable.Animatable;
import androidx.annotation.Nullable;
import android.view.ViewGroup;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import com.mynews.common.core.image.fresco.util.ImageDensityUtil;

/**
 * 单张图片显示控制器
 *
 */
public class SingleImageControllerListener extends BaseControllerListener<ImageInfo> {

    private final SimpleDraweeView draweeView;

    public SingleImageControllerListener(SimpleDraweeView draweeView) {
        this.draweeView = draweeView;
    }

    @Override
    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
        if (imageInfo == null || draweeView == null) {
            return;
        }

        ViewGroup.LayoutParams vp = draweeView.getLayoutParams();
        int maxWidthSize = ImageDensityUtil.getDisplayWidth(draweeView.getContext());
        int maxHeightSize = ImageDensityUtil.getDisplayHeight(draweeView.getContext());
        int width = imageInfo.getWidth();
        int height = imageInfo.getHeight();

        if (width > height) {
            int maxWidth = ImageDensityUtil.dipToPixels(draweeView.getContext(), maxWidthSize);
            if (width > maxWidth) {
                width = maxWidth;
            }
            vp.width = width;
            vp.height = (int) (imageInfo.getHeight() / (float) imageInfo.getWidth() * vp.width);
        } else {
            // width <= height
            int maxHeight = ImageDensityUtil.dipToPixels(draweeView.getContext(), maxHeightSize);
            if (height > maxHeight) {
                height = maxHeight;
            }

            vp.height = height;
            vp.width = (int) ((float) imageInfo.getWidth() / imageInfo.getHeight() * vp.height);
        }

        draweeView.requestLayout();
    }

}

