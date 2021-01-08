package com.mynews.app.news.data.source.remote

import android.location.Address
import com.mynews.app.news.bean.*
import com.mynews.app.news.bean.base.BaseNewsBean
import com.mynews.app.news.bean.list.*
import com.mynews.app.news.data.DataManager
import com.mynews.app.news.data.adapter.json.GsonBaseNewsBeanAdapters
import com.mynews.app.news.data.adapter.json.GsonInboxMessageBeanAdapters
import com.mynews.app.news.data.source.remote.http.api.*
import com.mynews.app.news.data.source.remote.http.api.global.DownloadGlobalAPI
import com.mynews.app.news.proto.AppLog
import com.mynews.app.news.push.fcm.FCMMessagingService.Companion.CHANNEL_ID
import com.mynews.app.news.push.fcm.FCMMessagingService.Companion.isNotificationChannelEnabled
import com.mynews.app.news.util.checkUDID
import com.mynews.common.core.CoreApp
import com.mynews.common.core.json.gson.util.CoreGsonUtils
import com.mynews.common.core.net.http.HttpManager
import com.mynews.common.core.net.http.bean.APIResponse
import com.mynews.common.core.net.http.extension.create
import com.mynews.common.core.net.http.extension.extractData
import com.mynews.common.core.net.http.factory.ProtoGsonConverterFactory
import com.mynews.common.extension.prompt.Prompt
import com.appsflyer.AppsFlyerLib
import com.crashlytics.android.Crashlytics
import io.reactivex.Observable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Url
import java.io.File

open class RemoteSource {

    val mGsonRetrofit by lazy {
        val gson = CoreGsonUtils.getNewInstanceBuildFromDefault()
                .registerTypeAdapter(BaseNewsBean::class.java, GsonBaseNewsBeanAdapters())
                .registerTypeAdapter(InboxMessage::class.java, GsonInboxMessageBeanAdapters())
//                .registerTypeAdapter(ListComment::class.java, GsonListCommentBeanAdapter())
                .create()
        HttpManager.getBaseRetrofit().newBuilder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
    }

    val mProtoGsonRetrofit by lazy {
        HttpManager.getBaseRetrofit().newBuilder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(ProtoGsonConverterFactory.create(CoreGsonUtils.getDefaultInstance()))
                .build()
    }

    private val mArticleAPI by lazy { mGsonRetrofit.create(ArticleAPI::class) }
    private val mArticleUserActionAPI by lazy { mGsonRetrofit.create(UserActionAPI::class) }
    private val mChannelAPI by lazy { mGsonRetrofit.create(ChannelAPI::class) }
    private val mWeatherAPI by lazy { mGsonRetrofit.create(WeatherAPI::class) }
    private val mFeedbackAPI by lazy { mGsonRetrofit.create(FeedbackAPI::class) }
    private val mUserAPI by lazy { mGsonRetrofit.create(UserAPI::class) }
    private val mImageAPI by lazy { mGsonRetrofit.create(ImageAPI::class) }
    private val mPushAPI by lazy { mGsonRetrofit.create(PushAPI::class) }
    private val mAppConfigAPI by lazy { mGsonRetrofit.create(AppConfigAPI::class) }
    private val mAppLogAPI by lazy { mGsonRetrofit.create(AppLogAPI::class) }
    private val mVideoAPI by lazy { mGsonRetrofit.create(VideoAPI::class) }
    private val mPromptAPI by lazy { mGsonRetrofit.create(PromptAPI::class) }
    private val mCollectAPI by lazy { mGsonRetrofit.create(CollectAPI::class) }
    private val mCommentAPI by lazy { mGsonRetrofit.create(CommentAPI::class) }
    private val mPublicReportAPI by lazy { mGsonRetrofit.create(PublicReportAPI::class) }
    private val mAppLogProtoBufAPI by lazy { mProtoGsonRetrofit.create(AppLogAPI::class) }
    private val mSportsAPI by lazy { mGsonRetrofit.create(SportsAPI::class) }
    private val mPLinkAPI by lazy { mGsonRetrofit.create(PLinkAPI::class) }
    private val mInboxAPI by lazy { mGsonRetrofit.create(InboxAPI::class) }
    private val mSearchAPI by lazy { mGsonRetrofit.create(SearchAPI::class) }
    private val mAdhocAPI by lazy { mGsonRetrofit.create(AdhocAPI::class) }
    private val mStatisticAPI by lazy { mGsonRetrofit.create(StatisticAPI::class) }
    private val mRecommandAPI by lazy { mGsonRetrofit.create(RecommandAPI::class) }

    fun pLinkReport(pLinkRequest: PLinkRequest): Observable<PLinkResponse> {
        return mPLinkAPI.report(pLinkRequest).extractData()
    }

    fun getPlayer(playerId: String): Observable<WorldcupPlayer> {
        return mSportsAPI.getPlayer(playerId = playerId).extractData()
    }

    fun getTeam(teamId: String): Observable<WorldcupTeam> {
        return mSportsAPI.getTeam(teamId = teamId).extractData()
    }

    fun getMatch(matchId: String): Observable<WorldcupMatch> {
        return mSportsAPI.getMatch(matchId = matchId).extractData()
    }

    fun getFavorite(pageNo: Int): Observable<ListFavorite> {
        return mCollectAPI.getFavorite(pageNo = pageNo).extractData()
    }

    fun getMyCommentlist(pageNo: Int, lastCommentId: String): Observable<ListComment> {
        return mCommentAPI.getMyComments(pageNo = pageNo, lastCommentId = lastCommentId).extractData()
    }

    fun getFeed(pub: String, chid: String, minEmitTime: Long?, maxEmitTime: Long?, channelType: Int, refreshCount: Int, loadMoreCount: Int): Observable<ListNews> {
        return mArticleAPI.feed(pub = pub,
                chid = chid,
                minEmitTime = minEmitTime,
                maxEmitTime = maxEmitTime,
                channelType = channelType,
                refreshCount = refreshCount,
                loadMoreCount = loadMoreCount,
                totalCount = refreshCount + loadMoreCount).checkUDID().extractData()
    }

    fun getArticleDetail(aid: String): Observable<ArticleDetail> {
        return mArticleAPI.detail(aid).extractData()
    }

    fun getArticleContent(aid: String): Observable<ArticleContentWrapper> {
        return mArticleAPI.content(aid).extractData()
    }

    fun getArticleInformation(aid: String): Observable<ArticleInformation> {
        return mArticleAPI.information(aid).extractData()
    }

//    fun getWeatherSimpleInfo(cityCode: String): Observable<WeatherInfo> {
//        return mWeatherAPI.getSimpleInfo(cityCode).extractData()
//    }

    fun getWeatherCityList(): Observable<ListRegion> {
        return mWeatherAPI.getCityList().extractData()
    }

    fun getFeedbackMessage(): Observable<ListFeedback> {
        return mFeedbackAPI.getMessage().extractData()
    }

    //普通获取channelList方法
    fun getChannelList(): Observable<RecommendChannel> {
        return mChannelAPI.list().checkUDID().extractData()
    }

    //当用户在登陆或改变性别或者改变年龄段后获更新channel
    fun getServerChannelList(): Observable<RecommendChannel> {
        return mChannelAPI.serverList().checkUDID().extractData()
    }

    fun saveClientChannelList(input: ChannelInput): Observable<Any> {
        return mChannelAPI.saveClientChannelList(input).checkUDID().extractData()
    }

    fun getUniqueDeviceId(): Observable<User> {
        return mUserAPI.getUniqueDeviceId().extractData()
    }

    fun login(info: LoginRequestInfo): Observable<LoginResponseInfo> {
        return mUserAPI.login(info).extractData()
    }

    fun logout(): Observable<LogoutResponseInfo> {
        return mUserAPI.logout().extractData()
    }

    fun syncProfileStatus(): Observable<SyncProfileStatus> {
        return mUserAPI.syncProfileStatus().extractData()
    }

    fun updateProfile(profile: UpdateProfile): Observable<Account> {
        return mUserAPI.updateProfile(profile).extractData()
    }

    fun selectInfo(selectInfo: SelectInfo): Observable<SelectInfo> {
        return mUserAPI.selectInfo(selectInfo).extractData()
    }

    fun getProfile(): Observable<UserProfileWrapper> {
        return mUserAPI.profile().extractData()
    }

    fun getFeedbackHasUnread(): Observable<FeedbackUnread> {
        return mFeedbackAPI.getHasUnread().extractData()
    }

    fun sendFeedbackMessage(message: FeedbackMessage): Observable<Any> {
        return mFeedbackAPI.sendMessage(message).extractData()
    }

    fun uploadImageFile(file: File): Observable<UploadImageResult> {
        val body = RequestBody.create(MediaType.parse("image/*"), file)
        val part = MultipartBody.Part.createFormData("file", file.name, body)
        return mImageAPI.upload(part).extractData()
    }

    fun checkCity(address: Address): Observable<City> {
        return mWeatherAPI.checkCity(
                address.postalCode,
                address.thoroughfare, address.subThoroughfare,
                address.locality, address.subLocality,
                address.adminArea, address.subAdminArea,
                address.countryCode, address.countryName).extractData()
    }

    fun postArticleUserAction(actionType: Int, aid: String, pk: String): Observable<Any> {
        val action = ArticleUserAction(listOf(Ops(actionType = actionType, aid = aid, pk = pk)))
        return mArticleUserActionAPI.postArticleUserAction(action).extractData()
    }

    fun postArticleUserActions(opss: List<Ops>): Observable<Any> {
        val action = ArticleUserAction(opss)
        return mArticleUserActionAPI.postArticleUserAction(action).extractData()
    }

    fun submitPushToken(token: String): Observable<Any> {
        val isEnabled = isNotificationChannelEnabled(CoreApp.getInstance(), CHANNEL_ID)
        val enabled = if (isEnabled) {
            1
        } else {
            0
        }
        return mPushAPI.submitToken(SubmitPushToken(token, enabled)).extractData()
    }

    fun getAppConfig(): Observable<AppConfig> {
        val afId = try {
            AppsFlyerLib.getInstance().getAppsFlyerUID(CoreApp.getInstance()) ?: "unknown"
        } catch (e: Exception) {
            Crashlytics.logException(e)
            "unknown"
        }
        return mAppConfigAPI.config(afId).extractData()
    }

    fun getAppConfigResource(): Observable<AppConfigResource> {
        return mAppConfigAPI.resource().extractData()
    }

    fun getAppConfigShare(url: String): Observable<AppConfigShare> {
        return mAppConfigAPI.share(url).extractData()
    }

    fun getAppLogConfig(): Observable<AppLogConfig> {
        return mAppLogAPI.config().extractData()
    }

    fun postAppLog(appLog: AppLog.NewsAppLog): Observable<Any> {
        return mAppLogProtoBufAPI.log(appLog).extractData()
    }

    fun pushArrival(@Body arrival: PushArrival): Observable<Any> {
        return mAppLogAPI.pushArrival(arrival).extractData()
    }

    fun getYouTubeVideoUrl(youTubeId: String): Observable<VideoUrls> {
        return mVideoAPI.url(youTubeId).extractData()
    }

    fun getPrompt(): Observable<Prompt> {
        return mPromptAPI.prompt().extractData()
    }

    fun download(@Url url: String): Observable<ResponseBody> {
        return HttpManager.getBaseRetrofit().newBuilder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(DownloadGlobalAPI::class)
                .download(url)
    }

    fun loadComment(targetType: String, targetId: String): Observable<ListComment> {
        return mCommentAPI.load(targetType, targetId).extractData()
    }

    fun loadCommentMore(targetType: String, targetId: String, pageNo: Int, lastCommentId: String): Observable<ListComment> {
        return mCommentAPI.loadMore(targetType, targetId, pageNo, lastCommentId).extractData()
    }

    fun postComment(commentPost: CommentPostRequestInfo): Observable<CommentPostResponseInfo> {
        return mCommentAPI.post(commentPost).extractData()
    }

    fun deleteComment(commentDelete: CommentDelete): Observable<Any> {
        return mCommentAPI.delete(commentDelete).extractData()
    }

    fun postCommentUserAction(actionType: Int, cid: String, pk: String): Observable<Any> {
        val action = ArticleUserAction(listOf(Ops(actionType = actionType, cid = cid, pk = pk)))
        return mCommentAPI.postUserAction(action).extractData()
    }

    fun postPublicReport(post: ReportPostRequestInfo): Observable<Any> {
        return mPublicReportAPI.post(post).extractData()
    }

    fun postSportsUserAction(actionType: Int, itemId: String, pk: String = DataManager.Local.getUniqueDeviceId()): Observable<Any> {
        val action = SportsUserAction(listOf(Ops(actionType = actionType, item_id = itemId, pk = pk)))
        return mSportsAPI.postUserAction(action).extractData()
    }

    fun sportsStatus(): Observable<SportsStatus> {
        return mSportsAPI.status().extractData()
    }

    fun getSportsBoard(pageNo: Int, chid: String, lastCommentId: String?, type: Int?): Observable<WorldCupBoard> {
        return mSportsAPI.board(pageNo, chid, lastCommentId, type).extractData()
    }

    fun getInboxCount(): Observable<InboxCountResponse> {
        return mInboxAPI.count().extractData()
    }

    fun loadInbox(type: Int, lastId: String?): Observable<ListInbox> {
        return mInboxAPI.load(type, lastId).extractData()
    }

    fun loadPushHistory(lastId: String?): Observable<ListInbox> {
        return mInboxAPI.loadPush(lastId).extractData()
    }

    fun getSearchHotwords(type: Int): Observable<ListHotWords> {
        return mSearchAPI.getHotWords(type).extractData()
    }

    fun getHeadlineCount(minEmitTime: Long): Observable<ArticleHeadlineCount> {
        return mArticleAPI.headlineCount(minEmitTime).extractData()
    }

    fun postDeviceAppMap(deviceApp: DeviceAppRequestInfo): Observable<Any> {
        return mAdhocAPI.deviceApp(deviceApp).extractData()
    }

    fun reportDau(tag: Int): Observable<Any> {
        return mStatisticAPI.report(tag).extractData()
    }

    fun getUserInterests(): Observable<APIResponse<InterestData>> {
        return mRecommandAPI.getInterest()
    }

    fun sendUserInterest(tags: HashMap<String, Any>): Observable<APIResponse<Any>> {
        return mRecommandAPI.sendInterest(tags)
    }

}