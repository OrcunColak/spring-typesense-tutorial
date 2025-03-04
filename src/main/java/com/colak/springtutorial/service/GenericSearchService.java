package com.colak.springtutorial.service;

import com.colak.springtutorial.model.BaseModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.typesense.api.FieldTypes;
import org.typesense.model.CollectionSchema;
import org.typesense.model.Field;
import org.typesense.model.SearchParameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.typesense.api.Client;
import org.typesense.model.SearchResultHit;

@Service
@RequiredArgsConstructor
@Slf4j
public class GenericSearchService<T extends BaseModel> {
    public static final String CONTENT = "content";
    private final Client typesenseClient;


    public void indexDocuments(String collectionName, List<T> documents) {
        try {
            ensureCollectionExists(collectionName);

            final List<Map<String, Object>> documentsToIndex = documentsToExtract(documents);

            indexDocumentsInternal(collectionName, documents, documentsToIndex);

        } catch (Exception e) {
            log.error("Error in indexing process for collection {}: {}",
                    collectionName, e.getMessage(), e);
        }
    }

    private void indexDocumentsInternal(String collectionName, List<T> documents, List<Map<String, Object>> documentsToIndex) {
        int successCount = 0;
        for (Map<String, Object> document : documentsToIndex)
            try {
                typesenseClient.collections(collectionName)
                        .documents()
                        .create(document);
                successCount++;
            } catch (Exception e) {
                log.error("Error indexing document {}: {}", document.get("id"), e.getMessage());
            }
        log.info("Successfully indexed {}/{} documents in collection {}",
                successCount, documents.size(), collectionName);
    }

    private static <T extends BaseModel> List<Map<String, Object>> documentsToExtract(List<T> documents) {
        List<Map<String, Object>> documentsToIndex = new ArrayList<>();
        for (T document : documents) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", document.getModelId());
            map.put(CONTENT, document.getSearchableContent());
            documentsToIndex.add(map);
        }
        return documentsToIndex;
    }

    private void deleteCollection(String collectionName) {
        try {
            typesenseClient.collections(collectionName).delete();
            log.info("Deleted existing collection: {}", collectionName);
        } catch (Exception e) {
            log.debug("Collection {} doesn't exist yet", collectionName);
        }
    }

    public List<Map<String, Object>> search(String query, String collectionName) {
        try {
            if (query == null || query.trim().isEmpty()) {
                log.warn("Empty search query received");
                return List.of();
            }

            SearchParameters searchParameters = new SearchParameters()
                    .q(query)
                    .queryBy(CONTENT)
                    .perPage(100);  // Adjust this value based on your needs

            return typesenseClient.collections(collectionName)
                    .documents()
                    .search(searchParameters)
                    .getHits()
                    .stream()
                    .map(SearchResultHit::getDocument)
                    .toList();

        } catch (Exception e) {
            log.error("Error searching in collection {}: {}",
                    collectionName, e.getMessage(), e);
            return List.of();
        }
    }

    private void createCollection(String collectionName) {
        try {
            List<Field> fields = new ArrayList<>();
            fields.add(new Field()
                    .name("id")
                    .type(FieldTypes.STRING));

            fields.add(new Field()
                    .name(CONTENT)
                    .type(FieldTypes.STRING));

            CollectionSchema schema = new CollectionSchema()
                    .name(collectionName)
                    .fields(fields);

            typesenseClient.collections().create(schema);
            log.info("Created new collection: {}", collectionName);

        } catch (Exception e) {
            log.error("Error creating collection {}: {}",
                    collectionName, e.getMessage(), e);
        }
    }

    private boolean collectionExists(String collectionName) {
        try {
            typesenseClient.collections(collectionName).retrieve();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void ensureCollectionExists(String collectionName) {
        if (!collectionExists(collectionName)) {
            createCollection(collectionName);
        } else {
            log.debug("Collection {} already exists", collectionName);
        }
    }
}
