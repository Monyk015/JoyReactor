package y2k.joyreactor.services

import rx.Observable
import y2k.joyreactor.common.concatAndRepeat
import y2k.joyreactor.model.Message
import y2k.joyreactor.services.repository.DataContext
import y2k.joyreactor.services.requests.SendMessageRequest
import y2k.joyreactor.services.synchronizers.PrivateMessageFetcher

/**
 * Created by y2k on 12/8/15.
 */
class UserMessagesService(
    private val sendRequest: SendMessageRequest,
    private val fetcher: PrivateMessageFetcher,
    private val entities: DataContext.Factory) {

    fun sendNewMessage(username: String, message: String): Observable<List<Message>> {
        return sendRequest
            .request(username, message)
            .flatMap { fetcher.execute() }
            .flatMap { getMessages(username) }
    }

    fun getThreads(): Observable<List<Message>> {
        return entities
            .applyUse {
                Messages
                    .groupBy("userName", orderProp = "date")
                    .sortedByDescending { it.date }
            }
            .concatAndRepeat(fetcher.execute())
    }

    fun getMessages(username: String): Observable<List<Message>> {
        return entities.applyUse {
            Messages
                .filter("userName" to username)
                .sortedByDescending { it.date }
        }
    }
}