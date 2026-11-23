
package ma.emsi.kafkastreamsapp.services;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.messaging.Message;
import org.springframework.kafka.support.KafkaHeaders;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class AnalyticsResultService {

    private final Map<String, Long> statistics =
            new ConcurrentHashMap<>();

    @Bean
    public Consumer<Message<Long>> analyticsConsumer() {

        return message -> {

            String page = message.getHeaders()
                    .get(KafkaHeaders.RECEIVED_KEY, String.class);

            Long count = message.getPayload();

            if (page != null) {
                statistics.put(page, count);
            }
        };
    }

    public Map<String, Long> getStatistics() {
        return new TreeMap<>(statistics);
    }
}
