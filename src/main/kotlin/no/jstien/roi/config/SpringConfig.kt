package no.jstien.roi.config

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import no.jstien.roi.episodes.MetadataExtractor
import no.jstien.roi.episodes.repo.EpisodeRepository
import no.jstien.roi.episodes.repo.S3FileRepository
import no.jstien.roi.event.Event
import no.jstien.roi.event.EventLog
import no.jstien.roi.podcast.PodcastRepository
import no.jstien.roi.stream.InfiniteEpisodeStream
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableScheduling
@PropertySource(value= ["classpath:config.properties"])
open class SpringConfig {
    @Value("\${s3.bucketname}") private val s3BucketName: String? = null
    @Value("\${podcast.title") private val podcastTitle: String? = null
    @Value("\${podcast.description") private val podcastDesc: String? = null
    @Value("\${podcast.rooturl") private val podcastRootUrl: String? = null


    private fun podcastConfig(): PodcastConfig {
        return PodcastConfig(podcastTitle!!, podcastDesc!!, podcastRootUrl!!)
    }


    @Bean
    open fun s3Client(): AmazonS3 {
        return AmazonS3ClientBuilder.defaultClient()
    }

    @Bean
    open fun s3FileRepository(): S3FileRepository {
        return S3FileRepository(s3Client(), s3BucketName!!)
    }

    @Bean
    open fun podcastRepository(): PodcastRepository {
        return PodcastRepository(s3FileRepository(), podcastConfig())
    }

    @Bean
    open fun eventLog(): EventLog {
        val eventLog = EventLog()
        eventLog.addEvent(Event.Type.SERVER_EVENT, "Server started")
        return eventLog
    }

    @Bean
    @Autowired
    open fun metadataExtractor(@Value("\${meta.seasonPrefix}") seasonPrefix: String): MetadataExtractor {
        return MetadataExtractor(seasonPrefix)
    }

    @Bean
    @Autowired
    open fun episodeRepository(metadataExtractor: MetadataExtractor): EpisodeRepository {
        return EpisodeRepository(s3FileRepository(), metadataExtractor, eventLog())
    }

    @Bean
    @Autowired
    open fun infiniteEpisodeStream(episodeRepository: EpisodeRepository): InfiniteEpisodeStream {
        return InfiniteEpisodeStream(episodeRepository, eventLog())
    }
}
