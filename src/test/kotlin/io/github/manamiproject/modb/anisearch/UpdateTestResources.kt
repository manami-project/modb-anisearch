package io.github.manamiproject.modb.anisearch

import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.test.testResource
import kotlinx.coroutines.runBlocking
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val downloader = AnisearchDownloader(AnisearchConfig)
    val relationsDownloader = AnisearchDownloader(AnisearchRelationsConfig)
    
    runBlocking {
        downloader.downloadSuspendable("14628").writeToFile(resourceFile("file_converter_tests/anime_season/season/01.html"))
        downloader.downloadSuspendable("14967").writeToFile(resourceFile("file_converter_tests/anime_season/season/02.html"))
        downloader.downloadSuspendable("16251").writeToFile(resourceFile("file_converter_tests/anime_season/season/03.html"))
        downloader.downloadSuspendable("14508").writeToFile(resourceFile("file_converter_tests/anime_season/season/04.html"))
        downloader.downloadSuspendable("15264").writeToFile(resourceFile("file_converter_tests/anime_season/season/05.html"))
        downloader.downloadSuspendable("14935").writeToFile(resourceFile("file_converter_tests/anime_season/season/06.html"))
        downloader.downloadSuspendable("14816").writeToFile(resourceFile("file_converter_tests/anime_season/season/07.html"))
        downloader.downloadSuspendable("16401").writeToFile(resourceFile("file_converter_tests/anime_season/season/08.html"))
        downloader.downloadSuspendable("16401").writeToFile(resourceFile("file_converter_tests/anime_season/season/09.html"))
        downloader.downloadSuspendable("15606").writeToFile(resourceFile("file_converter_tests/anime_season/season/10.html"))
        downloader.downloadSuspendable("15042").writeToFile(resourceFile("file_converter_tests/anime_season/season/11.html"))
        downloader.downloadSuspendable("14484").writeToFile(resourceFile("file_converter_tests/anime_season/season/12.html"))
        downloader.downloadSuspendable("16275").writeToFile(resourceFile("file_converter_tests/anime_season/season/unknown.html"))
        downloader.downloadSuspendable("17467").writeToFile(resourceFile("file_converter_tests/anime_season/season/year_only.html"))
    
        downloader.downloadSuspendable("15890").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/06-aug-2021.html"))
        downloader.downloadSuspendable("16264").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/12-2021.html"))
        downloader.downloadSuspendable("8504").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2022.html"))
        downloader.downloadSuspendable("16275").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/unknown.html"))
    
        downloader.downloadSuspendable("6247").writeToFile(resourceFile("file_converter_tests/duration/1_hour.html"))
        downloader.downloadSuspendable("11051").writeToFile(resourceFile("file_converter_tests/duration/1_minute.html"))
        downloader.downloadSuspendable("6889").writeToFile(resourceFile("file_converter_tests/duration/2_hours.html"))
        downloader.downloadSuspendable("14844").writeToFile(resourceFile("file_converter_tests/duration/24_minutes_per_episode.html"))
        downloader.downloadSuspendable("7192").writeToFile(resourceFile("file_converter_tests/duration/63_minutes_by_6_episodes.html"))
        downloader.downloadSuspendable("7163").writeToFile(resourceFile("file_converter_tests/duration/70_minutes.html"))
        downloader.downloadSuspendable("602").writeToFile(resourceFile("file_converter_tests/duration/134_minutes.html"))
        downloader.downloadSuspendable("16711").writeToFile(resourceFile("file_converter_tests/duration/episodes_and_duration_unknown.html"))
        downloader.downloadSuspendable("17039").writeToFile(resourceFile("file_converter_tests/duration/episodes_known_duration_unknown.html"))
    
        downloader.downloadSuspendable("9981").writeToFile(resourceFile("file_converter_tests/episodes/1.html"))
        downloader.downloadSuspendable("3135").writeToFile(resourceFile("file_converter_tests/episodes/10.html"))
        downloader.downloadSuspendable("4138").writeToFile(resourceFile("file_converter_tests/episodes/100.html"))
        downloader.downloadSuspendable("5801").writeToFile(resourceFile("file_converter_tests/episodes/1818.html"))
        downloader.downloadSuspendable("16804").writeToFile(resourceFile("file_converter_tests/episodes/type-is-double.html"))
        downloader.downloadSuspendable("16578").writeToFile(resourceFile("file_converter_tests/episodes/unknown.html"))
    
        downloader.downloadSuspendable("15237").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html"))
        downloader.downloadSuspendable("3633").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html"))
    
        relationsDownloader.downloadSuspendable("4942").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_related_anime.html"))
        downloader.downloadSuspendable("4942").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_related_anime_main.html"))
        relationsDownloader.downloadSuspendable("10941").writeToFile(resourceFile("file_converter_tests/related_anime/no_related_anime.html"))
        downloader.downloadSuspendable("10941").writeToFile(resourceFile("file_converter_tests/related_anime/no_related_anime_main.html"))
        relationsDownloader.downloadSuspendable("14844").writeToFile(resourceFile("file_converter_tests/related_anime/no_related_anime_but_adaption.html"))
        downloader.downloadSuspendable("14844").writeToFile(resourceFile("file_converter_tests/related_anime/no_related_anime_but_adaption_main.html"))
        downloader.downloadSuspendable("4942").writeToFile(resourceFile("file_converter_tests/related_anime/related_anime_file_missing_main.html"))
        relationsDownloader.downloadSuspendable("14254").writeToFile(resourceFile("file_converter_tests/related_anime/single_related_anime.html"))
        downloader.downloadSuspendable("14254").writeToFile(resourceFile("file_converter_tests/related_anime/single_related_anime_main.html"))
    
        downloader.downloadSuspendable("3633").writeToFile(resourceFile("file_converter_tests/sources/3633.html"))
    
        downloader.downloadSuspendable("12433").writeToFile(resourceFile("file_converter_tests/status/on_hold.html"))
        downloader.downloadSuspendable("12433").writeToFile(resourceFile("file_converter_tests/status/aborted.html"))
        downloader.downloadSuspendable("3633").writeToFile(resourceFile("file_converter_tests/status/completed.html"))
        downloader.downloadSuspendable("13540").writeToFile(resourceFile("file_converter_tests/status/completed_in_japan_upcoming_elsewhere.html"))
        downloader.downloadSuspendable("14494").writeToFile(resourceFile("file_converter_tests/status/no_status.html"))
        downloader.downloadSuspendable("16777").writeToFile(resourceFile("file_converter_tests/status/ongoing.html"))
        downloader.downloadSuspendable("12224").writeToFile(resourceFile("file_converter_tests/status/upcoming.html"))
    
        downloader.downloadSuspendable("1958").writeToFile(resourceFile("file_converter_tests/synonyms/multiple_synonyms.html"))
        downloader.downloadSuspendable("16260").writeToFile(resourceFile("file_converter_tests/synonyms/no_synonyms.html"))
        downloader.downloadSuspendable("13631").writeToFile(resourceFile("file_converter_tests/synonyms/romanji_alteration.html"))
        downloader.downloadSuspendable("14456").writeToFile(resourceFile("file_converter_tests/synonyms/single_synonym.html"))
    
        downloader.downloadSuspendable("8724").writeToFile(resourceFile("file_converter_tests/synonyms/hidden_synonyms_8724.html"))
        downloader.downloadSuspendable("11197").writeToFile(resourceFile("file_converter_tests/synonyms/hidden_synonyms_11197.html"))
        downloader.downloadSuspendable("8093").writeToFile(resourceFile("file_converter_tests/synonyms/hidden_synonyms_and_named_parts.html"))
        downloader.downloadSuspendable("17015").writeToFile(resourceFile("file_converter_tests/synonyms/italic.html"))
        downloader.downloadSuspendable("1958").writeToFile(resourceFile("file_converter_tests/synonyms/multiple_synonyms.html"))
        downloader.downloadSuspendable("16260").writeToFile(resourceFile("file_converter_tests/synonyms/no_synonyms.html"))
        downloader.downloadSuspendable("13631").writeToFile(resourceFile("file_converter_tests/synonyms/romanji_alteration.html"))
        downloader.downloadSuspendable("14456").writeToFile(resourceFile("file_converter_tests/synonyms/single_synonym.html"))
        downloader.downloadSuspendable("15599").writeToFile(resourceFile("file_converter_tests/synonyms/synonyms_contain_named_parts.html"))
    
        downloader.downloadSuspendable("15073").writeToFile(resourceFile("file_converter_tests/tags/multiple_tags.html"))
        downloader.downloadSuspendable("17467").writeToFile(resourceFile("file_converter_tests/tags/no_tags.html"))
        downloader.downloadSuspendable("12954").writeToFile(resourceFile("file_converter_tests/tags/one_tag.html"))
    
        downloader.downloadSuspendable("4410").writeToFile(resourceFile("file_converter_tests/title/no_suffix_for_title.html"))
        downloader.downloadSuspendable("15159").writeToFile(resourceFile("file_converter_tests/title/special_chars.html"))
    
        downloader.downloadSuspendable("10454").writeToFile(resourceFile("file_converter_tests/type/bonus.html"))
        downloader.downloadSuspendable("12290").writeToFile(resourceFile("file_converter_tests/type/cm.html"))
        downloader.downloadSuspendable("9981").writeToFile(resourceFile("file_converter_tests/type/movie.html"))
        downloader.downloadSuspendable("9830").writeToFile(resourceFile("file_converter_tests/type/music-video.html"))
        downloader.downloadSuspendable("16289").writeToFile(resourceFile("file_converter_tests/type/other.html"))
        downloader.downloadSuspendable("3627").writeToFile(resourceFile("file_converter_tests/type/ova.html"))
        downloader.downloadSuspendable("4946").writeToFile(resourceFile("file_converter_tests/type/tv-series.html"))
        downloader.downloadSuspendable("13250").writeToFile(resourceFile("file_converter_tests/type/tv-special.html"))
        downloader.downloadSuspendable("17467").writeToFile(resourceFile("file_converter_tests/type/unknown.html"))
        downloader.downloadSuspendable("14935").writeToFile(resourceFile("file_converter_tests/type/web.html"))
    }
}

private fun resourceFile(file: String): Path {
    return Paths.get(
        testResource(file).toAbsolutePath()
            .toString()
            .replace("/build/resources/test/", "/src/test/resources/")
    )
}