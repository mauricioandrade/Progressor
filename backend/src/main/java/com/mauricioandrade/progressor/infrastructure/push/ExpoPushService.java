package com.mauricioandrade.progressor.infrastructure.push;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ExpoPushService {

    private static final Logger log = LoggerFactory.getLogger(ExpoPushService.class);
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";

    private final RestClient restClient;

    public ExpoPushService() {
        this.restClient = RestClient.create();
    }

    public void send(String pushToken, String title, String body) {
        if (pushToken == null || pushToken.isBlank()) return;
        try {
            var payload = Map.of(
                "to", pushToken,
                "title", title,
                "body", body,
                "sound", "default"
            );
            restClient.post()
                .uri(EXPO_PUSH_URL)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(payload)
                .retrieve()
                .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Falha ao enviar push para {}: {}", pushToken, e.getMessage());
        }
    }

    public void sendBatch(List<String> pushTokens, String title, String body) {
        pushTokens.forEach(token -> send(token, title, body));
    }
}
