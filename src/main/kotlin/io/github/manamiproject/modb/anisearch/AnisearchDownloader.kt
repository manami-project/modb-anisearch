package io.github.manamiproject.modb.anisearch

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_NETWORK
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.logging.LoggerDelegate
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Downloads anime data from anisearch.com
 * @since 1.0.0
 * @param config Configuration for downloading data.
 * @param httpClient To actually download the anime data.
 */
public class AnisearchDownloader(
    private val config: MetaDataProviderConfig,
    private val httpClient: HttpClient = DefaultHttpClient(isTestContext = config.isTestContext()),
): Downloader {

    @Deprecated("Use coroutines", ReplaceWith(EMPTY))
    override fun download(id: AnimeId, onDeadEntry: (AnimeId) -> Unit): String = runBlocking {
        downloadSuspendable(id, onDeadEntry)
    }

    override suspend fun downloadSuspendable(id: AnimeId, onDeadEntry: suspend (AnimeId) -> Unit): String = withContext(LIMITED_NETWORK) {
        log.debug { "Downloading [anisearchId=$id]" }

        val response = httpClient.getSuspedable(
            url = config.buildDataDownloadLink(id).toURL(),
            headers = mapOf("host" to listOf("www.${config.hostname()}")),
        )

        check(response.body.isNotBlank()) { "Response body was blank for [anisearchId=$id] with response code [${response.code}]" }

        return@withContext when(response.code) {
            200 -> response.body
            404 -> {
                onDeadEntry.invoke(id)
                EMPTY
            }
            else -> throw IllegalStateException("Unable to determine the correct case for [anisearchId=$id], [responseCode=${response.code}]")
        }
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}