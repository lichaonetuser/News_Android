package com.box.common.core.anim;

import android.widget.ImageView;

public class FrameAnim {

    private boolean mIsRepeat;

    private AnimationListener mAnimationListener;

    private ImageView mImageView;

    private int[] mFrameRess;

    /**
     * 每帧动画的播放间隔数组
     */
    private int[] mDurations;

    /**
     * 每帧动画的播放间隔
     */
    private int mDuration;

    /**
     * 下一遍动画播放的延迟时间
     */
    private int mDelay;

    private int mLastFrame;

    private boolean mNext;

    private boolean mPause;

    private int mCurrentSelect;

    private int mCurrentFrame;

    private static final int SELECTED_A = 1;

    private static final int SELECTED_B = 2;

    private static final int SELECTED_C = 3;

    private static final int SELECTED_D = 4;


    /**
     * @param iv       播放动画的控件
     * @param frameRes 播放的图片数组
     * @param duration 每帧动画的播放间隔(毫秒)
     * @param isRepeat 是否循环播放
     */
    public FrameAnim(ImageView iv, int[] frameRes, int duration, boolean isRepeat) {
        this.mImageView = iv;
        this.mFrameRess = frameRes;
        this.mDuration = duration;
        this.mLastFrame = frameRes.length - 1;
        this.mIsRepeat = isRepeat;
    }

    /**
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param isRepeat  是否循环播放
     */
    public FrameAnim(ImageView iv, int[] frameRess, int[] durations, boolean isRepeat) {
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDurations = durations;
        this.mLastFrame = frameRess.length - 1;
        this.mIsRepeat = isRepeat;
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param duration  每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    public FrameAnim(ImageView iv, int[] frameRess, int duration, int delay) {
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDuration = duration;
        this.mDelay = delay;
        this.mLastFrame = frameRess.length - 1;
    }

    /**
     * 循环播放动画
     *
     * @param iv        播放动画的控件
     * @param frameRess 播放的图片数组
     * @param durations 每帧动画的播放间隔(毫秒)
     * @param delay     循环播放的时间间隔
     */
    public FrameAnim(ImageView iv, int[] frameRess, int[] durations, int delay) {
        this.mImageView = iv;
        this.mFrameRess = frameRess;
        this.mDurations = durations;
        this.mDelay = delay;
        this.mLastFrame = frameRess.length - 1;
    }

    public void playByDurationsAndDelay(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {   // 暂停和播放需求
                    mCurrentSelect = SELECTED_A;
                    mCurrentFrame = i;
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                mImageView.setImageResource(mFrameRess[i]);
                if (i == mLastFrame) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationRepeat();
                    }
                    mNext = true;
                    playByDurationsAndDelay(0);
                } else {
                    playByDurationsAndDelay(i + 1);
                }
            }
        }, mNext && mDelay > 0 ? mDelay : mDurations[i]);

    }

    public void playAndDelay(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_B;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                mNext = false;
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                mImageView.setImageResource(mFrameRess[i]);
                if (i == mLastFrame) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationRepeat();
                    }
                    mNext = true;
                    playAndDelay(0);
                } else {
                    playAndDelay(i + 1);
                }
            }
        }, mNext && mDelay > 0 ? mDelay : mDuration);

    }

    public void playByDurations(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_C;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                mImageView.setImageResource(mFrameRess[i]);
                if (i == mLastFrame) {
                    if (mIsRepeat) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationRepeat();
                        }
                        playByDurations(0);
                    } else {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationEnd();
                        }
                    }
                } else {

                    playByDurations(i + 1);
                }
            }
        }, mDurations[i]);

    }

    public void cancelTo(int frame) {
        if (mImageView.getHandler() != null) {
            mImageView.getHandler().removeCallbacksAndMessages(null);
        }
        mImageView.setImageResource(mFrameRess[frame]);
    }

    public void play(final int i) {
        mImageView.postDelayed(new Runnable() {

            @Override
            public void run() {
                if (mPause) {
                    if (mPause) {
                        mCurrentSelect = SELECTED_D;
                        mCurrentFrame = i;
                        return;
                    }
                    return;
                }
                if (0 == i) {
                    if (mAnimationListener != null) {
                        mAnimationListener.onAnimationStart();
                    }
                }
                mImageView.setImageResource(mFrameRess[i]);
                if (i == mLastFrame) {

                    if (mIsRepeat) {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationRepeat();
                        }
                        play(0);
                    } else {
                        if (mAnimationListener != null) {
                            mAnimationListener.onAnimationEnd();
                        }
                    }

                } else {

                    play(i + 1);
                }
            }
        }, mDuration);
    }

    public static interface AnimationListener {

        /**
         * <p>Notifies the start of the animation.</p>
         */
        void onAnimationStart();

        /**
         * <p>Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         */
        void onAnimationEnd();

        /**
         * <p>Notifies the repetition of the animation.</p>
         */
        void onAnimationRepeat();
    }

    /**
     * <p>Binds an animation listener to this animation. The animation listener
     * is notified of animation events such as the end of the animation or the
     * repetition of the animation.</p>
     *
     * @param listener the animation listener to be notified
     */
    public void setAnimationListener(AnimationListener listener) {
        this.mAnimationListener = listener;
    }

    public void release() {
        pauseAnimation();
    }

    public void pauseAnimation() {
        this.mPause = true;
    }

    public boolean isPause() {
        return this.mPause;
    }

    public void restartAnimation() {
        if (mPause) {
            mPause = false;
            switch (mCurrentSelect) {
                case SELECTED_A:
                    playByDurationsAndDelay(mCurrentFrame);
                    break;
                case SELECTED_B:
                    playAndDelay(mCurrentFrame);
                    break;
                case SELECTED_C:
                    playByDurations(mCurrentFrame);
                    break;
                case SELECTED_D:
                    play(mCurrentFrame);
                    break;
                default:
                    break;
            }
        }
    }

}
