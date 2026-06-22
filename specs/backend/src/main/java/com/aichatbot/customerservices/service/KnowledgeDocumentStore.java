package com.aichatbot.customerservices.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class KnowledgeDocumentStore {

    private final Map<String, NavigableMap<Integer, KnowledgeDocumentRecord>> documentsBySource = new ConcurrentHashMap<>();

    public synchronized int nextVersion(String sourceName, boolean replaceExisting) {
        NavigableMap<Integer, KnowledgeDocumentRecord> versions = documentsBySource.get(sourceName);
        if (versions == null || versions.isEmpty()) {
            return 1;
        }

        Integer latestVersion = versions.lastKey();
        return replaceExisting ? latestVersion : latestVersion + 1;
    }

    public synchronized KnowledgeDocumentRecord save(KnowledgeDocumentRecord document) {
        documentsBySource.computeIfAbsent(document.sourceName(), key -> new TreeMap<>())
                .put(document.version(), document);
        return document;
    }

    public synchronized Optional<KnowledgeDocumentRecord> findLatestBySourceName(String sourceName) {
        NavigableMap<Integer, KnowledgeDocumentRecord> versions = documentsBySource.get(sourceName);
        if (versions == null || versions.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(versions.lastEntry())
                .map(Map.Entry::getValue);
    }

    public synchronized List<KnowledgeDocumentRecord> snapshot() {
        return documentsBySource.values().stream()
                .flatMap(versions -> versions.values().stream())
                .sorted(Comparator.comparing(KnowledgeDocumentRecord::uploadedAt))
                .toList();
    }
}
