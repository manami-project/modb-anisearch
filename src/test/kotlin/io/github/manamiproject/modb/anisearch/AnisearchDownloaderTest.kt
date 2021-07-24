package io.github.manamiproject.modb.anisearch

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.Hostname
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.EMPTY
import io.github.manamiproject.modb.core.extensions.toAnimeId
import io.github.manamiproject.modb.test.MockServerTestCase
import io.github.manamiproject.modb.test.WireMockServerCreator
import io.github.manamiproject.modb.test.shouldNotBeInvoked
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

internal class AnisearchDownloaderTest : MockServerTestCase<WireMockServer> by WireMockServerCreator() {

    @Test
    fun `successfully load an entry`() {
        // given
        val id = 1535

        val testAnisearchConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLink(id: AnimeId): URI = URI("http://localhost:$port/anime/$id")
            override fun buildDataDownloadLink(id: String): URI = buildAnimeLink(id)
            override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
        }

        val responseBody = "<html><head/><body></body></html>"

        serverInstance.stubFor(
            get(urlPathEqualTo("/anime/$id")).willReturn(
                aResponse()
                    .withHeader("Content-Type", "text/html")
                    .withStatus(200)
                    .withBody(responseBody)
            )
        )

        val anisearchDownloader = AnisearchDownloader(testAnisearchConfig)

        // when
        val result = anisearchDownloader.download(id = id.toAnimeId(), onDeadEntry = { shouldNotBeInvoked() })

        // then
        assertThat(result).isEqualTo(responseBody)
    }

    @Test
    fun `throws an exception if the response body is empty`() {
        // given
        val id = 1535

        val testAnisearchConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
            override fun buildDataDownloadLink(id: String): URI = URI("http://${hostname()}:$port/anime/$id")
            override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
        }

        serverInstance.stubFor(
            get(urlPathEqualTo("/anime/$id")).willReturn(
                aResponse()
                    .withHeader("Content-Type", "text/html")
                    .withStatus(200)
                    .withBody(EMPTY)
            )
        )

        val anisearchDownloader = AnisearchDownloader(testAnisearchConfig)

        // when
        val result = org.junit.jupiter.api.assertThrows<IllegalStateException> {
            anisearchDownloader.download(id.toAnimeId()) { shouldNotBeInvoked() }
        }

        // then
        assertThat(result).hasMessage("Response body was blank for [anisearchId=1535] with response code [200]")
    }

    @Test
    fun `unhandled response code`() {
        // given
        val id = 1535

        val testAnisearchConfig = object: MetaDataProviderConfig by MetaDataProviderTestConfig {
            override fun hostname(): Hostname = "localhost"
            override fun buildAnimeLink(id: AnimeId): URI = URI("http://localhost:$port/anime/$id")
            override fun buildDataDownloadLink(id: String): URI = buildAnimeLink(id)
            override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
        }

        serverInstance.stubFor(
            get(urlPathEqualTo("/anime/$id"))
                .willReturn(
                    aResponse()
                        .withHeader("Content-Type", "text/html")
                        .withStatus(502)
                        .withBody("<html></html>")
                )
        )

        val anisearchDownloader = AnisearchDownloader(testAnisearchConfig)

        // when
        val result = org.junit.jupiter.api.assertThrows<IllegalStateException> {
            anisearchDownloader.download(id = id.toAnimeId(), onDeadEntry = { shouldNotBeInvoked() })
        }

        // then
        assertThat(result).hasMessage("Unable to determine the correct case for [anisearchId=$id], [responseCode=502]")
    }
}