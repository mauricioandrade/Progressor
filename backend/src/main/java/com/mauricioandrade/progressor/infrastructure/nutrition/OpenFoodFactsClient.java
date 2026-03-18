package com.mauricioandrade.progressor.infrastructure.nutrition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mauricioandrade.progressor.core.application.dto.FoodItemResponse;
import com.mauricioandrade.progressor.core.application.ports.FoodSearchPort;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Open Food Facts implementation of {@link FoodSearchPort}.
 *
 * <p>No authentication required. Uses the Brazilian subdomain to prioritise local products:
 * <pre>GET https://br.openfoodfacts.org/cgi/search.pl
 *   ?search_terms={query}&amp;search_simple=1&amp;action=process&amp;json=1&amp;page_size=20</pre>
 *
 * <p>Macros are extracted from the {@code nutriments} object (per-100g fields).
 * Missing fields default to {@code 0.0} — community data is often incomplete.
 *
 * <p>Uses Spring's {@link RestClient} with {@link SimpleClientHttpRequestFactory} (blocking
 * {@code HttpURLConnection}) to avoid {@link java.nio.channels.UnresolvedAddressException}
 * on Windows/Java 25 configurations with async NIO DNS resolution.
 */
@Component
public class OpenFoodFactsClient implements FoodSearchPort {

  private static final Logger log = LoggerFactory.getLogger(OpenFoodFactsClient.class);

  private static final String API_HOST = "br.openfoodfacts.org";
  private static final String API_PATH = "/cgi/search.pl";
  private static final Duration TIMEOUT = Duration.ofSeconds(30);

  private RestClient restClient;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @PostConstruct
  void init() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout((int) TIMEOUT.toMillis());
    factory.setReadTimeout((int) TIMEOUT.toMillis());

    restClient = RestClient.builder()
        .requestFactory(factory)
        .build();

    log.info("[OpenFoodFacts] Client initialised — endpoint: https://{}{}", API_HOST, API_PATH);
  }

  @Override
  public List<FoodItemResponse> search(String query) {
    log.info("[OpenFoodFacts] Searching: '{}'", query);

    URI uri = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host(API_HOST)
        .path(API_PATH)
        .queryParam("search_terms", query)
        .queryParam("search_simple", 1)
        .queryParam("action", "process")
        .queryParam("json", 1)
        .queryParam("page_size", 20)
        .encode()
        .build()
        .toUri();

    log.info("[OpenFoodFacts] Request → {}", uri);

    String body;
    try {
      body = restClient.get()
          .uri(uri)
          .header("User-Agent", "ProgressorApp/1.0 (github.com/mauricioandrade/progressor)")
          .retrieve()
          .body(String.class);
    } catch (RestClientException e) {
      String msg = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
      log.error("[OpenFoodFacts] Request failed — type={} message={}",
          e.getClass().getSimpleName(), msg, e);
      throw new RuntimeException("Open Food Facts request failed: " + msg, e);
    }

    if (body == null || body.isBlank()) {
      log.warn("[OpenFoodFacts] Empty response body for query '{}'", query);
      return List.of();
    }

    log.debug("[OpenFoodFacts] Response length: {} chars", body.length());
    return parseResponse(body);
  }

  private List<FoodItemResponse> parseResponse(String json) {
    List<FoodItemResponse> results = new ArrayList<>();
    JsonNode root;
    try {
      root = objectMapper.readTree(json);
    } catch (Exception e) {
      throw new RuntimeException("Invalid JSON from Open Food Facts: " + e.getMessage(), e);
    }

    JsonNode products = root.path("products");
    if (products.isMissingNode() || !products.isArray()) {
      log.warn("[OpenFoodFacts] No 'products' array in response");
      return results;
    }

    log.info("[OpenFoodFacts] Processing {} products", products.size());

    for (JsonNode product : products) {
      // Name: prefer Portuguese localisation, fall back to generic product_name
      String name = product.path("product_name_pt").asText("").trim();
      if (name.isEmpty()) {
        name = product.path("product_name").asText("").trim();
      }
      if (name.isEmpty()) continue;

      String id = product.path("id").asText("");

      // Brand: take the whole brands string (e.g. "Tio João" or "Nestlé, Nescau")
      String rawBrands = product.path("brands").asText("").trim();
      // Use only the first brand if comma-separated, to keep the UI badge concise
      String brandName = rawBrands.isEmpty() ? null : rawBrands.split(",")[0].trim();
      if (brandName != null && brandName.isEmpty()) brandName = null;

      // Macros from nutriments — all per 100g; default to 0.0 if absent
      JsonNode nutriments = product.path("nutriments");
      double calories = nutriments.path("energy-kcal_100g").asDouble(0.0);
      double protein  = nutriments.path("proteins_100g").asDouble(0.0);
      double carbs    = nutriments.path("carbohydrates_100g").asDouble(0.0);
      double fat      = nutriments.path("fat_100g").asDouble(0.0);

      String description = String.format(
          "Por 100g — Cal: %.0fkcal | P: %.1fg | C: %.1fg | G: %.1fg",
          calories, protein, carbs, fat);

      log.debug("[OpenFoodFacts] '{}' brand='{}' cal={} prot={} carbs={} fat={}",
          name, brandName, calories, protein, carbs, fat);

      results.add(new FoodItemResponse(id, name, brandName, calories, protein, carbs, fat,
          description));
    }

    return results;
  }
}
