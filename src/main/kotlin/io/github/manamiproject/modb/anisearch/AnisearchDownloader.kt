package io.github.manamiproject.modb.anisearch

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.downloader.Downloader
import io.github.manamiproject.modb.core.httpclient.DefaultHttpClient
import io.github.manamiproject.modb.core.httpclient.HttpClient
import io.github.manamiproject.modb.core.logging.LoggerDelegate

/**
 * Downloads anime data from anisearch.com
 * @since 1.0.0
 * @param config Configuration for downloading data.
 * @param httpClient To actually download the anime data.
 */
public class AnisearchDownloader(
    private val config: MetaDataProviderConfig = AnisearchConfig,
    private val httpClient: HttpClient = DefaultHttpClient(),
): Downloader {

    override fun download(id: AnimeId, onDeadEntry: (AnimeId) -> Unit): String {
        log.debug("Downloading [anisearchId={}]", id)

        val response = httpClient.get(
            url = config.buildDataDownloadLink(id).toURL(),
            headers = mapOf("host" to listOf("www.${config.hostname()}"))
        )

        check(response.body.isNotBlank()) { "Response body was blank for [anisearchId=$id] with response code [${response.code}]" }

        return when(response.code) {
            200 -> response.body
            else -> throw IllegalStateException("Unable to determine the correct case for [anisearchId=$id], [responseCode=${response.code}]")
        }
    }

    private companion object {
        private val log by LoggerDelegate()
    }
}