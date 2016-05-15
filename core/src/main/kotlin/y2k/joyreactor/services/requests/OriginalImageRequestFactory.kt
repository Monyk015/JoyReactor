package y2k.joyreactor.services.requests

import rx.Observable
import rx.schedulers.Schedulers
import y2k.joyreactor.common.ForegroundScheduler
import y2k.joyreactor.common.PartialResult
import y2k.joyreactor.common.http.HttpClient
import y2k.joyreactor.common.ioObservable
import y2k.joyreactor.common.platform.Platform
import java.io.File
import java.util.regex.Pattern

/**
 * Created by y2k on 16/10/15.
 */
class OriginalImageRequestFactory(
    private val httpClient: HttpClient,
    private val platform: Platform) {

    fun request(imageUrl: String): Observable<File> {
        return ioObservable {
            val file = File(platform.currentDirectory, "" + imageUrl.hashCode() + "." + getExtension(imageUrl))
            if (!file.exists()) {
                try {
                    httpClient.downloadToFile(imageUrl, file, null)
                } catch (e: Exception) {
                    file.delete()
                    throw e
                }
            }
            file
        }
    }

    fun requestPartial(imageUrl: String): Observable<PartialResult<File>> {
        return Observable.create<PartialResult<File>> { subscriber ->
            // TODO
            val file = File(platform.currentDirectory, "" + imageUrl.hashCode() + "." + getExtension(imageUrl))
            if (file.exists()) subscriber.onNext(PartialResult.complete(file))

            try {
                httpClient.downloadToFile(imageUrl, file) {
                    progress, max ->
                    subscriber.onNext(PartialResult.inProgress<File>(progress, max))
                }
                subscriber.onNext(PartialResult.complete(file))
            } catch (e: Exception) {
                file.delete()
                subscriber.onError(e)
            }
        }.subscribeOn(Schedulers.io()).observeOn(ForegroundScheduler.instance)
    }

    private fun getExtension(imageUrl: String): String {
        val fm = Pattern.compile("format=([^&]+)").matcher(imageUrl)
        if (fm.find()) return fm.group(1)

        val m = Pattern.compile("\\.([^\\.]+)$").matcher(imageUrl)
        if (!m.find()) throw IllegalStateException("can't find extension from url " + imageUrl)
        return m.group(1)
    }
}