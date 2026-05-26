package com.example.demo.service;

import com.example.demo.dto.ReceiptData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final String GEMINI_URL =
        "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent";

    private static final String PROMPT = """
        Du är en kvittoläsare. Extrahera data från detta svenska kvitto och returnera ENDAST ett JSON-objekt.

        Använd exakt denna struktur:
        {
          "store": "butiksnamn",
          "date": "DD/MM-YY",
          "time": "HH:MM",
          "items": [
            { "name": "artikelnamn", "price": "12.99", "isDiscount": false }
          ],
          "total": "1800.00",
          "moms": "360.00"
        }

        Regler:
        - Priser som decimalsträngar utan valutasymboler (t.ex. "1800.00")
        - Rabatter som negativa priser (t.ex. "-495.00") med isDiscount: true
        - Om ett fält saknas på kvittot, använd en tom sträng ""
        - Returnera ENDAST JSON, ingen förklaring
        """;

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ReceiptData scanReceipt(byte[] imageBytes, String mimeType) throws Exception {
        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        Map<String, Object> body = Map.of(
            "contents", List.of(Map.of(
                "parts", List.of(
                    Map.of("text", PROMPT),
                    Map.of("inline_data", Map.of(
                        "mime_type", mimeType != null ? mimeType : "image/jpeg",
                        "data", base64
                    ))
                )
            )),
            "generationConfig", Map.of(
                "temperature", 0.1,
                "responseMimeType", "application/json"
            )
        );

        String response = restClient.post()
            .uri(GEMINI_URL + "?key=" + apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .body(body)
            .retrieve()
            .body(String.class);

        JsonNode root = objectMapper.readTree(response);
        String json = root.path("candidates")
            .get(0).path("content").path("parts")
            .get(0).path("text").asText();

        return objectMapper.readValue(json, ReceiptData.class);
    }
}
