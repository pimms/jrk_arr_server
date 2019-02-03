package no.jstien.roi.podcast

data class PodcastFeed(
    val title: String,
    val description: String,
    val items: List<PodcastItem>,

    val category: String = "Comedy",
    val language: String = "no"
)