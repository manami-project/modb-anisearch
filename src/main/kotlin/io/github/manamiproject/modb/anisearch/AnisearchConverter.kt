package io.github.manamiproject.modb.anisearch

import io.github.manamiproject.modb.anisearch.extensions.isInt
import io.github.manamiproject.modb.core.json.Json
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.converter.AnimeConverter
import io.github.manamiproject.modb.core.coroutines.ModbDispatchers.LIMITED_CPU
import io.github.manamiproject.modb.core.extensions.*
import io.github.manamiproject.modb.core.models.*
import io.github.manamiproject.modb.core.models.Anime.Status.*
import io.github.manamiproject.modb.core.models.Anime.Type.*
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.HOURS
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.MINUTES
import io.github.manamiproject.modb.core.parseHtml
import kotlinx.coroutines.withContext
import org.jsoup.nodes.Document
import java.net.URI

/**
 * Converts raw data to an [Anime].
 * Requires raw HTML.
 * @since 1.0.0
 * @param config Configuration for converting data.
 * @param relationsDir Directory containing the raw files for the related anime.
 * @throws IllegalArgumentException if [relationsDir] doesn't exist or is not a directory.
 */
public class AnisearchConverter(
    private val config: MetaDataProviderConfig = AnisearchConfig,
    private val relationsDir: Directory,
) : AnimeConverter {

    init {
        require(relationsDir.directoryExists()) { "Directory for relations [$relationsDir] does not exist or is not a directory." }
    }

    override suspend fun convert(rawContent: String): Anime = withContext(LIMITED_CPU) {
        val document = parseHtml(rawContent)

        val jsonData = document.select("script[type=application/ld+json]")
            .dataNodes()
            .map { it.toString() }
            .first { !it.contains("BreadcrumbList") }
            .trimStart('[')
            .trimEnd(']')
        val anisearchData = Json.parseJson<AnisearchData>(jsonData) ?: AnisearchData()

        val thumbnail = extractThumbnail(document)
        val sources = extractSourcesEntry(document)
        val id = config.extractAnimeId(sources.first())

        return@withContext Anime(
            _title = extractTitle(anisearchData, document),
            episodes = extractEpisodes(anisearchData),
            type = extractType(document),
            picture = generatePicture(thumbnail),
            thumbnail = thumbnail,
            status = extractStatus(document),
            duration = extractDuration(document),
            animeSeason = extractAnimeSeason(anisearchData),
        ).apply {
            addSources(sources)
            addSynonyms(extractSynonyms(document))
            addRelatedAnime(extractRelatedAnime(id))
            addTags(extractTags(document))
        }
    }

    private fun extractThumbnail(document: Document): URI {
        // links in JSON are invalid (http 404) for a few weeks now. Have to solely rely on meta tag again
        val value = document.select("meta[property=og:image]")
                .attr("content")
                .trim()
        return URI(value)
    }

    private fun extractTitle(anisearchData: AnisearchData, document: Document): Title {
        if (anisearchData.name.isNotBlank()) {
            return anisearchData.name
        }

        val titleFromMetaData = document.select("meta[property=og:title]")
            .attr("content")
            .trim()
            .replace(" (Anime)", EMPTY)
            .trim()

        if (titleFromMetaData.isNotBlank()) {
            return titleFromMetaData
        }

        throw IllegalStateException("Unable to extract title for [anisearchData=$anisearchData]")
    }

    private fun extractEpisodes(anisearchData: AnisearchData): Episodes = anisearchData.episodes

    private fun extractType(document: Document): Anime.Type {
        val type = document.select("ul[class=xlist row simple infoblock]")
            .select("div[class=type]")
            .textNodes()
            .first()
            .text()
            .split(',')
            .first()
            .trim()
            .lowercase()

        return when(type) {
            "bonus" -> SPECIAL
            "cm" -> SPECIAL
            "movie" -> MOVIE
            "music video" -> SPECIAL
            "other" -> Anime.Type.UNKNOWN
            "ova" -> OVA
            "tv-series" -> TV
            "tv-special" -> TV
            "unknown" -> Anime.Type.UNKNOWN
            "web" -> ONA
            else -> throw IllegalStateException("Unmapped type [$type]")
        }
    }

    private fun generatePicture(picture: URI): URI {
        val value = picture.toString().replace("/full", EMPTY).replace(".webp", "_300.webp")
        return URI(value)
    }

    private fun extractStatus(document: Document): Anime.Status {
        return when(val status = document.select("div[class=status]").first()?.ownText()?.trim()?.lowercase() ?: EMPTY) {
            "aborted" -> Anime.Status.UNKNOWN
            "completed" -> FINISHED
            "ongoing" -> ONGOING
            "upcoming" -> UPCOMING
            "on hold", EMPTY -> Anime.Status.UNKNOWN
            else -> throw IllegalStateException("Unmapped status [$status]")
        }
    }

    private fun extractDuration(document: Document): Duration {
        val textValue = document.select("ul[class=xlist row simple infoblock]")
            .select("time")
            .text()
            .trim()
            .lowercase()

        val fallbackValue = "0"
        val value = (Regex("[0-9]+").find(textValue)?.value?.ifBlank { fallbackValue } ?: fallbackValue).toInt()
        val fallbackUnit = "unknown"
        val extractedUnit = Regex("[aA-zZ]+").find(textValue)?.value ?: fallbackUnit

        when {
            value > 0 && extractedUnit == fallbackUnit -> throw IllegalStateException("Value for duration is present [], but unit is unknown")
            value == 0 && extractedUnit == fallbackUnit -> return Duration.UNKNOWN
        }

        val unit = when(extractedUnit) {
            "hrs" -> HOURS
            "min" -> MINUTES
            else -> throw IllegalStateException("Unmapped duration unit [$extractedUnit]")
        }

        return Duration(value, unit)
    }

    private fun extractAnimeSeason(anisearchData: AnisearchData): AnimeSeason {
        val date = anisearchData.startDate
        if (date.isBlank()) {
            return AnimeSeason()
        }

        val year = Regex("[0-9]{4}").find(date)!!.value.toInt()
        val fallback = "0"
        val month = (Regex("-[0-9]{2}-").find(date)?.value?.replace(Regex("-"), EMPTY)?.ifBlank { fallback } ?: fallback).toInt()

        val season = when(month) {
            1, 2, 3 -> WINTER
            4, 5, 6 -> SPRING
            7, 8, 9 -> SUMMER
            10, 11, 12 -> FALL
            else -> UNDEFINED
        }

        return AnimeSeason(
            year = year,
            season = season
        )
    }

    private fun extractSourcesEntry(document: Document): Collection<URI> {
        val id = document.select("div[id=content-outer]").attr("data-id")
        return setOf(config.buildAnimeLink(id))
    }

    private fun extractSynonyms(document: Document): Collection<Title> {
        val titleDiv = document.select("div[class=title]")
        val synonymsByLanguage = titleDiv.select("strong")
            .map { it.text().trim() }
        val subheaderTitles = titleDiv.select("div[class=grey]")
            .map { it.text().trim() }

        val synonymsDiv = document.select("div[class=synonyms]")
        val synonymsDivNoSpan = synonymsDiv.textNodes().map { it.text().trim() }.map { it.trimStart(',').trimEnd(',') }
        val synonymsDivSpan = synonymsDiv.select("span")
            .filterNot { it.hasAttr("id") && it.attr("id") == "text-synonyms"}
            .map { it.text().trim() }
            .filterNot { it == "Synonyms:" }
        val italic = synonymsDiv.select("i").textNodes().map { it.text() }.toSet()

        val hiddenWithoutSpan = synonymsDiv.select("span[id=text-synonyms]")
            .textNodes()
            .map { it.text().trim() }
            .map { it.trimStart(',').trimEnd(',') }

        return synonymsByLanguage.union(subheaderTitles).union(synonymsDivNoSpan).union(synonymsDivSpan).union(hiddenWithoutSpan).union(italic)
    }

    private suspend fun extractRelatedAnime(id: AnimeId): Collection<URI> = withContext(LIMITED_CPU) {
        val relationsFile = relationsDir.resolve("$id.${config.fileSuffix()}")

        check(relationsFile.regularFileExists()) { "Relations file is missing" }

        return@withContext parseHtml(relationsFile.readFile()) { document ->
            document.select("section[id=relations_anime]")
                .select("table")
                .select("tbody")
                .select("a")
                .map { it.attr("href") }
                .map { it.replace("anime/", EMPTY) }
                .map { it.substring(0, it.indexOf(',')) }
                .map { config.buildAnimeLink(it) }
        }
    }

    private fun extractTags(document: Document): Collection<Tag> {
        return document.select("section[id=description]")
            .select("ul[class=cloud]")
            .select("li")
            .select("a")
            .map { it.text().trim() }
    }
}

private data class AnisearchData(
    val name: String = EMPTY,
    val url: String = EMPTY,
    val image: String = EMPTY,
    val numberOfEpisodes: Any? = EMPTY, // they mess up the type. They use both strings and integer for episodes
    val startDate: String = EMPTY,
) {
    val episodes: Int
        get() {
            return when(numberOfEpisodes) {
                is String -> {
                    val trimmedValue = numberOfEpisodes.trim()
                    when {
                        trimmedValue.isInt() -> numberOfEpisodes.toInt()
                        else -> 1
                    }
                }
                is Int -> numberOfEpisodes
                is Double -> numberOfEpisodes.toInt()
                null -> 1
                else -> throw IllegalStateException("Unknown type for numberOfEpisodes: [${numberOfEpisodes.javaClass}]")
            }
        }
}