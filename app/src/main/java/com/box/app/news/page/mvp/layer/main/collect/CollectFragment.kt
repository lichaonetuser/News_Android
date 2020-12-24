package com.box.app.news.page.mvp.layer.main.collect

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import android.view.View
import com.box.app.news.R
import com.box.common.core.anim.AnimatorProperty
import com.box.common.extension.app.mvp.loading.list.MVPListFragment
import com.box.common.extension.widget.recycler.decoration.CommonItemDecoration
import kotlinx.android.synthetic.main.fragment_collect.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper


class CollectFragment : MVPListFragment<CollectContract.View,
        CollectContract.Presenter<CollectContract.View>>(),
        CollectContract.View {

    override val mPresenter = CollectPresenter()
    override val mLayoutRes = R.layout.fragment_collect
    override val mAttachSwipeBack = true
    override var mCommonItemDecorations = CommonItemDecoration()
            .withDivider(R.drawable.divider_news_list)
            .withDrawDividerOnLastItem(false)

    var mAnimator: Animator? = null

    override fun initView(view: View?, savedInstanceState: Bundle?) {
        mCommonAdapter.setEnableLoadMore(false)
        OverScrollDecoratorHelper.setUpOverScroll(common_content_rv, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        edit_btn.setOnClickListener { mPresenter.onClickEditButton() }
        favorite_delete_layout.setOnClickListener { mPresenter.onClickDeleteButton() }
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        notifyStatusBarIsLight()
    }

    override fun setEditBtnText(text: String) {
        edit_btn.text = text
    }

    override fun setEditBtnEnabled(enabled: Boolean) {
        edit_btn.isEnabled = enabled
    }

    override fun setDeleteButtonShow(show: Boolean) {
        mAnimator?.cancel()
        mAnimator = if (show) {
            ObjectAnimator.ofFloat(favorite_delete_layout,
                    AnimatorProperty.TRANSLATION_Y,
                    favorite_delete_layout.height.toFloat(), 0f)
        } else {
            ObjectAnimator.ofFloat(favorite_delete_layout,
                    AnimatorProperty.TRANSLATION_Y,
                    0f, favorite_delete_layout.height.toFloat())
        }
        mAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (!show) {
                    favorite_delete_layout.visibility = View.INVISIBLE
                } else {
                    val params = loading_layout.layoutParams as? ConstraintLayout.LayoutParams
                            ?: return
                    params.bottomToTop = R.id.favorite_delete_layout
                    loading_layout.layoutParams = params
                }
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
                if (show) {
                    favorite_delete_layout.visibility = View.VISIBLE
                } else {
                    val params = loading_layout.layoutParams as? ConstraintLayout.LayoutParams
                            ?: return
                    params.bottomToTop = ConstraintLayout.LayoutParams.UNSET
                    loading_layout.layoutParams = params
                }
            }
        })
        mAnimator?.duration = 200
        mAnimator?.start()
    }

    @SuppressLint("SetTextI18n")
    override fun refreshDeleteNum() {
        val selectedCount = getCommonAdapter().selectedItemCount
        delete_num_txt.text = "${getString(R.string.Common_Delete)}($selectedCount)"
        val haveSelected = selectedCount > 0
        favorite_delete_layout.isActivated = haveSelected
        favorite_delete_layout.isClickable = haveSelected
    }

}