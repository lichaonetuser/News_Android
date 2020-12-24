package com.box.common.core.image.fresco.photoview;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.umeng.commonsdk.statistics.common.MLog;

import java.util.ArrayList;
import java.util.Locale;

import com.box.app.news.R;
import com.box.common.core.image.fresco.photoview.anim.TransitionCompat;
import com.box.common.core.image.fresco.photoview.entity.PhotoInfo;
import com.box.common.core.image.fresco.photoview.photodraweeview.OnPhotoTapListener;
import com.box.common.core.widget.CoreViewPager;

public class PictureBrowseActivity extends FragmentActivity
        implements ViewPager.OnPageChangeListener, OnPhotoTapListener, View.OnLongClickListener {

    protected int mPhotoIndex;
    protected int mPhotoCount;
    protected PhotoInfo mLookPhoto;

    protected TextView tvPhotoIndex;
    protected CoreViewPager mViewPager;

    protected PictureBrowseAdapter mAdapter;
    protected ArrayList<PhotoInfo> mItems;

    protected TransitionCompat mTransitionCompat;
    protected boolean mPhotoOnlyOne;
    protected boolean mIsAnimation;
    protected boolean mLongClick;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        Intent data = getIntent();
        mItems = data.getParcelableArrayListExtra(PictureBrowse.PHOTO_LIST_KEY);
        if (mItems == null || mItems.size() == 0) {
            MLog.i("mItems is NULL");
            onBackPressed();
            return;
        }

        mPhotoIndex = data.getIntExtra(PictureBrowse.PHOTO_CURRENT_POSITION_KEY, 0);
        MLog.i("mPhotoIndex = " + mPhotoIndex);
        mIsAnimation = data.getBooleanExtra(PictureBrowse.PHOTO_IS_ANIMATION_KEY, false);
        MLog.i("isAnimation = " + mIsAnimation);
        mPhotoOnlyOne = data.getBooleanExtra(PictureBrowse.PHOTO_ONLY_ONE_KEY, false);
        MLog.i("mPhotoOnlyOne = " + mPhotoOnlyOne);
        mLongClick = data.getBooleanExtra(PictureBrowse.PHOTO_LONGCLICK_KEY, true);
        MLog.i("mLongClick = " + mLongClick);

        setupViews();

        if (mIsAnimation) {
            mTransitionCompat = new TransitionCompat(PictureBrowseActivity.this);
            mTransitionCompat.setCurrentPosition(mPhotoIndex);
            mTransitionCompat.startTransition();
        }
    }

    protected int getLayoutResId() {
        return R.layout.core_activity_picture_browse;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (mPhotoOnlyOne) {
            return;
        }

        mPhotoIndex = position;
        setPhotoIndex();

        if (mTransitionCompat != null && mIsAnimation) {
            MLog.i("onPageSelected mPhotoIndex = " + mPhotoIndex);
            mTransitionCompat.setCurrentPosition(mPhotoIndex);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        if (mTransitionCompat != null && mIsAnimation) {
            mTransitionCompat.finishAfterTransition();
        } else {
//            super.onBackPressed();
            finish();
            overridePendingTransition(0, 0);
        }
    }

    @Override
    public void onPhotoTap(View view, float x, float y) {
        onBackPressed();
    }

    protected void setupViews() {
        mViewPager = (CoreViewPager) findViewById(R.id.vp_picture_browse);
        mViewPager.clearOnPageChangeListeners();
        mViewPager.addOnPageChangeListener(this);

        if (mLongClick) {
            mAdapter = new PictureBrowseAdapter(this, mItems, this, this);
        } else {
            mAdapter = new PictureBrowseAdapter(this, mItems, this, null);
        }
        mViewPager.setAdapter(mAdapter);

        mPhotoCount = mItems.size();
        setupBottomViews();
        mViewPager.setCurrentItem(mPhotoIndex);
    }

    protected void setupBottomViews() {
        tvPhotoIndex = (TextView) findViewById(R.id.tv_photo_count);
        if (mPhotoOnlyOne) {
            findViewById(R.id.photo_bottom_view).setVisibility(View.GONE);
            tvPhotoIndex.setVisibility(View.GONE);
        } else {
            setPhotoIndex();
        }
    }

    protected void setPhotoIndex() {
        tvPhotoIndex.setText(String.format(Locale.getDefault(), "%d/%d", mPhotoIndex + 1, mPhotoCount));
    }

    @Override
    public boolean onLongClick(View view) {
        MLog.i("onLongClick");
        return false;
    }

    public ArrayList<PhotoInfo> getItems() {
        return mItems;
    }

    public PhotoInfo getItem(int position) {
        if (mItems != null && mItems.size() > 0) {
            return mItems.get(position);
        }
        return null;
    }

    /**
     * 获取当前PhotoInfo在集合中Position
     *
     * @return
     */
    public int getCurrentPosition() {
        return mPhotoIndex;
    }

    public PhotoInfo getCurrentPhotoInfo() {
        if (mItems != null && mItems.size() > 0) {
            return mItems.get(mPhotoIndex);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        if (mLookPhoto != null) {
            mLookPhoto = null;
        }

        if (mItems != null) {
            mItems = null;
        }

        if (mAdapter != null) {
            mAdapter.recycler();
            mAdapter = null;
        }

        if (mViewPager != null) {
            mViewPager.removeOnPageChangeListener(this);
            mViewPager.setAdapter(null);
            mViewPager = null;
        }

        super.onDestroy();
    }

}
