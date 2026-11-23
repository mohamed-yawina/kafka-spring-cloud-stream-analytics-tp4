
package ma.emsi.kafkastreamsapp.services;

import ma.emsi.kafkastreamsapp.entities.PageEvent;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.function.Function;

@Configuration
public class KafkaAnalyticsService {

    @Bean
    public Function<KStream<String, PageEvent>,
            KStream<String, Long>> pageEventAnalytics() {

        return input -> input

                .groupBy(
                        (key, event) -> event.getName(),
                        Grouped.with(
                                Serdes.String(),
                                new JsonSerde<>(PageEvent.class)
                        )
                )

                .count(
                        Materialized.as("page-count-store")
                )

                .toStream()

                .peek((page, count) ->
                        System.out.println(
                                "Analytics : " + page
                                        + " => " + count
                        )
                );
    }
}
