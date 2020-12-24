package com.box.app.news.data

import android.os.Parcelable
import com.box.app.news.bean.*
import com.box.app.news.bean.base.BaseNewsBean
import com.box.app.news.data.cache.CacheManager
import com.box.app.news.data.source.local.LocalSource
import com.box.app.news.data.source.memory.MemorySource
import com.box.app.news.data.source.remote.RemoteSource
import com.box.app.news.data.task.initialization.InitTasks
import com.box.app.news.data.task.polling.PollingTasks
import com.box.app.news.event.EventManager
import com.box.app.news.event.change.*
import com.box.app.news.util.PKUtils
import com.box.common.core.rx.schedulers.io
import com.crashlytics.android.Crashlytics
import com.google.firebase.perf.metrics.AddTrace
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

object DataManager {

    @AddTrace(name = "InitDataManager", enabled = true)
    fun init() {
        Init.startDefault()
        Polling.startDefault()
    }

    object Init : InitTasks()

    object Polling : PollingTasks()

    object Memory : MemorySource()

    object Local : LocalSource()

    object Remote : RemoteSource() {

        fun toggleCollectNews(news: BaseNewsBean, channel: Channel) {
            if (news.isFavorite) {
                uncollectNews(news, channel)
            } else {
                collectNews(news, channel)
            }
        }

        fun collectNews(news: BaseNewsBean, channel: Channel) {
            if (news.isFavorite) {
                return
            }
            val actionType = DataDictionary.NewsActionType.FAVORITE
            news.isFavorite = true
            postArticleUserAction(actionType.value, news.aid, pk = PKUtils.getPk())
                    .io().onErrorReturn { -1 }.subscribe()
            EventManager.post(CollectionListChangeEvent(DataAction.INSERT, news))
            EventManager.post(NewsListChangeEvent(DataAction.UPDATE, channel, news, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
        }

        fun uncollectNews(news: BaseNewsBean, channel: Channel) {
            if (!news.isFavorite) {
                return
            }
            val actionType = DataDictionary.NewsActionType.UNFAVORITE
            news.isFavorite = false
            postArticleUserAction(actionType.value, news.aid, pk = PKUtils.getPk())
                    .io().onErrorReturn { -1 }.subscribe()
            EventManager.post(CollectionListChangeEvent(DataAction.DELETE, news))
            EventManager.post(NewsListChangeEvent(DataAction.UPDATE, channel, news, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
        }

        fun uncollectNewsList(newsList: List<BaseNewsBean>, checkChannel: Boolean) {
            val opss = arrayListOf<Ops>()
            for (bean: BaseNewsBean in newsList) {
                opss.add(Ops(actionType = DataDictionary.NewsActionType.UNFAVORITE.value, aid = bean.aid
                        , pk = PKUtils.getPk()))
            }
            Remote.postArticleUserActions(opss)
                    .io().onErrorReturn { -1 }.subscribeBy(
                            onNext = {
                                for (new: BaseNewsBean in newsList) {
                                    new.isFavorite = false
                                    EventManager.post(NewsListChangeEvent(DataAction.UPDATE, Channel(), new, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION, checkChannel))
                                }
                            },
                            onError = {
                            }
                    )
        }

        fun toggleDigComment(comment: Comment, targetBean: Parcelable) {
            if (comment.isDigged) {
                cancelDigComment(comment, targetBean)
            } else {
                digComment(comment, targetBean)
            }
        }

        fun digComment(comment: Comment, targetBean: Parcelable) {
            if (comment.isDigged) {
                return
            }
            val actionType = DataDictionary.NewsActionType.DIG
            comment.isDigged = true
            comment.digCount += 1
            postCommentUserAction(actionType = actionType.value, cid = comment.id, pk = PKUtils.getPk())
                    .io().onErrorReturn { -1 }.subscribe()
            EventManager.post(CommentListChangeEvent(action = DataAction.UPDATE, targetBean = targetBean, comment = comment, extra = CommentListChangeEvent.EXTRA.UPDATE_INFORMATION))
        }

        fun cancelDigComment(comment: Comment, targetBean: Parcelable) {
            if (!comment.isDigged) {
                return
            }
            val actionType = DataDictionary.NewsActionType.CANCEL_DIG
            comment.isDigged = false
            comment.digCount -= 1
            postCommentUserAction(actionType = actionType.value, cid = comment.id, pk = PKUtils.getPk())
                    .io().onErrorReturn { -1 }.subscribe()
            EventManager.post(CommentListChangeEvent(action = DataAction.UPDATE, targetBean = targetBean, comment = comment, extra = CommentListChangeEvent.EXTRA.UPDATE_INFORMATION))
        }

        fun toggleDigNews(news: BaseNewsBean, channel: Channel) {
            if (news.isDigged) {
                cancelDigNews(news, channel)
            } else {
                digNews(news, channel)
            }
        }

        fun digNews(news: BaseNewsBean, channel: Channel) {
            if (news.isDigged) {
                return
            }
            val actionType: DataDictionary.NewsActionType
            if (news.isBuried) {
                actionType = DataDictionary.NewsActionType.CANCEL_BURY_AND_DIG
                news.buryCount -= 1
            } else {
                actionType = DataDictionary.NewsActionType.DIG
            }
            news.isDigged = true
            news.isBuried = false
            news.digCount += 1
            postArticleUserAction(actionType.value, news.aid, pk = PKUtils.getPk())
                    .io().onErrorReturn { -1 }.subscribe()
            EventManager.post(NewsListChangeEvent(DataAction.UPDATE, channel, news, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
        }

        fun cancelDigNews(news: BaseNewsBean, channel: Channel) {
            if (!news.isDigged) {
                return
            }
            news.isDigged = false
            news.digCount -= 1
            postArticleUserAction(DataDictionary.NewsActionType.CANCEL_DIG.value, news.aid, pk = PKUtils.getPk())
                    .io().onErrorReturn { -1 }.subscribe()
            EventManager.post(NewsListChangeEvent(DataAction.UPDATE, channel, news, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
        }

        fun toggleBuryNews(news: BaseNewsBean, channel: Channel) {
            if (news.isBuried) {
                cancelBuryNews(news, channel)
            } else {
                buryNews(news, channel)
            }
        }

        fun buryNews(news: BaseNewsBean, channel: Channel) {
            if (news.isBuried) {
                return
            }
            val actionType: DataDictionary.NewsActionType
            if (news.isDigged) {
                actionType = DataDictionary.NewsActionType.CANCEL_DIG_AND_BURY
                news.digCount -= 1
            } else {
                actionType = DataDictionary.NewsActionType.BURY
            }
            news.isDigged = false
            news.isBuried = true
            news.buryCount += 1
            postArticleUserAction(actionType.value, news.aid, pk = PKUtils.getPk())
                    .io().onErrorReturn { -1 }.subscribe()
            EventManager.post(NewsListChangeEvent(DataAction.UPDATE, channel, news, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
        }

        fun cancelBuryNews(news: BaseNewsBean, channel: Channel) {
            if (!news.isBuried) {
                return
            }
            news.isBuried = false
            news.buryCount -= 1
            postArticleUserAction(DataDictionary.NewsActionType.CANCEL_BURY.value, news.aid, pk = PKUtils.getPk())
                    .io().onErrorReturn { -1 }.subscribe()
            EventManager.post(NewsListChangeEvent(DataAction.UPDATE, channel, news, NewsListChangeEvent.EXTRA.UPDATE_INFORMATION))
        }
    }

    object Cache : CacheManager()

    fun checkAndSubmitPushToken(token: String?) {
        if (token == null) {
            return
        }

        if (token.isBlank()) {
            return
        }

        val isSubmit = Local.getPushTokenSubmit()
        if (!isSubmit) {
            Remote.submitPushToken(token)
                    .io()
                    .subscribeBy(
                            onNext = {
                                Local.savePushTokenSubmit(true)
                            },
                            onError = {
                                Local.savePushTokenSubmit(false)
                            })
        }
    }

    fun submitPushToken(token: String?) {
        try {
            if (token == null || token.isBlank()) {
                return
            }

            Remote.submitPushToken(token)
                    .io()
                    .subscribeBy(
                            onNext = {
                                Local.savePushTokenSubmit(true)
                            },
                            onError = {
                                Local.savePushTokenSubmit(false)
                            })
        } catch (e: Exception) {
            Crashlytics.logException(e)
        }
    }

    fun deleteArticleAndPostNotInterest(news: BaseNewsBean, channel: Channel): Single<Int> {
        Remote.postArticleUserAction(DataDictionary.NewsActionType.NOT_INTEREST.value, news.aid, pk = PKUtils.getPk())
                .io().onErrorReturn { -1 }.subscribe()
        return Local.deleteArticleListOrder(news, channel).doOnSuccess {
            EventManager.post(NewsListChangeEvent(DataAction.DELETE, channel, news))
        }

    }

    fun deleteArticleAndPostDelete(news: BaseNewsBean, channel: Channel): Single<Int> {
        Remote.postArticleUserAction(DataDictionary.NewsActionType.DELETE.value, news.aid, pk = PKUtils.getPk())
                .io().onErrorReturn { -1 }.subscribe()
        return Local.deleteArticleListOrder(news, channel).doOnSuccess {
            EventManager.post(NewsListChangeEvent(DataAction.DELETE, channel, news))
        }
    }

    fun toggleSubscribeTeam(team: WorldcupTeam, isToggle: Boolean) {
        if (isToggle) {
            if (team.is_subscribed) {
                cancelSubscribeTeam(team)
            } else {
                subscribeTeam(team)
            }
        }
        EventManager.post(TeamSubscribeChangeEvent(action = DataAction.UPDATE, team = team, extra = TeamSubscribeChangeEvent.EXTRA.UPDATE_INFORMATION))
    }

    fun subscribeTeam(team: WorldcupTeam) {
        if (team.is_subscribed) {
            return
        }
        val actionType = DataDictionary.SportActionType.FOLLOW
        team.is_subscribed = true
        Remote.postSportsUserAction(actionType = actionType.value, itemId = team.team_id)
                .io().doOnNext { }.onErrorReturn { -1 }.subscribe()
    }

    fun cancelSubscribeTeam(team: WorldcupTeam) {
        if (!team.is_subscribed) {
            return
        }
        val actionType = DataDictionary.SportActionType.CANCEL_FOLLOW
        team.is_subscribed = false
        Remote.postSportsUserAction(actionType = actionType.value, itemId = team.team_id)
                .io().onErrorReturn { -1 }.subscribe()
    }

    fun toggleSubscribeMatch(match: WorldcupMatch, isToggle: Boolean) {
        if (isToggle) {
            if (match.is_subscribed) {
                cancelSubscribeMatch(match)
            } else {
                subscribeMatch(match)
            }
        }
        EventManager.post(MatchSubscribeChangeEvent(action = DataAction.UPDATE, match = match, extra = MatchSubscribeChangeEvent.EXTRA.UPDATE_SUBSCRIBE_INFORMATION))
    }

    fun subscribeMatch(match: WorldcupMatch) {
        if (match.is_subscribed) {
            return
        }
        val actionType = DataDictionary.SportActionType.RESERVATION
        match.is_subscribed = true
        Remote.postSportsUserAction(actionType = actionType.value, itemId = match.match_id)
                .io().onErrorReturn { -1 }.subscribe()
    }

    fun cancelSubscribeMatch(match: WorldcupMatch) {
        if (!match.is_subscribed) {
            return
        }
        val actionType = DataDictionary.SportActionType.CANCEL_RESERVATION
        match.is_subscribed = false
        Remote.postSportsUserAction(actionType = actionType.value, itemId = match.match_id)
                .io().onErrorReturn { -1 }.subscribe()
    }

    fun toggleDigHome(match: WorldcupMatch, isToggle: Boolean) {
        if (isToggle) {
            if (match.home_team.is_digged) {
                cancelDigHome(match)
            } else {
                digHome(match)
            }
        }
        EventManager.post(MatchSubscribeChangeEvent(action = DataAction.UPDATE, match = match, extra = MatchSubscribeChangeEvent.EXTRA.UPDATE_DIG_HOME_TEAM))
    }

    fun digHome(match: WorldcupMatch) {
        if (match.home_team.is_digged) {
            return
        }
        val actionType = DataDictionary.SportActionType.DIG_HOME
        match.home_team.is_digged = true
        match.home_team.dig_count += 1
        cancelDigAway(match)
        Remote.postSportsUserAction(actionType = actionType.value, itemId = match.match_id)
                .io().onErrorReturn { -1 }.subscribe()
    }

    fun cancelDigHome(match: WorldcupMatch) {
        if (!match.home_team.is_digged) {
            return
        }
        val actionType = DataDictionary.SportActionType.CANCEL_DIG_HOME
        match.home_team.is_digged = false
        if (match.home_team.dig_count > 0) {
            match.home_team.dig_count -= 1
        }
        Remote.postSportsUserAction(actionType = actionType.value, itemId = match.match_id)
                .io().onErrorReturn { -1 }.subscribe()
    }

    fun toggleDigAway(match: WorldcupMatch, isToggle: Boolean) {
        if (isToggle) {
            if (match.away_team.is_digged) {
                cancelDigAway(match)
            } else {
                digAway(match)
            }
        }
        EventManager.post(MatchSubscribeChangeEvent(action = DataAction.UPDATE, match = match, extra = MatchSubscribeChangeEvent.EXTRA.UPDATE_DIG_AWAY_TEAM))
    }

    fun digAway(match: WorldcupMatch) {
        if (match.away_team.is_digged) {
            return
        }
        val actionType = DataDictionary.SportActionType.DIG_AWAY
        match.away_team.is_digged = true
        match.away_team.dig_count += 1
        cancelDigHome(match)
        Remote.postSportsUserAction(actionType = actionType.value, itemId = match.match_id)
                .io().onErrorReturn { -1 }.subscribe()
    }

    fun cancelDigAway(match: WorldcupMatch) {
        if (!match.away_team.is_digged) {
            return
        }
        val actionType = DataDictionary.SportActionType.CANCEL_DIG_AWAY
        match.away_team.is_digged = false
        if (match.away_team.dig_count > 0) {
            match.away_team.dig_count -= 1
        }
        Remote.postSportsUserAction(actionType = actionType.value, itemId = match.match_id)
                .io().onErrorReturn { -1 }.subscribe()
    }

}