package com.luseen.ribble.presentation.screen.shot_detail


import android.animation.StateListAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.method.LinkMovementMethod
import android.view.View
import com.luseen.ribble.R
import com.luseen.ribble.domain.entity.Comment
import com.luseen.ribble.domain.entity.Shot
import com.luseen.ribble.presentation.adapter.RibbleAdapter
import com.luseen.ribble.presentation.base_mvp.base.BaseFragment
import com.luseen.ribble.presentation.widget.TextImageLayout
import com.luseen.ribble.presentation.widget.navigation_view.NavigationId
import com.luseen.ribble.utils.C
import com.luseen.ribble.utils.L
import com.luseen.ribble.utils.S
import com.luseen.ribble.utils.extensions.*
import com.luseen.ribble.utils.glide.TransformationType
import com.luseen.ribble.utils.glide.load
import kotlinx.android.synthetic.main.comment_item.view.*
import kotlinx.android.synthetic.main.fragment_shot_detail.*
import javax.inject.Inject

class ShotDetailFragment : BaseFragment<ShotDetailContract.View, ShotDetailContract.Presenter>(), ShotDetailContract.View {

    companion object {
        const val SHOT_EXTRA_KEY = "shot_extra_key"

        fun getBundle(shot: Shot): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(SHOT_EXTRA_KEY, shot)
            return bundle
        }
    }

    private val items = listOf(R.drawable.heart_full, R.drawable.eye, R.drawable.bucket)

    @Inject
    protected lateinit var shotDetailPresenter: ShotDetailPresenter

    private var recyclerAdapter: RibbleAdapter<Comment>? = null
    private lateinit var shot: Shot

    override fun injectDependencies() {
        activityComponent.inject(this)
    }

    override fun layoutResId() = L.fragment_shot_detail

    override fun initPresenter() = shotDetailPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        shot = this getExtraWithKey SHOT_EXTRA_KEY
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        with(shot) {
            shotDetailImage.load(image.normal)
            shotAuthor.text = user.name
            authorLocation.text = user.location
            authorImage.load(user.avatarUrl, TransformationType.CIRCLE)
        }
        progressBar.backgroundCircleColor = takeColor(C.colorPrimary)

        //TODO move to attributes
        (0 until linearLayout.childCount)
                .map { linearLayout[it] }
                .map { it as TextImageLayout }
                .forEachIndexed { index, child ->
                    child.imageResId = items[index]
                }
        likeLayout.layoutText = shot.likesCount
        viewCountLayout.layoutText = shot.viewsCount
        bucketLayout.layoutText = shot.bucketCount
    }

    override fun onDataReceive(commentList: List<Comment>) {
        updateAdapter(commentList)
    }

    override fun getShotId(): String? {
        return shot.id
    }

    override fun showNoComments() {
        lockAppBar()
        noCommentsText.setAnimatedText(getString(S.no_comments_text))
    }

    @SuppressLint("NewApi")
    private fun lockAppBar() {
        if (isPortrait()) {
            val params = scrollingView.layoutParams as AppBarLayout.LayoutParams
            params.scrollFlags = 0
            LorAbove {
                appBarLayout.stateListAnimator = StateListAnimator()
            }
        }
    }

    override fun showLoading() {
        progressBar.start()
    }

    override fun hideLoading() {
        progressBar.stop()
    }

    override fun showError(message: String?) {
        showErrorDialog(message)
    }

    private fun updateAdapter(commentList: List<Comment>) {
        recyclerAdapter?.update(commentList) ?: this setUpRecyclerView commentList
    }

    private infix fun setUpRecyclerView(commentList: List<Comment>) {

        recyclerAdapter = RibbleAdapter(commentList, L.comment_item, {
            commentDate.text = it.commentDate
            comment.text = it.commentText
            comment.movementMethod = LinkMovementMethod.getInstance()
            commentAuthor.text = it.user?.username
            userImage.load(it.user?.avatarUrl, TransformationType.CIRCLE)
            if (it.likeCount.isZero()) {
                userCommentLikeCount.invisible()
            } else {
                userCommentLikeCount.show()
                userCommentLikeCount.text = it.likeCount.toString()
            }
        })

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = recyclerAdapter
    }

    override fun getTitle(): String {
        return shot.title ?: NavigationId.SHOT_DETAIL.name
    }
}
