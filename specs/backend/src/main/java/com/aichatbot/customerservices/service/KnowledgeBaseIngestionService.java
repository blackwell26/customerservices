package com.aichatbot.customerservices.service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aichatbot.customerservices.api.dto.KnowledgeUploadResponse;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class KnowledgeBaseIngestionService {

    private static final int CHUNK_SIZE = 900;
    private static final int CHUNK_OVERLAP = 120;
    private static final Pattern PRINTABLE_SEGMENT = Pattern.compile("[A-Za-z0-9][A-Za-z0-9 \\t,.;:!?()\\-_/]{3,}");

    private final KnowledgeDocumentStore documentStore;
    private final KnowledgeIngestionStatusStore statusStore;

    public KnowledgeBaseIngestionService(
            KnowledgeDocumentStore documentStore,
            KnowledgeIngestionStatusStore statusStore) {
        this.documentStore = documentStore;
        this.statusStore = statusStore;
    }

    public KnowledgeUploadResponse ingest(
            MultipartFile file,
            String sourceName,
            boolean replaceExisting,
            String uploadedBy) {
        String originalFileName = file == null ? null : safeFileName(file.getOriginalFilename());
        try {
            return ingestInternal(file, sourceName, replaceExisting, uploadedBy, originalFileName);
        } catch (KnowledgeDocumentValidationException ex) {
            statusStore.recordFailure(
                    normalizeAttemptSourceName(sourceName, originalFileName),
                    originalFileName,
                    resolveDocumentTypeOrNull(originalFileName, file == null ? null : file.getContentType()),
                    0,
                    uploadedBy,
                    ex.code(),
                    ex.getMessage());
            throw ex;
        }
    }

    private KnowledgeUploadResponse ingestInternal(
            MultipartFile file,
            String sourceName,
            boolean replaceExisting,
            String uploadedBy,
            String originalFileName) {
        validateFile(file);

        String documentType = resolveDocumentType(originalFileName, file.getContentType());
        String resolvedSourceName = normalizeSourceName(sourceName, originalFileName);
        String extractedText = extractText(documentType, file);
        List<String> chunkTexts = chunkText(extractedText);
        int version = documentStore.nextVersion(resolvedSourceName, replaceExisting);
        UUID documentId = UUID.randomUUID();

        List<KnowledgeChunkRecord> chunks = new ArrayList<>();
        for (int index = 0; index < chunkTexts.size(); index++) {
            chunks.add(new KnowledgeChunkRecord(
                    UUID.randomUUID(),
                    documentId,
                    index + 1,
                    chunkTexts.get(index),
                    null));
        }

        KnowledgeDocumentRecord record = new KnowledgeDocumentRecord(
                documentId,
                resolvedSourceName,
                originalFileName,
                documentType,
                file.getContentType(),
                version,
                uploadedBy,
                Instant.now(),
                extractedText,
                chunks);
        documentStore.save(record);
        statusStore.recordSuccess(
                record.sourceName(),
                record.originalFileName(),
                record.documentType(),
                record.version(),
                record.uploadedBy(),
                record.chunks().size(),
                record.extractedText().length());

        String status = replaceExisting && version > 1 ? "REPLACED" : (version > 1 ? "VERSIONED" : "CREATED");
        String message = replaceExisting && version > 1
                ? "Knowledge document replaced with the latest version."
                : "Knowledge document ingested and chunked for future retrieval.";

        return new KnowledgeUploadResponse(
                record.documentId(),
                record.sourceName(),
                record.originalFileName(),
                record.documentType(),
                record.version(),
                record.chunks().size(),
                record.extractedText().length(),
                status,
                message);
    }

    private String resolveDocumentTypeOrNull(String originalFileName, String contentType) {
        if (originalFileName == null || originalFileName.isBlank()) {
            if (contentType == null || contentType.isBlank()) {
                return null;
            }
            try {
                return resolveDocumentType("placeholder.txt", contentType);
            } catch (KnowledgeDocumentValidationException ex) {
                return null;
            }
        }

        try {
            return resolveDocumentType(originalFileName, contentType);
        } catch (KnowledgeDocumentValidationException ex) {
            return null;
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new KnowledgeDocumentValidationException(
                    HttpStatus.BAD_REQUEST,
                    "FILE_REQUIRED",
                    "A non-empty knowledge document must be provided.");
        }
    }

    private String resolveDocumentType(String originalFileName, String contentType) {
        String lowerName = originalFileName.toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".md") || lowerName.endsWith(".markdown")) {
            return "MARKDOWN";
        }
        if (lowerName.endsWith(".pdf")) {
            return "PDF";
        }
        if (lowerName.endsWith(".txt")) {
            return "TEXT";
        }

        if (contentType != null) {
            String normalizedContentType = contentType.toLowerCase(Locale.ROOT);
            if (normalizedContentType.contains("markdown")) {
                return "MARKDOWN";
            }
            if (normalizedContentType.equals("application/pdf")) {
                return "PDF";
            }
            if (normalizedContentType.startsWith("text/")) {
                return "TEXT";
            }
        }

        throw new KnowledgeDocumentValidationException(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "UNSUPPORTED_DOCUMENT_TYPE",
                "Only PDF, Markdown, and plain text documents are supported.");
    }

    private String extractText(String documentType, MultipartFile file) {
        try {
            byte[] bytes = file.getBytes();
            if ("PDF".equals(documentType)) {
                return extractTextFromPdf(bytes);
            }
            return normalizeMarkdownLikeText(new String(bytes, StandardCharsets.UTF_8));
        } catch (Exception ex) {
            throw new KnowledgeDocumentValidationException(
                    HttpStatus.BAD_REQUEST,
                    "DOCUMENT_READ_FAILED",
                    "The uploaded document could not be read.");
        }
    }

    private String extractTextFromPdf(byte[] bytes) {
        String raw = new String(bytes, StandardCharsets.ISO_8859_1);
        Matcher matcher = PRINTABLE_SEGMENT.matcher(raw);
        StringBuilder builder = new StringBuilder();
        while (matcher.find()) {
            String segment = matcher.group().trim();
            if (segment.length() >= 4) {
                if (!builder.isEmpty()) {
                    builder.append('\n');
                }
                builder.append(segment);
            }
        }

        String extracted = builder.toString().trim();
        if (extracted.isEmpty()) {
            throw new KnowledgeDocumentValidationException(
                    HttpStatus.BAD_REQUEST,
                    "PDF_TEXT_EXTRACTION_FAILED",
                    "The PDF did not contain extractable text.");
        }

        return normalizeWhitespace(extracted);
    }

    private String normalizeMarkdownLikeText(String text) {
        String cleaned = text
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replaceAll("(?m)^#{1,6}\\s*", "")
                .replaceAll("\\[(.+?)\\]\\((.+?)\\)", "$1")
                .replaceAll("(?m)^[-*+]\\s+", "")
                .replaceAll("```", "")
                .replaceAll("`", "");
        return normalizeWhitespace(cleaned);
    }

    private String normalizeWhitespace(String text) {
        return text.replaceAll("\\n{3,}", "\n\n").trim();
    }

    private List<String> chunkText(String extractedText) {
        if (extractedText.isBlank()) {
            throw new KnowledgeDocumentValidationException(
                    HttpStatus.BAD_REQUEST,
                    "EMPTY_CONTENT",
                    "The uploaded document did not contain usable text.");
        }

        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < extractedText.length()) {
            int end = Math.min(extractedText.length(), start + CHUNK_SIZE);
            if (end < extractedText.length()) {
                int lastParagraphBreak = extractedText.lastIndexOf("\n\n", end);
                int lastLineBreak = extractedText.lastIndexOf('\n', end);
                int splitPoint = Math.max(lastParagraphBreak, lastLineBreak);
                if (splitPoint > start + (CHUNK_SIZE / 2)) {
                    end = splitPoint;
                }
            }

            String chunk = extractedText.substring(start, end).trim();
            if (!chunk.isBlank()) {
                chunks.add(chunk);
            }

            if (end >= extractedText.length()) {
                break;
            }

            start = Math.max(end - CHUNK_OVERLAP, start + 1);
        }

        if (chunks.isEmpty()) {
            chunks.add(extractedText);
        }

        return chunks;
    }

    private String normalizeSourceName(String sourceName, String originalFileName) {
        String resolved = sourceName == null || sourceName.isBlank()
                ? stripExtension(originalFileName)
                : sourceName.trim();
        if (resolved.isBlank()) {
            throw new KnowledgeDocumentValidationException(
                    HttpStatus.BAD_REQUEST,
                    "SOURCE_NAME_REQUIRED",
                    "A source name could not be resolved for the uploaded document.");
        }

        return resolved;
    }

    private String normalizeAttemptSourceName(String sourceName, String originalFileName) {
        if (sourceName != null && !sourceName.isBlank()) {
            return sourceName.trim();
        }
        return originalFileName == null || originalFileName.isBlank() ? null : stripExtension(originalFileName);
    }

    private String stripExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
    }

    private String safeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new KnowledgeDocumentValidationException(
                    HttpStatus.BAD_REQUEST,
                    "FILE_NAME_REQUIRED",
                    "The uploaded file must have a filename.");
        }

        return java.nio.file.Paths.get(originalFileName).getFileName().toString();
    }
}
