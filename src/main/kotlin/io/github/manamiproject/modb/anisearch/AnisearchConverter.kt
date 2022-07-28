package io.github.manamiproject.modb.anisearch

import io.github.manamiproject.modb.anisearch.extensions.isInt
import io.github.manamiproject.modb.core.Json
import io.github.manamiproject.modb.core.config.AnimeId
import io.github.manamiproject.modb.core.config.MetaDataProviderConfig
import io.github.manamiproject.modb.core.converter.AnimeConverter
import io.github.manamiproject.modb.core.extensions.*
import io.github.manamiproject.modb.core.models.*
import io.github.manamiproject.modb.core.models.Anime.Status.*
import io.github.manamiproject.modb.core.models.Anime.Type.*
import io.github.manamiproject.modb.core.models.AnimeSeason.Season.*
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.HOURS
import io.github.manamiproject.modb.core.models.Duration.TimeUnit.MINUTES
import org.jsoup.Jsoup
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

    override fun convert(rawContent: String): Anime {
        val document = Jsoup.parse(rawContent)

        val jsonData = document.select("script[type=application/ld+json]")
            .dataNodes()
            .toString()
            .trimStart('[')
            .trimEnd(']')
        val anisearchData = Json.parseJson<AnisearchData>(jsonData) ?: AnisearchData()

        val picture = extractPicture(anisearchData, document)
        val sources = extractSourcesEntry(document)
        val id = config.extractAnimeId(sources.first())

        return Anime(
            _title = extractTitle(anisearchData, document),
            episodes = extractEpisodes(anisearchData, document),
            type = extractType(document),
            picture = picture,
            thumbnail = findThumbnail(picture),
            status = extractStatus(document),
            duration = extractDuration(document),
            animeSeason = extractAnimeSeason(anisearchData)
        ).apply {
            addSources(sources)
            addSynonyms(extractSynonyms(document))
            addRelations(extractRelatedAnime(id))
            addTags(extractTags(document))
        }
    }

    private fun extractPicture(anisearchData: AnisearchData, document: Document): URI {
        if (anisearchData.image.isNotBlank()) {
            return URI(anisearchData.image)
        }

        val value = document.select("meta[property=og:image]")
                .attr("content")
                .trim()
        return URI(value)
    }

    private fun extractTitle(anisearchData: AnisearchData, document: Document): Title {
        if (anisearchData.name.isNotBlank()) {
            return anisearchData.name
        }

        var title = document.select("h1[id=htitle]")
            .select("span[itemprop=name]")
            .text()
            .trim()

        if (title.isBlank()) {
            title = document.select("h1[id=htitle]")
                .text()
                .trim()
        }

        return title
    }

    private fun extractEpisodes(anisearchData: AnisearchData, document: Document): Episodes {
        val value = document.select("ul[id=infodetails]")
            .select("span:matchesOwn(Episodes)")
            .next()
            .text()

        return when {
            value == "?" -> 0
            value.isInt() -> value.toInt()
            else -> anisearchData.episodes // frontend shows ? whereas the json contains 1. So the json is fallback only
        }
    }

    private fun extractType(document: Document): Anime.Type {
        val type = document.select("ul[id=infodetails]")
            .select("span:matchesOwn(Type)")
            .parents()
            .first()!!
            .ownText()
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

    private fun findThumbnail(picture: URI): URI {
        val value = picture.toString().replace("full", "thumb")
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
        val textValue = document.select("ul[id=infodetails]")
            .select("span:matchesOwn(Episodes)")
            .next()
            .text()
            .trim()

        if (textValue == "?") {
            return Duration.UNKNOWN
        }

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

        val hiddenWithoutSpan = synonymsDiv.select("span[id=text-synonyms]")
            .textNodes()
            .map { it.text().trim() }
            .map { it.trimStart(',').trimEnd(',') }

        return synonymsByLanguage.union(subheaderTitles).union(synonymsDivNoSpan).union(synonymsDivSpan).union(hiddenWithoutSpan)
    }

    private fun extractRelatedAnime(id: AnimeId): Collection<URI> {
        val relationsFile = relationsDir.resolve("$id.${config.fileSuffix()}")

        check(relationsFile.regularFileExists()) { "Relations file is missing" }

        return Jsoup.parse(relationsFile.readFile())
            .select("section[id=relations_anime]")
            .select("table")
            .select("tbody")
            .select("a")
            .map { it.attr("href") }
            .map { it.replace("anime/", EMPTY) }
            .map { it.substring(0, it.indexOf(',')) }
            .map { config.buildAnimeLink(it) }
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
    val numberOfEpisodes: Any = EMPTY, // they mess up the type. They use both strings and integer for episodes
    val startDate: String = EMPTY,
) {
    val episodes: Int
        get() {
            return when(numberOfEpisodes) {
                is String -> {
                    val trimmedValue = numberOfEpisodes.trim()
                    when {
                        trimmedValue.isInt() -> numberOfEpisodes.toInt()
                        else -> 0
                    }
                }
                is Int -> numberOfEpisodes
                is Double -> numberOfEpisodes.toInt()
                else -> throw IllegalStateException("Unknown type for numberOfEpisodes: [${numberOfEpisodes.javaClass}]")
            }
        }
}