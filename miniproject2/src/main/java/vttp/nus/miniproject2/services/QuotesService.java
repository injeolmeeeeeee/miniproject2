package vttp.nus.miniproject2.services;

import java.io.StringReader;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

@Service
public class QuotesService {

    RestTemplate restTemplate = new RestTemplate();

    public String getQuote() {
        String apiUrl = "https://api.quotable.io/random";

        try {
            String json = restTemplate.getForObject(apiUrl, String.class);
            JsonReader reader = Json.createReader(new StringReader(json));
            JsonObject quoteObject = reader.readObject();

            String content = quoteObject.getString("content");
            String author = quoteObject.getString("author");

            return "\"" + content + "\" - " + author;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while fetching quote";
        }
    }
}