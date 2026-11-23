
package ma.emsi.kafkastreamsapp.services;

import ma.emsi.kafkastreamsapp.entities.PageEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;
import java.util.function.Supplier;

@Configuration
public class KafkaSupplierService {

    private final Random random = new Random();

    @Bean
    public Supplier<PageEvent> messageSupplier() {

        return () -> {

            PageEvent pageEvent = new PageEvent(
                    "P" + (random.nextInt(5) + 1),
                    "U" + (random.nextInt(5) + 1),
                    System.currentTimeMillis(),
                    random.nextInt(5000) + 100
            );

            System.out.println("Supplier : " + pageEvent);

            return pageEvent;
        };
    }
}
