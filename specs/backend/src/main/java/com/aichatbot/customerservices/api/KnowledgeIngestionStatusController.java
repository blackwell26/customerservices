package com.aichatbot.customerservices.api;

import java.util.List;

import com.aichatbot.customerservices.api.dto.KnowledgeIngestionStatusResponse;
import com.aichatbot.customerservices.service.KnowledgeIngestionStatusStore;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/kb")
public class KnowledgeIngestionStatusController {

    private final KnowledgeIngestionStatusStore statusStore;

    public KnowledgeIngestionStatusController(KnowledgeIngestionStatusStore statusStore) {
        this.statusStore = statusStore;
    }

    @GetMapping("/status")
    public List<KnowledgeIngestionStatusResponse> status(
            @RequestParam(required = false) String sourceName,
            @RequestParam(defaultValue = "20") int limit) {
        return statusStore.recentBySourceName(sourceName, limit).stream()
                .map(KnowledgeIngestionStatusResponse::from)
                .toList();
    }
}
