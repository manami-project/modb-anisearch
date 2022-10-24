package io.github.manamiproject.modb.anisearch

import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.FileSuffix
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.core.models.Anime
import io.github.manamiproject.modb.core.models.Anime.Status.*
import io.github.manamiproject.modb.core.models.Anime.Type.*
import io.github.manamiproject.modb.core.models.Anime.Type.UNKNOWN
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.*
import io.github.manamiproject.modb.test.exceptionExpected
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.tempDirectory
import io.github.manamiproject.modb.test.testResource
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.net.URI
import kotlin.io.path.copyTo

internal class AnisearchConverterTest {

    @Nested
    inner class TitleTests {

        @Test
        fun `title containing special chars`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/title/special_chars.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.title).isEqualTo("22/7: 8+3=?")
            }
        }

        @Test
        fun `no suffix for title`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/title/no_suffix_for_title.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.title).isEqualTo(".hack//G.U. Returner")
            }
        }
    }

    @Nested
    inner class PictureAndThumbnailTests {

        @Test
        fun `neither picture nor thumbnail`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.picture).isEqualTo(URI("https://www.anisearch.com/images/anime/cover/0_300.webp"))
                assertThat(result.thumbnail).isEqualTo(URI("https://www.anisearch.com/images/anime/cover/full/0.webp"))
            }
        }

        @Test
        fun `picture and thumbnail`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.picture).isEqualTo(URI("https://www.anisearch.com/images/anime/cover/3/3633_300.webp"))
                assertThat(result.thumbnail).isEqualTo(URI("https://www.anisearch.com/images/anime/cover/full/3/3633.webp"))
            }
        }
    }

    @Nested
    inner class SourcesTests {

        @Test
        fun `extract id 16498`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/sources/3633.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.sources).containsExactly(URI("https://anisearch.com/anime/3633"))
            }
        }
    }

    @Nested
    inner class TypeTests {

        @Test
        fun `'bonus' is mapped to SPECIAL`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/type/bonus.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.type).isEqualTo(SPECIAL)
            }
        }

        @Test
        fun `'cm' is mapped to 'SPECIAL'`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/type/cm.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.type).isEqualTo(SPECIAL)
            }
        }

        @Test
        fun `'movie' is mapped to Movie`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/type/movie.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.type).isEqualTo(MOVIE)
            }
        }

        @Test
        fun `'music video' is mapped to SPECIAL`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/type/music-video.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.type).isEqualTo(SPECIAL)
            }
        }

        @Test
        fun `other' is mapped to UNKNOWN`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/type/other.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.type).isEqualTo(UNKNOWN)
            }
        }

        @Test
        fun `'ova' is mapped to OVA`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/type/ova.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.type).isEqualTo(OVA)
            }
        }

        @Test
        fun `tv-series' is mapped to TV`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/type/tv-series.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.type).isEqualTo(TV)
            }
        }

        @Test
        fun `tv-special' is mapped to TV`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/type/tv-special.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.type).isEqualTo(TV)
            }
        }

        @Test
        fun `unknown' is mapped to UNKNOWN`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/type/unknown.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.type).isEqualTo(UNKNOWN)
            }
        }

        @Test
        fun `web' is mapped to ONA`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/type/web.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.type).isEqualTo(ONA)
            }
        }
    }

    @Nested
    inner class TagsTests {

        @Test
        fun `extract multiple tags`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/tags/multiple_tags.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.tags).containsExactly(
                    "action",
                    "action comedy",
                    "adventure",
                    "alternative world",
                    "comedy",
                    "demons",
                    "ecchi",
                    "fantasy",
                    "guns",
                    "harem",
                    "knight",
                    "magic",
                    "parody",
                    "robots & androids",
                    "science-fiction",
                    "swords & co",
                )
            }
        }

        @Test
        fun `extract exactly one tag`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/tags/one_tag.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.tags).containsExactly("slice of life")
            }
        }

        @Test
        fun `no tags available`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/tags/no_tags.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.tags).isEmpty()
            }
        }
    }

    @Nested
    inner class EpisodesTests {

        @Test
        fun `unknown number of episodes defaults to 1`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/episodes/unknown.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.episodes).isOne()
            }
        }

        @Test
        fun `1 episode`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/episodes/1.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.episodes).isOne()
            }
        }

        @Test
        fun `10 episodes`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/episodes/10.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.episodes).isEqualTo(10)
            }
        }

        @Test
        fun `100 episodes`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/episodes/100.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.episodes).isEqualTo(100)
            }
        }

        @Test
        fun `1818 episodes`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/episodes/1818.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.episodes).isEqualTo(1818)
            }
        }

        @Test
        fun `although not a double in json the number of episodes is deserialized to kotlin type double`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/episodes/type-is-double.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.episodes).isEqualTo(6)
            }
        }
    }

    @Nested
    inner class DurationTests {

        @Test
        fun `1 hour`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/duration/1_hour.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.duration).isEqualTo(Duration(1, HOURS))
            }
        }

        @Test
        fun `1 minute`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/duration/1_minute.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.duration).isEqualTo(Duration(1, MINUTES))
            }
        }

        @Test
        fun `2 hours`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/duration/2_hours.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.duration).isEqualTo(Duration(2, HOURS))
            }
        }

        @Test
        fun `24 minutes per episode`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/duration/24_minutes_per_episode.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.duration).isEqualTo(Duration(24, MINUTES))
            }
        }

        @Test
        fun `63 minutes`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/duration/63_minutes_by_6_episodes.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.duration).isEqualTo(Duration(63, MINUTES))
            }
        }

        @Test
        fun `70 minutes`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/duration/70_minutes.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.duration).isEqualTo(Duration(70, MINUTES))
            }
        }

        @Test
        fun `134 minutes`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/duration/134_minutes.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.duration).isEqualTo(Duration(134, MINUTES))
            }
        }

        @Test
        fun `episodes and duration unknown`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/duration/episodes_and_duration_unknown.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.duration).isEqualTo(Duration(0, SECONDS))
            }
        }

        @Test
        fun `episodes known duration unknown`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/duration/episodes_known_duration_unknown.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.duration).isEqualTo(Duration(0, SECONDS))
            }
        }
    }

    @Nested
    inner class SynonymsTests {

        @Test
        fun `multiple synonyms`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/synonyms/multiple_synonyms.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.synonyms).containsExactly(
                    "Bésame Licia",
                    "Embrasse-moi Lucile",
                    "Kiss Me Licia",
                    "Love Me Night",
                    "Love in Rock 'n Roll",
                    "Lucile, Amour et Rock'n'Roll",
                    "Rock'n' Roll Kids",
                    "愛してナイト",
                )
            }
        }

        @Test
        fun `no synonyms`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/synonyms/no_synonyms.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.synonyms).isEmpty()
            }
        }

        @Test
        fun `single synonym`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/synonyms/single_synonym.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.synonyms).containsExactly(
                    "センコロール 3",
                )
            }
        }

        @Test
        fun `single synonym and romanji alteration`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/synonyms/romanji_alteration.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.synonyms).containsExactly(
                    "Chikyūgai Shōnen Shōjo",
                    "Extra-Terrestrial Boys & Girls",
                    "Extraterrestial Boys and Girls",
                    "Jóvenes en órbita",
                    "Notre jeunesse en orbite",
                    "The Orbital Children",
                    "地球外少年少女"
                )
            }
        }

        @Test
        fun `correctly separate hidden synonyms using 11197 as an example`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/synonyms/hidden_synonyms_11197.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.synonyms).containsExactly(
                    "DanMachi OVA",
                    "Danmachi OAV",
                    "Danmachi: Is It Wrong to Try to Pick Up Girls in a Dungeon? Familia Myth OVA",
                    "Danmachi: Is It Wrong to Try to Pick Up Girls in a Dungeon? Ist es falsch, im Dungeon in heißen Quellen zu baden?",
                    "Danmachi: ¿qué Tiene De Malo Intentar Buscar Unos Baños Termales En Una Mazmorra?",
                    "Dungeon ni Deai o Motomeru no wa Machigatte Iru Darouka: Familia Myth OVA",
                    "Dungeon ni Deai o Motomeru no wa Machigatte Iru Darō ka: Familia Myth - Dungeon ni Onsen o Motomeru no wa Machigatte Iru Darō ka",
                    "Dungeon ni Deai wo Motomeru no wa Machigatte Iru Darou ka: Familia Myth - Dungeon ni Onsen wo Motomeru no wa Machigatte Iru Darou ka",
                    "Dungeon ni Deai wo Motomeru no wa Machigatte Iru Darouka: Familia Myth OVA",
                    "Is It Wrong to Try to Pick Up Girls in a Dungeon? Familia Myth OVA",
                    "Is It Wrong to Try to Pick Up Girls in a Dungeon? Is It Wrong to Expect a Hot Spring in a Dungeon?",
                    "Is It Wrong to Try to Pick Up Girls in a Dungeon? Is It Wrong to Try to Soak in a Hot Spring in a Dungeon?",
                    "ダンジョンに出会いを求めるのは間違っているだろうか FAMILIA MYTH ダンジョンに温泉を求めるのは間違っているだろうか",
                )
            }
        }

        @Test
        fun `correctly separate hidden synonyms using 8724 as an example`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/synonyms/hidden_synonyms_8724.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.synonyms).containsExactly(
                    "My Teen Romantic Comedy SNAFU OVA",
                    "My Youth Romantic Comedy Is Wrong as I Expected. OVA",
                    "Oregairu OVA",
                    "Yahari Ore no Seishun Love Come wa Machigatte Iru. OAD",
                    "Yahari Ore no Seishun Love Comedy wa Machigatteiru.: Kochira to Shite mo Karera Kanojora no Yukusue ni Sachi Ookaran Koto o Negawazaru o Enai.",
                    "Yahari Ore no Seishun Love Comedy wa Machigatteiru.: Kochira to Shite mo Karera Kanojora no Yukusue ni Sachi Ookaran Koto wo Negawazaru wo Enai.",
                    "Yahari Ore no Seishun Lovecome wa Machigatte Iru.: Kochira to Shite mo Karera Kanojora no Yukusue ni Sachi Ookaran Koto wo Negawazaru wo Enai.",
                    "Yahari Ore no Seishun Lovecome wa Machigatte Iru.: Kochira to Shite mo Karera Kanojora no Yukusue ni Sachi Ōkaran Koto o Negawazaru o Enai.",
                    "やはり俺の青春ラブコメはまちがっている。「こちらとしても彼ら彼女らの行く末に幸多からんことを願わざるを得ない。」",
                )
            }
        }

        @Test
        fun `synonyms containing named parts are not split`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/synonyms/synonyms_contain_named_parts.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.synonyms).containsExactly(
                    "Extra One Room: Second Season: Hanasaka Yui Experiments / Hanasaka Yui Gets Delivered / Nanahashi Minori Holds a Meeting / Nanahashi Minori Becomes a Big-Shot / Amatsuki Mashiro Becomes a Cat / Amatsuki Mashiro Gives Treatment",
                    "Extra One Room: Second Season: Hanasaka Yui wa Tameshite Miru / Hanasaka Yui wa Okurarete Kuru / Nanahashi Minori wa Kaigi Suru / Nanahashi Minori wa Oomono ni Naru / Amatsuki Mashiro wa Neko ni Naru / Amatsuki Mashiro wa Chiryou Suru",
                    "One Room 2nd Season Extra",
                    "One Room Second Season Special",
                    "One Room セカンドシーズン -extra-",
                )
            }
        }

        @Test
        fun `hidden synonyms and named parts`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/synonyms/hidden_synonyms_and_named_parts.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.synonyms).containsExactly(
                    "Inu x Boku SS Special",
                    "Inu x Boku SS: Miketsukami-kun Henka / Switch / Omamagoto",
                    "Inu x Boku Secret Service: Miketsukami-kun‘s Transformations / Switch / Playing House",
                    "Inu x Boku Secret Service: Miketsukami‘s Metamorphosis / Switch / Playing House",
                    "Inu × Boku SS Special",
                    "Inu × Boku Secret Service: Miketsukami-kun‘s Transformations / Switch / Playing House",
                    "Inu × Boku Secret Service: Miketsukami‘s Metamorphosis",
                    "Inu × Boku Secret Service: Miketsukami‘s Metamorphosis / Switch / Playing House",
                    "Youko x Boku SS Special",
                    "Youko × Boku SS Special",
                    "妖狐×僕SS 御狐神くん変化 ／ スイッチ ／ おままごと",
                )
            }
        }

        @Test
        fun `correctly extract synonyms in italic`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/synonyms/italic.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.synonyms).containsExactly(
                    "Bakugan: Battle Planet Season 4",
                    "Bakugan: Battle Planet Staffel 4",
                    "爆丸エボリューションズ",
                )
            }
        }
    }

    @Nested
    inner class StatusTests {

        @Test
        fun `'aborted' is mapped to 'UNKNOWN'`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/status/aborted.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.status).isEqualTo(Anime.Status.UNKNOWN)
            }
        }

        @Test
        fun `'on hold' is mapped to 'UNKNOWN'`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/status/on_hold.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.status).isEqualTo(Anime.Status.UNKNOWN)
            }
        }

        @Test
        fun `no status is mapped to 'UNKNOWN'`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/status/no_status.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.status).isEqualTo(Anime.Status.UNKNOWN)
            }
        }

        @Test
        fun `'completed' is mapped to 'FINISHED'`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/status/completed.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.status).isEqualTo(FINISHED)
            }
        }

        @Test
        fun `completed is only based on the original release`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/status/completed_in_japan_upcoming_elsewhere.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.status).isEqualTo(FINISHED)
            }
        }

        @Test
        fun `'ongoing' is mapped to ONGOING`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/status/ongoing.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.status).isEqualTo(ONGOING)
            }
        }

        @Test
        fun `'upcoming' is mapped to UPCOMING`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/status/upcoming.html")
                "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(testFile)
                }

                // then
                assertThat(result.status).isEqualTo(UPCOMING)
            }
        }
    }

    @Nested
    inner class AnimeSeasonTests {

        @Nested
        inner class SeasonTests {

            @ParameterizedTest
            @ValueSource(strings = ["01", "02", "03"])
            fun `'jan', 'feb', and 'mar' are mapped to WINTER`(fileName: String) {
                tempDirectory {
                    // given
                    val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                        override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/$fileName.html")
                    "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                    val converter = AnisearchConverter(
                        config = testAnisearchConfig,
                        relationsDir = tempDir,
                    )

                    // when
                    val result = runBlocking {
                        converter.convertSuspendable(testFile)
                    }

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(WINTER)
                }
            }

            @ParameterizedTest
            @ValueSource(strings = ["04", "05", "06"])
            fun `'apr', 'may' and 'jul' are mapped to SPRING`(fileName: String) {
                tempDirectory {
                    // given
                    val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                        override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/$fileName.html")
                    "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                    val converter = AnisearchConverter(
                        config = testAnisearchConfig,
                        relationsDir = tempDir,
                    )

                    // when
                    val result = runBlocking {
                        converter.convertSuspendable(testFile)
                    }

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(SPRING)
                }
            }

            @ParameterizedTest
            @ValueSource(strings = ["07", "08", "09"])
            fun `'jul', 'aug' and 'sep' are mapped to SUMMER`(fileName: String) {
                tempDirectory {
                    // given
                    val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                        override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/$fileName.html")
                    "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                    val converter = AnisearchConverter(
                        config = testAnisearchConfig,
                        relationsDir = tempDir,
                    )

                    // when
                    val result = runBlocking {
                        converter.convertSuspendable(testFile)
                    }

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(SUMMER)
                }
            }

            @ParameterizedTest
            @ValueSource(strings = ["10", "11", "12"])
            fun `'oct', 'nov' and 'dec' are mapped to SUMMER`(fileName: String) {
                tempDirectory {
                    // given
                    val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                        override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/$fileName.html")
                    "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                    val converter = AnisearchConverter(
                        config = testAnisearchConfig,
                        relationsDir = tempDir,
                    )

                    // when
                    val result = runBlocking {
                        converter.convertSuspendable(testFile)
                    }

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(FALL)
                }
            }

            @ParameterizedTest
            @ValueSource(strings = ["year_only", "unknown"])
            fun `if the month cannot be determined use UNDEFINED`(fileName: String) {
                tempDirectory {
                    // given
                    val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                        override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/season/$fileName.html")
                    "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                    val converter = AnisearchConverter(
                        config = testAnisearchConfig,
                        relationsDir = tempDir,
                    )

                    // when
                    val result = runBlocking {
                        converter.convertSuspendable(testFile)
                    }

                    // then
                    assertThat(result.animeSeason.season).isEqualTo(UNDEFINED)
                }
            }
        }

        @Nested
        inner class YearOfPremiereTests {

            @Test
            fun `06 Aug 2021`() {
                tempDirectory {
                    // given
                    val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                        override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/06-aug-2021.html")
                    "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                    val converter = AnisearchConverter(
                        config = testAnisearchConfig,
                        relationsDir = tempDir,
                    )

                    // when
                    val result = runBlocking {
                        converter.convertSuspendable(testFile)
                    }

                    // then
                    assertThat(result.animeSeason.year).isEqualTo(2021)
                }
            }

            @Test
            fun `12-2021`() {
                tempDirectory {
                    // given
                    val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                        override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/12-2021.html")
                    "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                    val converter = AnisearchConverter(
                        config = testAnisearchConfig,
                        relationsDir = tempDir,
                    )

                    // when
                    val result = runBlocking {
                        converter.convertSuspendable(testFile)
                    }

                    // then
                    assertThat(result.animeSeason.year).isEqualTo(2021)
                }
            }

            @Test
            fun `2022`() {
                tempDirectory {
                    // given
                    val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                        override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/2022.html")
                    "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                    val converter = AnisearchConverter(
                        config = testAnisearchConfig,
                        relationsDir = tempDir,
                    )

                    // when
                    val result = runBlocking {
                        converter.convertSuspendable(testFile)
                    }

                    // then
                    assertThat(result.animeSeason.year).isEqualTo(2022)
                }
            }

            @Test
            fun `unknown year`() {
                tempDirectory {
                    // given
                    val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                        override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                        override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                        override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                        override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                    }

                    val testFile = loadTestResource("file_converter_tests/anime_season/year_of_premiere/unknown.html")
                    "<html></html>".writeToFile(tempDir.resolve("test-id.${testAnisearchConfig.fileSuffix()}"))

                    val converter = AnisearchConverter(
                        config = testAnisearchConfig,
                        relationsDir = tempDir,
                    )

                    // when
                    val result = runBlocking {
                        converter.convertSuspendable(testFile)
                    }

                    // then
                    assertThat(result.animeSeason.year).isZero()
                }
            }
        }
    }

    @Nested
    inner class RelatedAnimeTests {

        @Test
        fun `no related anime but a single adaption`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "14844"
                }

                val main = loadTestResource("file_converter_tests/related_anime/no_related_anime_but_adaption_main.html")
                testResource("file_converter_tests/related_anime/no_related_anime_but_adaption.html")
                    .copyTo(tempDir.resolve("14844.html"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(main)
                }

                // then
                assertThat(result.relatedAnime).isEmpty()
            }
        }

        @Test
        fun `no related anime`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "10941"
                }

                val main = loadTestResource("file_converter_tests/related_anime/no_related_anime_main.html")
                testResource("file_converter_tests/related_anime/no_related_anime.html")
                    .copyTo(tempDir.resolve("10941.html"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(main)
                }

                // then
                assertThat(result.relatedAnime).isEmpty()
            }
        }

        @Test
        fun `single related anime`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "14254"
                }

                val main = loadTestResource("file_converter_tests/related_anime/single_related_anime_main.html")
                testResource("file_converter_tests/related_anime/single_related_anime.html")
                    .copyTo(tempDir.resolve("14254.html"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(main)
                }

                // then
                assertThat(result.relatedAnime).containsExactly(
                    URI("https://anisearch.com/anime/15862")
                )
            }
        }

        @Test
        fun `multiple related anime`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "4942"
                }

                val main = loadTestResource("file_converter_tests/related_anime/multiple_related_anime_main.html")
                testResource("file_converter_tests/related_anime/multiple_related_anime.html")
                    .copyTo(tempDir.resolve("4942.html"))

                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )

                // when
                val result = runBlocking {
                    converter.convertSuspendable(main)
                }

                // then
                assertThat(result.relatedAnime).containsExactly(
                    URI("https://anisearch.com/anime/2088"),
                    URI("https://anisearch.com/anime/2753"),
                )
            }
        }

        @Test
        fun `throws exception if relations file is missing`() {
            tempDirectory {
                // given
                val testAnisearchConfig = object : MetaDataProviderConfig by MetaDataProviderTestConfig {
                    override fun buildAnimeLink(id: AnimeId): URI = AnisearchConfig.buildAnimeLink(id)
                    override fun buildDataDownloadLink(id: String): URI = AnisearchConfig.buildDataDownloadLink(id)
                    override fun fileSuffix(): FileSuffix = AnisearchConfig.fileSuffix()
                    override fun extractAnimeId(uri: URI): AnimeId = "test-id"
                }

                val testFile = loadTestResource("file_converter_tests/related_anime/related_anime_file_missing_main.html")
    
                val converter = AnisearchConverter(
                    config = testAnisearchConfig,
                    relationsDir = tempDir,
                )
    
                // when
                val result = exceptionExpected<IllegalStateException> {
                    converter.convertSuspendable(testFile)
                }
    
                // then
                assertThat(result).hasMessage("Relations file is missing")
            }
        }
    }
}