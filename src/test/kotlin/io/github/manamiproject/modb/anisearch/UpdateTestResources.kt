package io.github.manamiproject.modb.anisearch

import io.github.manamiproject.modb.core.extensions.writeToFile
import io.github.manamiproject.modb.test.testResource
import java.nio.file.Path
import java.nio.file.Paths

fun main() {
    val downloader = AnisearchDownloader(AnisearchConfig)
    val relationsDownloader = AnisearchDownloader(AnisearchRelationsConfig)

    downloader.download("14628").writeToFile(resourceFile("file_converter_tests/anime_season/season/01.html"))
    downloader.download("14967").writeToFile(resourceFile("file_converter_tests/anime_season/season/02.html"))
    downloader.download("16251").writeToFile(resourceFile("file_converter_tests/anime_season/season/03.html"))
    downloader.download("14508").writeToFile(resourceFile("file_converter_tests/anime_season/season/04.html"))
    downloader.download("15264").writeToFile(resourceFile("file_converter_tests/anime_season/season/05.html"))
    downloader.download("14935").writeToFile(resourceFile("file_converter_tests/anime_season/season/06.html"))
    downloader.download("14816").writeToFile(resourceFile("file_converter_tests/anime_season/season/07.html"))
    downloader.download("16401").writeToFile(resourceFile("file_converter_tests/anime_season/season/08.html"))
    downloader.download("16401").writeToFile(resourceFile("file_converter_tests/anime_season/season/09.html"))
    downloader.download("15606").writeToFile(resourceFile("file_converter_tests/anime_season/season/10.html"))
    downloader.download("15042").writeToFile(resourceFile("file_converter_tests/anime_season/season/11.html"))
    downloader.download("14484").writeToFile(resourceFile("file_converter_tests/anime_season/season/12.html"))
    downloader.download("16275").writeToFile(resourceFile("file_converter_tests/anime_season/season/unknown.html"))
    downloader.download("17467").writeToFile(resourceFile("file_converter_tests/anime_season/season/year_only.html"))

    downloader.download("15890").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/06-aug-2021.html"))
    downloader.download("16264").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/12-2021.html"))
    downloader.download("8504").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/2022.html"))
    downloader.download("16275").writeToFile(resourceFile("file_converter_tests/anime_season/year_of_premiere/unknown.html"))

    downloader.download("9981").writeToFile(resourceFile("file_converter_tests/episodes/1.html"))
    downloader.download("3135").writeToFile(resourceFile("file_converter_tests/episodes/10.html"))
    downloader.download("4138").writeToFile(resourceFile("file_converter_tests/episodes/100.html"))
    downloader.download("5801").writeToFile(resourceFile("file_converter_tests/episodes/1818.html"))
    downloader.download("16804").writeToFile(resourceFile("file_converter_tests/episodes/type-is-double.html"))
    downloader.download("16578").writeToFile(resourceFile("file_converter_tests/episodes/unknown.html"))

    downloader.download("17551").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/neither_picture_nor_thumbnail.html"))
    downloader.download("3633").writeToFile(resourceFile("file_converter_tests/picture_and_thumbnail/picture_and_thumbnail_available.html"))

    relationsDownloader.download("4942").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_related_anime.html"))
    downloader.download("4942").writeToFile(resourceFile("file_converter_tests/related_anime/multiple_related_anime_main.html"))
    relationsDownloader.download("10941").writeToFile(resourceFile("file_converter_tests/related_anime/no_related_anime.html"))
    downloader.download("10941").writeToFile(resourceFile("file_converter_tests/related_anime/no_related_anime_main.html"))
    relationsDownloader.download("14844").writeToFile(resourceFile("file_converter_tests/related_anime/no_related_anime_but_adaption.html"))
    downloader.download("14844").writeToFile(resourceFile("file_converter_tests/related_anime/no_related_anime_but_adaption_main.html"))
    downloader.download("4942").writeToFile(resourceFile("file_converter_tests/related_anime/related_anime_file_missing_main.html"))
    relationsDownloader.download("14254").writeToFile(resourceFile("file_converter_tests/related_anime/single_related_anime.html"))
    downloader.download("14254").writeToFile(resourceFile("file_converter_tests/related_anime/single_related_anime_main.html"))

    downloader.download("3633").writeToFile(resourceFile("file_converter_tests/sources/3633.html"))

    downloader.download("12433").writeToFile(resourceFile("file_converter_tests/status/on_hold.html"))
    downloader.download("12433").writeToFile(resourceFile("file_converter_tests/status/aborted.html"))
    downloader.download("3633").writeToFile(resourceFile("file_converter_tests/status/completed.html"))
    downloader.download("13540").writeToFile(resourceFile("file_converter_tests/status/completed_in_japan_upcoming_elsewhere.html"))
    downloader.download("14494").writeToFile(resourceFile("file_converter_tests/status/no_status.html"))
    downloader.download("16777").writeToFile(resourceFile("file_converter_tests/status/ongoing.html"))
    downloader.download("12224").writeToFile(resourceFile("file_converter_tests/status/upcoming.html"))

    downloader.download("1958").writeToFile(resourceFile("file_converter_tests/synonyms/multiple_synonyms.html"))
    downloader.download("16260").writeToFile(resourceFile("file_converter_tests/synonyms/no_synonyms.html"))
    downloader.download("13631").writeToFile(resourceFile("file_converter_tests/synonyms/romanji_alteration.html"))
    downloader.download("14456").writeToFile(resourceFile("file_converter_tests/synonyms/single_synonym.html"))

    downloader.download("8724").writeToFile(resourceFile("file_converter_tests/synonyms/hidden_synonyms_8724.html"))
    downloader.download("11197").writeToFile(resourceFile("file_converter_tests/synonyms/hidden_synonyms_11197.html"))
    downloader.download("8093").writeToFile(resourceFile("file_converter_tests/synonyms/hidden_synonyms_and_named_parts.html"))
    downloader.download("17015").writeToFile(resourceFile("file_converter_tests/synonyms/italic.html"))
    downloader.download("1958").writeToFile(resourceFile("file_converter_tests/synonyms/multiple_synonyms.html"))
    downloader.download("16260").writeToFile(resourceFile("file_converter_tests/synonyms/no_synonyms.html"))
    downloader.download("13631").writeToFile(resourceFile("file_converter_tests/synonyms/romanji_alteration.html"))
    downloader.download("14456").writeToFile(resourceFile("file_converter_tests/synonyms/single_synonym.html"))
    downloader.download("15599").writeToFile(resourceFile("file_converter_tests/synonyms/synonyms_contain_named_parts.html"))

    downloader.download("15073").writeToFile(resourceFile("file_converter_tests/tags/multiple_tags.html"))
    downloader.download("17467").writeToFile(resourceFile("file_converter_tests/tags/no_tags.html"))
    downloader.download("12954").writeToFile(resourceFile("file_converter_tests/tags/one_tag.html"))

    downloader.download("4410").writeToFile(resourceFile("file_converter_tests/title/no_suffix_for_title.html"))
    downloader.download("15159").writeToFile(resourceFile("file_converter_tests/title/special_chars.html"))

    downloader.download("10454").writeToFile(resourceFile("file_converter_tests/type/bonus.html"))
    downloader.download("12290").writeToFile(resourceFile("file_converter_tests/type/cm.html"))
    downloader.download("9981").writeToFile(resourceFile("file_converter_tests/type/movie.html"))
    downloader.download("9830").writeToFile(resourceFile("file_converter_tests/type/music-video.html"))
    downloader.download("16289").writeToFile(resourceFile("file_converter_tests/type/other.html"))
    downloader.download("3627").writeToFile(resourceFile("file_converter_tests/type/ova.html"))
    downloader.download("4946").writeToFile(resourceFile("file_converter_tests/type/tv-series.html"))
    downloader.download("13250").writeToFile(resourceFile("file_converter_tests/type/tv-special.html"))
    downloader.download("17467").writeToFile(resourceFile("file_converter_tests/type/unknown.html"))
    downloader.download("14935").writeToFile(resourceFile("file_converter_tests/type/web.html"))
}

private fun resourceFile(file: String): Path {
    return Paths.get(
        testResource(file).toAbsolutePath()
            .toString()
            .replace("/build/resources/test/", "/src/test/resources/")
    )
}