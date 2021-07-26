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
import io.github.manamiproject.modb.test.loadTestResource
import io.github.manamiproject.modb.test.tempDirectory
import io.github.manamiproject.modb.test.testResource
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

                // then
                assertThat(result.picture).isEqualTo(URI("https://cdn.anisearch.com/images/anime/cover/full/0.webp"))
                assertThat(result.thumbnail).isEqualTo(URI("https://cdn.anisearch.com/images/anime/cover/thumb/0.webp"))
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
                val result = converter.convert(testFile)

                // then
                assertThat(result.picture).isEqualTo(URI("https://cdn.anisearch.com/images/anime/cover/full/3/3633.webp"))
                assertThat(result.thumbnail).isEqualTo(URI("https://cdn.anisearch.com/images/anime/cover/thumb/3/3633.webp"))
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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

                // then
                assertThat(result.tags).isEmpty()
            }
        }
    }

    @Nested
    inner class EpisodesTests {

        @Test
        fun `unknown number of episodes`() {
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
                val result = converter.convert(testFile)

                // then
                assertThat(result.episodes).isZero()
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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

                // then
                assertThat(result.episodes).isEqualTo(1818)
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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

                // then
                assertThat(result.synonyms).containsExactly(
                    "Chikyūgai Shōnen Shōjo",
                    "Extra-Terrestrial Boys & Girls",
                    "Extraterrestial Boys and Girls",
                    "地球外少年少女",
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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                val result = converter.convert(testFile)

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
                    val result = converter.convert(testFile)

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
                    val result = converter.convert(testFile)

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
                    val result = converter.convert(testFile)

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
                    val result = converter.convert(testFile)

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
                    val result = converter.convert(testFile)

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
                    val result = converter.convert(testFile)

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
                    val result = converter.convert(testFile)

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
                    val result = converter.convert(testFile)

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
                    val result = converter.convert(testFile)

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
                val result = converter.convert(main)

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
                val result = converter.convert(main)

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
                val result = converter.convert(main)

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
                val result = converter.convert(main)

                // then
                assertThat(result.relatedAnime).containsExactly(
                    URI("https://anisearch.com/anime/10100"),
                    URI("https://anisearch.com/anime/14020"),
                    URI("https://anisearch.com/anime/15195"),
                    URI("https://anisearch.com/anime/16488"),
                    URI("https://anisearch.com/anime/1740"),
                    URI("https://anisearch.com/anime/2088"),
                    URI("https://anisearch.com/anime/2569"),
                    URI("https://anisearch.com/anime/2753"),
                    URI("https://anisearch.com/anime/3563"),
                    URI("https://anisearch.com/anime/3602"),
                    URI("https://anisearch.com/anime/8303"),
                    URI("https://anisearch.com/anime/875"),
                    URI("https://anisearch.com/anime/9293"),
                    URI("https://anisearch.com/anime/9848"),
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
                val result = assertThrows<IllegalStateException> {
                    converter.convert(testFile)
                }
    
                // then
                assertThat(result).hasMessage("Relations file is missing")
            }
        }
    }
}