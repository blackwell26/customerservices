package com.aichatbot.customerservices.api;

import com.aichatbot.customerservices.api.dto.KnowledgeUploadResponse;
import com.aichatbot.customerservices.service.KnowledgeBaseIngestionService;

import jakarta.validation.constraints.NotBlank;

import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("/api/v1/kb")
public class KnowledgeBaseController {

    private final KnowledgeBaseIngestionService ingestionService;

    public KnowledgeBaseController(KnowledgeBaseIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public KnowledgeUploadResponse upload(
            Authentication authentication,
            @RequestPart("file") MultipartFile file,
            @RequestParam(required = false) String sourceName,
            @RequestParam(defaultValue = "false") boolean replaceExisting) {
        return ingestionService.ingest(file, sourceName, replaceExisting, authentication.getName());
    }
}
