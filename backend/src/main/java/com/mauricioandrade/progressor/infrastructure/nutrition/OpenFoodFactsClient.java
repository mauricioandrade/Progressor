package com.mauricioandrade.progressor.infrastructure.nutrition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mauricioandrade.progressor.core.application.dto.FoodItemResponse;
import com.mauricioandrade.progressor.core.application.ports.FoodSearchPort;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import org.springframework.cache.annotation.Cacheable;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
  @Cacheable(value = "food-search", key = "#query.toLowerCase().trim()")
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

  @Override
  @Cacheable(value = "food-barcode", key = "#barcode")
  public Optional<FoodItemResponse> lookupByBarcode(String barcode) {
    log.info("[OpenFoodFacts] Barcode lookup: '{}'", barcode);

    URI uri = UriComponentsBuilder.newInstance()
        .scheme("https")
        .host("world.openfoodfacts.org")
        .path("/api/v3/product/{barcode}.json")
        .buildAndExpand(barcode)
        .encode()
        .toUri();

    String body;
    try {
      body = restClient.get()
          .uri(uri)
          .header("User-Agent", "ProgressorApp/1.0 (github.com/mauricioandrade/progressor)")
          .retrieve()
          .body(String.class);
    } catch (RestClientException e) {
      log.error("[OpenFoodFacts] Barcode lookup failed — {}", e.getMessage(), e);
      throw new RuntimeException("Open Food Facts barcode lookup failed: " + e.getMessage(), e);
    }

    if (body == null || body.isBlank()) return Optional.empty();

    try {
      JsonNode root = objectMapper.readTree(body);
      if (!"success".equals(root.path("status").asText())) return Optional.empty();
      JsonNode product = root.path("product");
      return Optional.ofNullable(parseProduct(product));
    } catch (Exception e) {
      log.error("[OpenFoodFacts] Barcode parse error: {}", e.getMessage(), e);
      return Optional.empty();
    }
  }

  private FoodItemResponse parseProduct(JsonNode product) {
    String name = product.path("product_name_pt").asText("").trim();
    if (name.isEmpty()) name = product.path("product_name").asText("").trim();
    if (name.isEmpty()) return null;

    String id = product.path("id").asText("");
    String rawBrands = product.path("brands").asText("").trim();
    String brandName = rawBrands.isEmpty() ? null : rawBrands.split(",")[0].trim();
    if (brandName != null && brandName.isEmpty()) brandName = null;

    JsonNode nutriments = product.path("nutriments");
    double calories = nutriments.path("energy-kcal_100g").asDouble(0.0);
    double protein  = nutriments.path("proteins_100g").asDouble(0.0);
    double carbs    = nutriments.path("carbohydrates_100g").asDouble(0.0);
    double fat      = nutriments.path("fat_100g").asDouble(0.0);

    String description = String.format(
        "Por 100g — Cal: %.0fkcal | P: %.1fg | C: %.1fg | G: %.1fg",
        calories, protein, carbs, fat);

    return new FoodItemResponse(id, name, brandName, calories, protein, carbs, fat, description);
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
      FoodItemResponse item = parseProduct(product);
      if (item != null) results.add(item);
    }

    return results;
  }
}
