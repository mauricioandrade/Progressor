package com.mauricioandrade.progressor.infrastructure.nutrition;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mauricioandrade.progressor.core.application.dto.FoodItemResponse;
import com.mauricioandrade.progressor.core.application.ports.FoodSearchPort;
import jakarta.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * USDA FoodData Central implementation of {@link FoodSearchPort}.
 *
 * <p><strong>Superseded by {@link FatSecretNutritionClient} as of V1.2.</strong>
 * {@code @Component} is intentionally removed so Spring does not register this as an
 * active {@link FoodSearchPort} bean. Kept for reference and easy rollback.
 *
 * <p>Uses the JDK {@link HttpClient} (Java 11+) for reliable HTTPS with:
 * <ul>
 *   <li>30-second connect + request timeout (avoids I/O hangs)</li>
 *   <li>NORMAL redirect following</li>
 *   <li>System default SSLContext (respects JVM cacerts / OS trust store)</li>
 *   <li>User-Agent header required by some government APIs</li>
 * </ul>
 *
 * <p>Nutrient IDs: 1003 = Protein | 1004 = Fat | 1005 = Carbs | 1008 = Energy (kcal).
 * Foundation and SR Legacy values are per 100 g.
 */
public class UsdaClient implements FoodSearchPort {

  private static final Logger log = LoggerFactory.getLogger(UsdaClient.class);

  private static final String API_URL = "https://api.nal.usda.gov/fdc/v1/foods/search";
  private static final Duration TIMEOUT = Duration.ofSeconds(30);

  private static final int NUTRIENT_PROTEIN = 1003;
  private static final int NUTRIENT_FAT     = 1004;
  private static final int NUTRIENT_CARBS   = 1005;
  private static final int NUTRIENT_ENERGY  = 1008;

  @Value("${usda.api-key}")
  private String apiKey;

  private HttpClient httpClient;
  private final ObjectMapper objectMapper = new ObjectMapper();

  @PostConstruct
  void init() {
    apiKey = apiKey.trim();
    // JDK HttpClient: connect timeout at the client level, request timeout per-request.
    // Uses the JVM default SSLContext (picks up cacerts + OS trust store automatically).
    httpClient = HttpClient.newBuilder()
        .connectTimeout(TIMEOUT)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build();
    log.info("[USDA] Client initialised — key length={}", apiKey.length());
  }

  @Override
  public List<FoodItemResponse> search(String query) {
    String englishQuery = BrazilianFoodTranslation.toEnglishQuery(query);
    log.info("[USDA] Searching: '{}' → '{}'", query, englishQuery);

    // encode() ensures all param values (spaces, special chars) are percent-encoded.
    // dataType is passed twice so the USDA API receives it as an array:
    //   ?dataType=Foundation&dataType=SR%20Legacy
    URI uri = UriComponentsBuilder.fromUriString(API_URL)
        .queryParam("api_key", apiKey)
        .queryParam("query", englishQuery)
        .queryParam("pageSize", 20)
        .queryParam("dataType", "Foundation")
        .queryParam("dataType", "SR Legacy")
        .encode()
        .build()
        .toUri();

    // Log URL with key masked so we can verify structure without exposing the key
    String maskedUri = uri.toString().replace(apiKey,
        apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4));
    log.info("[USDA] Request → {}", maskedUri);

    HttpRequest request = HttpRequest.newBuilder(uri)
        .GET()
        .timeout(TIMEOUT)
        .header("User-Agent", "ProgressorApp/1.0")
        .header("Accept",     "application/json")
        .build();

    String body;
    try {
      HttpResponse<String> response = httpClient.send(request,
          HttpResponse.BodyHandlers.ofString());
      int status = response.statusCode();
      log.info("[USDA] HTTP {}", status);

      if (status == 403) {
        log.error("[USDA] 403 Forbidden — check USDA_API_KEY value (length={})", apiKey.length());
        throw new RuntimeException("USDA API key is invalid or missing");
      }
      if (status != 200) {
        log.error("[USDA] Unexpected status {} — body: {}", status,
            response.body().substring(0, Math.min(300, response.body().length())));
        throw new RuntimeException("USDA API returned HTTP " + status);
      }

      body = response.body();
      log.debug("[USDA] Response length: {} chars", body.length());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("USDA API request was interrupted", e);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      log.error("[USDA] I/O error: {}", e.getMessage(), e);
      throw new RuntimeException("USDA API I/O error: " + e.getMessage(), e);
    }

    return parseResponse(body);
  }

  private List<FoodItemResponse> parseResponse(String json) {
    List<FoodItemResponse> results = new ArrayList<>();
    JsonNode root;
    try {
      root = objectMapper.readTree(json);
    } catch (Exception e) {
      throw new RuntimeException("Invalid JSON from USDA: " + e.getMessage(), e);
    }

    JsonNode foods = root.path("foods");
    if (foods.isMissingNode() || !foods.isArray()) {
      log.warn("[USDA] No 'foods' array in response. Keys present: {}", root.fieldNames());
      return results;
    }

    log.info("[USDA] Processing {} food items", foods.size());

    for (JsonNode food : foods) {
      String id        = food.path("fdcId").asText("");
      String usdaName  = food.path("description").asText("").trim();
      if (usdaName.isEmpty()) continue;
      String name = BrazilianFoodTranslation.toPortugueseName(usdaName);

      double calories = 0, protein = 0, fat = 0, carbs = 0;

      JsonNode nutrients = food.path("foodNutrients");
      if (nutrients.isArray()) {
        for (JsonNode n : nutrients) {
          int nId = n.path("nutrientId").asInt(-1);
          double value = n.path("value").asDouble(0);
          switch (nId) {
            case NUTRIENT_ENERGY  -> calories = value;
            case NUTRIENT_PROTEIN -> protein  = value;
            case NUTRIENT_FAT     -> fat      = value;
            case NUTRIENT_CARBS   -> carbs    = value;
            default -> { /* ignore other nutrients */ }
          }
        }
      }

      String description = String.format(
          "Por 100g — Cal: %.0fkcal | P: %.1fg | C: %.1fg | G: %.1fg",
          calories, protein, carbs, fat);

      log.debug("[USDA] '{}' → '{}' cal={} prot={} carbs={} fat={}", usdaName, name, calories, protein, carbs, fat);
      results.add(new FoodItemResponse(id, name, null, calories, protein, carbs, fat, description));
    }

    return results;
  }
}
