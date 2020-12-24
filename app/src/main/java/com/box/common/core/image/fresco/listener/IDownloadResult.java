package com.box.common.core.image.fresco.listener;

import android.content.Context;

import com.box.common.core.image.fresco.util.ImageFileUtils;

/**
 * 下载图片的结果监听器
 * <p>
 */
public abstract class IDownloadResult implements IResult<String> {

    private String mFilePath;

    public IDownloadResult(String filePath) {
        this.mFilePath = filePath;
    }

    public IDownloadResult(Context context) {
        this.mFilePath = ImageFileUtils.getImageDownloadPath(context);
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void onProgress(int progress) {

    }

    public void onError(Exception e) {

    }

    @Override
    public abstract void onResult(String filePath);

}
