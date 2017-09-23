package com.luseen.ribble.presentation.base_mvp.api

import android.support.annotation.CallSuper
import com.luseen.ribble.presentation.base_mvp.base.BaseContract
import com.luseen.ribble.presentation.base_mvp.base.BasePresenter
import com.luseen.ribble.presentation.fetcher.Fetcher
import com.luseen.ribble.presentation.fetcher.Status
import com.luseen.ribble.presentation.fetcher.result_listener.RequestType
import com.luseen.ribble.presentation.fetcher.result_listener.ResultListener
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

/**
 * Created by Chatikyan on 04.08.2017.
 */
abstract class ApiPresenter<VIEW : BaseContract.View> : BasePresenter<VIEW>(), ResultListener {

    @Inject
    protected lateinit var fetcher: Fetcher

    @Deprecated("Use requestStatus instead", level = DeprecationLevel.WARNING)
    protected var status: Status = Status.IDLE
        private set

    protected fun requestStatus(requestType: RequestType) = fetcher.getRequestStatus(requestType)

    fun <TYPE> fetch(flowable: Flowable<TYPE>,
                     requestType: RequestType = RequestType.TYPE_NONE, success: (TYPE) -> Unit) {
        fetcher.fetch(flowable, requestType, this, success)
    }

    fun <TYPE> fetch(observable: Observable<TYPE>,
                     requestType: RequestType = RequestType.TYPE_NONE, success: (TYPE) -> Unit) {
        fetcher.fetch(observable, requestType, this, success)
    }

    fun <TYPE> fetch(single: Single<TYPE>,
                     requestType: RequestType = RequestType.TYPE_NONE, success: (TYPE) -> Unit) {
        fetcher.fetch(single, requestType, this, success)
    }

    fun <TYPE> fetch(completable: Completable,
                     requestType: RequestType = RequestType.TYPE_NONE, success: (TYPE) -> Unit) {
        fetcher.fetch(completable, requestType, this, success)
    }

    @CallSuper
    override fun onPresenterDestroy() {
        super.onPresenterDestroy()
        fetcher.clear()
    }

    @CallSuper
    override fun onRequestStart(requestType: RequestType) {
        onRequestStart()
    }

    override fun onRequestStart() {
        status = Status.LOADING
    }

    @CallSuper
    override fun <T> onRequestSuccess(data: T) {
        status = if (data is List<*> && data.isEmpty()) {
            Status.EMPTY
        } else {
            Status.SUCCESS
        }
    }

    override fun onRequestError(errorMessage: String?) {
        status = Status.ERROR
    }

    @CallSuper
    override fun onRequestError(requestType: RequestType, errorMessage: String?) {
        onRequestError(errorMessage)
    }
}