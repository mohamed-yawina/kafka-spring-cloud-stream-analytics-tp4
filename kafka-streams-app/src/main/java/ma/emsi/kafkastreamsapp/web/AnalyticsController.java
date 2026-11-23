
package ma.emsi.kafkastreamsapp.web;

import ma.emsi.kafkastreamsapp.services.AnalyticsResultService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
public class AnalyticsController {

    private final AnalyticsResultService analyticsResultService;

    public AnalyticsController(
            AnalyticsResultService analyticsResultService) {

        this.analyticsResultService = analyticsResultService;
    }

    @GetMapping("/analytics")
    public Map<String, Long> getAnalytics() {

        return analyticsResultService.getStatistics();
    }
}
