package com.behsazan.schemaforge.validation.spelling;

import com.behsazan.schemaforge.configuration.properties.SchemaForgeProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LanguageTool HTTP client optimized for schema identifiers.
 *
 * <p>The service never changes identifiers. It normalizes separators only for the remote check,
 * filters configured technical terms, limits suggestions, caches results and supports fail-open
 * operation so DDL generation does not depend on the availability of an external service.</p>
 */
public final class LanguageToolSpellCheckService implements SpellCheckService {

    private record CacheEntry(Instant expiresAt, List<SpellingError> errors) {}

    private final SchemaForgeProperties.SpellCheck config;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Clock clock;
    private final Set<String> technicalTerms;
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public LanguageToolSpellCheckService(
            SchemaForgeProperties.SpellCheck config,
            ObjectMapper objectMapper) {
        this(config,
                HttpClient.newBuilder().connectTimeout(config.connectTimeout()).build(),
                objectMapper,
                Clock.systemUTC());
    }

    LanguageToolSpellCheckService(
            SchemaForgeProperties.SpellCheck config,
            HttpClient httpClient,
            ObjectMapper objectMapper,
            Clock clock) {
        this.config = Objects.requireNonNull(config, "config must not be null");
        this.httpClient = Objects.requireNonNull(httpClient, "httpClient must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
        this.technicalTerms = normalizeTerms(config.technicalTerms());
    }

    @Override
    public List<SpellingError> check(String text) {
        if (!config.enabled() || text == null || text.isBlank()) {
            return List.of();
        }

        String normalizedText = normalizeIdentifier(text);
        if (normalizedText.isBlank() || allWordsAreTechnical(normalizedText)) {
            return List.of();
        }

        String cacheKey = config.language().toLowerCase(Locale.ROOT) + "|" + normalizedText.toLowerCase(Locale.ROOT);
        CacheEntry cached = cache.get(cacheKey);
        Instant now = clock.instant();
        if (cached != null && cached.expiresAt().isAfter(now)) {
            return cached.errors();
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.endpoint()))
                    .timeout(config.requestTimeout())
                    .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(createRequestBody(normalizedText)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return handleFailure("LanguageTool returned HTTP " + response.statusCode());
            }

            List<SpellingError> errors = parseResponse(normalizedText, response.body());
            cache.put(cacheKey, new CacheEntry(now.plus(config.cacheTtl()), errors));
            return errors;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return handleFailure("Spell-check request was interrupted");
        } catch (IOException | RuntimeException exception) {
            return handleFailure("Spell-check service unavailable: " + exception.getMessage());
        }
    }

    private List<SpellingError> parseResponse(String text, String responseBody) throws IOException {
        JsonNode matches = objectMapper.readTree(responseBody).path("matches");
        if (!matches.isArray()) {
            return List.of();
        }

        List<SpellingError> errors = new ArrayList<>();
        for (JsonNode match : matches) {
            String issueType = match.path("rule").path("issueType").asText("");
            String categoryId = match.path("rule").path("category").path("id").asText("");
            if (!"misspelling".equalsIgnoreCase(issueType) && !"TYPOS".equalsIgnoreCase(categoryId)) {
                continue;
            }

            String word = extractWord(text, match.path("offset").asInt(-1), match.path("length").asInt(0));
            if (word.isBlank() || technicalTerms.contains(normalizeWord(word))) {
                continue;
            }

            List<SpellingSuggestion> suggestions = new ArrayList<>();
            JsonNode replacements = match.path("replacements");
            if (replacements.isArray()) {
                for (JsonNode replacement : replacements) {
                    String value = replacement.path("value").asText("").trim();
                    if (!value.isBlank()) {
                        suggestions.add(new SpellingSuggestion(value));
                    }
                    if (suggestions.size() >= config.maximumSuggestions()) {
                        break;
                    }
                }
            }

            errors.add(new SpellingError(
                    word,
                    match.path("message").asText("Possible spelling error"),
                    suggestions));
        }
        return List.copyOf(errors);
    }

    private String createRequestBody(String text) {
        return "language=" + encode(config.language())
                + "&text=" + encode(text)
                + "&enabledOnly=false";
    }

    private String normalizeIdentifier(String identifier) {
        return identifier.replace('_', ' ').replace('-', ' ').replaceAll("\\s+", " ").trim();
    }

    private boolean allWordsAreTechnical(String text) {
        String[] words = text.split("\\s+");
        if (words.length == 0) {
            return false;
        }
        for (String word : words) {
            if (!technicalTerms.contains(normalizeWord(word))) {
                return false;
            }
        }
        return true;
    }

    private Set<String> normalizeTerms(List<String> terms) {
        Set<String> result = new LinkedHashSet<>();
        if (terms != null) {
            for (String term : terms) {
                if (term != null && !term.isBlank()) {
                    result.add(normalizeWord(term));
                }
            }
        }
        return Set.copyOf(result);
    }

    private String normalizeWord(String value) {
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private String extractWord(String text, int offset, int length) {
        if (offset < 0 || length <= 0 || offset >= text.length()) {
            return "";
        }
        return text.substring(offset, Math.min(offset + length, text.length()));
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private List<SpellingError> handleFailure(String message) {
        if (config.failOpen()) {
            return List.of();
        }
        throw new IllegalStateException(message);
    }
}
