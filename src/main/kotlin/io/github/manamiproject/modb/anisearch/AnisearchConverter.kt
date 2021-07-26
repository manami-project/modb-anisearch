package io.github.manamiproject.modb.anisearch

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

        val picture = extractPicture(document)
        val sources = extractSourcesEntry(document)
        val id = config.extractAnimeId(sources.first())

        return Anime(
            _title = extractTitle(document),
            episodes = extractEpisodes(document),
            type = extractType(document),
            picture = picture,
            thumbnail = findThumbnail(picture),
            status = extractStatus(document),
            duration = extractDuration(document),
            animeSeason = extractAnimeSeason(document)
        ).apply {
            addSources(sources)
            addSynonyms(extractSynonyms(document))
            addRelations(extractRelatedAnime(id))
            addTags(extractTags(document))
        }
    }

    private fun extractPicture(document: Document): URI {
        val value = document.select("meta[property=og:image]")
                .attr("content")
                .trim()
        return URI(value)
    }

    private fun extractTitle(document: Document): Title {
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

    private fun extractEpisodes(document: Document): Episodes {
        return document.select("ul[id=infodetails]")
            .select("span:matchesOwn(Episodes)")
            .parents()
            .first()!!
            .ownText()
            .trim()
            .ifBlank { "0" }
            .toInt()
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
        return when(val status = document.select("div[class=status]")?.first()?.ownText()?.trim()?.lowercase() ?: EMPTY) {
            "aborted" -> Anime.Status.UNKNOWN
            "completed" -> FINISHED
            "ongoing" -> ONGOING
            "upcoming" -> UPCOMING
            EMPTY -> Anime.Status.UNKNOWN
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

    private fun extractAnimeSeason(document: Document): AnimeSeason {
        val dateCreated = document.select("meta[itemprop=dateCreated]").attr("content") ?: EMPTY
        val fallback = "0"
        val year = (Regex("[0-9]{4}").find(dateCreated)?.value?.ifBlank { fallback } ?: fallback).toInt()
        val month = (Regex("-[0-9]{2}-").find(dateCreated)?.value?.replace(Regex("-"), EMPTY)?.ifBlank { fallback } ?: fallback).toInt()

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
            .map { it.text().trim() }
            .filterNot { it == "Synonyms:" }

        return synonymsByLanguage.union(subheaderTitles).union(synonymsDivNoSpan).union(synonymsDivSpan)
    }

    private fun extractRelatedAnime(id: AnimeId): Collection<URI> {
        val relationsFile = relationsDir.resolve("$id.${config.fileSuffix()}")

        check(relationsFile.regularFileExists()) { "Relations file is missing" }

        return Jsoup.parse(relationsFile.readFile())
            .select("section[id=relations]")
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