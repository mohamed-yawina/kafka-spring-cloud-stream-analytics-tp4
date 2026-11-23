
package ma.emsi.kafkastreamsapp.web;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ProducerController {

    private final StreamBridge streamBridge;

    public ProducerController(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    @GetMapping("/publish/{topic}/{message}")
    public ResponseEntity<String> publish(
            @PathVariable String topic,
            @PathVariable String message) {

        boolean sent = streamBridge.send(topic, message);

        if (sent) {
            return ResponseEntity.ok(
                    "Message envoyé vers le topic : " + topic
            );
        }

        return ResponseEntity.internalServerError()
                .body("Erreur lors de l'envoi du message");
    }
}
