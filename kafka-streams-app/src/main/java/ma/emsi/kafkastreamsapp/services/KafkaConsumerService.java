
package ma.emsi.kafkastreamsapp.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class KafkaConsumerService {

    @Bean
    public Consumer<String> messageConsumer() {
        return message -> {
            System.out.println("----------------------------");
            System.out.println("Message reçu : " + message);
            System.out.println("----------------------------");
        };
    }
}
