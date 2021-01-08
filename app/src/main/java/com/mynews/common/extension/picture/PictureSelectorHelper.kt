package com.mynews.common.extension.picture

import android.app.Activity
import androidx.fragment.app.Fragment;
import com.mynews.common.core.environment.EnvDisplayMetrics
import com.luck.picture.lib.PictureSelectionModel
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia

object PictureSelectorHelper {

    val REQUEST_CODE_SELECT_SINGLE_PICTURE = PictureConfig.CHOOSE_REQUEST

    fun startSelectSinglePicture(activity: Activity) {
        getDefaultSinglePictureSelectionModel(activity).forResult(REQUEST_CODE_SELECT_SINGLE_PICTURE)
    }

    fun startSelectSinglePicture(fragment: Fragment) {
        getDefaultSinglePictureSelectionModel(fragment).forResult(REQUEST_CODE_SELECT_SINGLE_PICTURE)
    }

    fun startSelectSinglePicture(pictureSelectionModel: PictureSelectionModel) {
        pictureSelectionModel.forResult(REQUEST_CODE_SELECT_SINGLE_PICTURE)
    }

    fun startSelectSinglePicture(pictureSelectionModel: PictureSelectionModel, requestCode: Int) {
        pictureSelectionModel.forResult(requestCode)
    }

    fun getDefaultSinglePictureSelectionModel(activity: Activity): PictureSelectionModel {
        return getDefaultSinglePictureSelectionModel(PictureSelector.create(activity))
    }

    fun getDefaultSinglePictureSelectionModel(fragment: Fragment): PictureSelectionModel {
        return getDefaultSinglePictureSelectionModel(PictureSelector.create(fragment))
    }

    private fun getDefaultSinglePictureSelectionModel(pictureSelector: PictureSelector): PictureSelectionModel {
        return pictureSelector.openGallery(PictureMimeType.ofImage()) // 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()
                .maxSelectNum(1)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选
                .previewImage(true)
                .previewVideo(false)// 是否可预览视频
                .imageSpanCount(4)// 每行显示个数 int
                .withAspectRatio(EnvDisplayMetrics.WIDTH_PIXELS, EnvDisplayMetrics.HEIGHT_PIXELS)
                .isCamera(true)// 是否显示拍照按钮
                .enableCrop(true)// 是否裁剪
                .compress(true)// 是否压缩
                .circleDimmedLayer(false)// 是否圆形裁剪
                .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
                .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .cropCompressQuality(80)// 裁剪压缩质量 默认90
                .minimumCompressSize(100)
                .rotateEnabled(true) // 裁剪是否可旋转图片
                .scaleEnabled(true)
    }

    fun getPathFromLocalMedia(localMedia: LocalMedia?): String {
        return if (localMedia == null) ""
        else when {
            localMedia.isCut && !localMedia.isCompressed -> localMedia.cutPath // 裁剪过
            localMedia.isCompressed || localMedia.isCut && localMedia.isCompressed -> localMedia.compressPath // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
            else -> localMedia.path // 原图地址
        }
    }

}