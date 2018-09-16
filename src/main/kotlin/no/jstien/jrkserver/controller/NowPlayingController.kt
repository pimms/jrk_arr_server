package no.jstien.jrkserver.controller

import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import no.jstien.jrkserver.episodes.Episode
import no.jstien.jrkserver.stream.InfiniteEpisodeStream
import org.apache.logging.log4j.LogManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


private data class NowPlaying(
    val isPlaying: Boolean,
    val name: String?,
    val key: String?,
    val season: String?
)


@RestController
@RequestMapping("/live")
class NowPlayingController
    @Autowired constructor(
            private val infiniteEpisodeStream: InfiniteEpisodeStream
    )
{
    companion object {
        private val LOG = LogManager.getLogger()
    }

    @GetMapping("nowPlaying")
    fun nowPlaying(): ResponseEntity<Any> {
        try {
            val episode = infiniteEpisodeStream.currentEpisode
            val nowPlaying = episode?.let { createNowPlayingResponse(it) }
                ?: createNothingPlayingResponse()
            return ResponseEntity.ok(nowPlaying)
        } catch (e: Exception) {
            val json = JsonObject()
            json.add("error", JsonPrimitive(e.message))
            return ResponseEntity.status(500).body(json.toString())
        }
    }

    private fun createNowPlayingResponse(episode: Episode): NowPlaying {
        return NowPlaying(true, episode.displayName, episode.s3Key, episode.season)
    }

    private fun createNothingPlayingResponse(): NowPlaying {
        return NowPlaying(false, null, null, null)
    }
}