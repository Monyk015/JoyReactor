package y2k.joyreactor.common

import y2k.joyreactor.common.http.CookieStorage
import y2k.joyreactor.common.http.DefaultHttpClient
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.images.MultiTryDownloader
import y2k.joyreactor.platform.NavigationService
import y2k.joyreactor.services.*
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.repository.IDataContext
import y2k.joyreactor.services.repository.ormlite.OrmLiteDataContext
import y2k.joyreactor.services.requests.*
import y2k.joyreactor.services.synchronizers.MyTagFetcher
import y2k.joyreactor.services.synchronizers.PostMerger
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher
import y2k.joyreactor.viewmodel.*
import java.util.*
import kotlin.reflect.KClass

/**
 * Created by y2k on 07/12/15.
 */
object ServiceLocator {

    private val map = HashMap <KClass<*>, () -> Any>()

    init {
        registerSingleton<HttpClient> { DefaultHttpClient(CookieStorage(resolve())) }
        register { PostViewModel(resolve(), resolve(), resolve()) }
        register { NavigationService.instance }
        register { ThreadsViewModel(resolve(), resolve(), resolve()) }
        register { MessagesViewModel(resolve(), resolve()) }

        register { BroadcastService }

        register { TokenRequest(resolve()) }
        register { LikePostRequest(resolve(), resolve()) }
        register { MessageListRequest(resolve(), resolve()) }
        register { PostMerger(resolve(), resolve()) }
        register { MemoryBuffer }
        register { MyTagFetcher(resolve(), resolve(), resolve()) }
        register { PrivateMessageFetcher(resolve(), resolve()) }
        register { PostsForTagRequest(resolve()) }
        register { AddTagRequest(resolve()) }
        register { UserNameRequest(resolve()) }
        register { TagsForUserRequest(resolve(), resolve()) }
        register { OriginalImageRequestFactory(resolve(), resolve()) }
        register { PostRequest(resolve()) }
        register { ProfileRequestFactory(resolve()) }
        register { LoginRequestFactory(resolve()) }
        register { SendMessageRequest(resolve()) }

        register { PostService(resolve(), resolve(), resolve(), resolve(), resolve(), resolve()) }
        register { TagService(resolve(), resolve(), resolve(), resolve()) }
        register { UserService(resolve(), resolve(), resolve(), resolve()) }
        register { ProfileService(resolve(), resolve(), resolve()) }
        register { UserMessagesService(resolve(), resolve(), resolve()) }
        register { CommentService(resolve(), resolve(), resolve()) }
        register { LoginViewModel(resolve(), resolve()) }
        register { MenuViewModel(resolve(), resolve(), resolve()) }
        register { GalleryViewModel(resolve()) }
        register { ImageViewModel(resolve()) }
        register { VideoViewModel(resolve(), resolve()) }
        register { ProfileViewModel(resolve(), resolve()) }
        register { AddTagViewModel(resolve(), resolve()) }
        register { PostListViewModel(resolve(), resolve(), resolve(), resolve()) }
        register { PostLikeViewModel(resolve(), resolve()) }

        register { MultiTryDownloader(resolve()) }

        registerSingleton<IDataContext> { OrmLiteDataContext(resolve()) }
        register { DataContext.Factory(resolve()) }
    }

    // ==========================================
    // Private methods
    // ==========================================

    inline fun <reified T : Any> resolve(lifeCycleService: LifeCycleService): T {
        register { lifeCycleService }
        try {
            return resolve()
        } finally {
            unregister(LifeCycleService::class)
        }
    }

    inline fun <reified T : Any> resolve(): T {
        return resolve(T::class)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> resolve(type: KClass<T>): T {
        try {
            return map[type]?.let { it() as T } ?: type.java.newInstance()
        } catch (e: InstantiationException) {
            throw IllegalArgumentException("Can't resolve type $type", e)
        }
    }

    inline fun <reified T : Any> registerSingleton(noinline factory: () -> T) {
        var singleton: T? = null
        register(T::class, {
            if (singleton == null) singleton = factory()
            singleton!!
        })
    }

    inline fun <reified T : Any> register(noinline factory: () -> T) {
        register(T::class, factory)
    }

    fun <T : Any> register(type: KClass<T>, factory: () -> T) {
        map[type] = factory
    }

    fun <T : Any> unregister(type: KClass<T>) {
        map.remove(type)
    }
}