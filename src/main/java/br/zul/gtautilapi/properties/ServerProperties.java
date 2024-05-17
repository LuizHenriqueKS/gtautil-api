package br.zul.gtautilapi.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import lombok.Data;

@Component
@Data
public class ServerProperties {
    
    private String url;
    private String websocketURL;

    @SuppressWarnings("unchecked")
    @PostConstruct
    public void onStartup() throws FileNotFoundException, IOException {
        var objectMapper = new ObjectMapper();
        try (var is = new FileInputStream("gtautil/server.json")) {
            Map<String, String> map = objectMapper.readValue(is, Map.class);
            this.url = map.get("url");
            this.websocketURL = map.get("websocketURL");
        }
    }
    
}
