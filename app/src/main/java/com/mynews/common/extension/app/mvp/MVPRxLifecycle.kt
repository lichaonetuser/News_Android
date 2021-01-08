/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mynews.common.extension.app.mvp

import androidx.annotation.CheckResult
import com.trello.rxlifecycle2.LifecycleTransformer
import com.trello.rxlifecycle2.OutsideLifecycleException
import com.trello.rxlifecycle2.RxLifecycle.bind
import com.trello.rxlifecycle2.internal.Preconditions.checkNotNull
import io.reactivex.Observable
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.functions.Function

class MVPRxLifecycle private constructor() {

    init {
        throw AssertionError("No instances")
    }

    companion object {

        @CheckResult
        fun <T> bindPresenter(lifecycle: Observable<MVPPresenterEvent>): LifecycleTransformer<T> {
            return bind(lifecycle, PRESENTER_LIFECYCLE)
        }

        @CheckReturnValue
        fun <T, R> bindUntilEvent(lifecycle: Observable<R>, event: R): LifecycleTransformer<T> {
            checkNotNull(lifecycle, "lifecycle == null")
            checkNotNull(event, "event == null")
            return bind(takeUntilEvent(lifecycle, event))
        }

        private fun <R> takeUntilEvent(lifecycle: Observable<R>, event: R): Observable<R> {
            return lifecycle.filter { lifecycleEvent -> lifecycleEvent == event }
        }

        private val PRESENTER_LIFECYCLE = Function<MVPPresenterEvent, MVPPresenterEvent> { lastEvent ->
            when (lastEvent) {
                MVPPresenterEvent.CREATE,
                MVPPresenterEvent.LAZY_CREATE,
                MVPPresenterEvent.ENTER_END,
                MVPPresenterEvent.BACK_BEGIN -> MVPPresenterEvent.DESTROY
                MVPPresenterEvent.DESTROY -> throw OutsideLifecycleException("Cannot bind to presenter lifecycle when outside of it.")
            }
        }
    }
}

