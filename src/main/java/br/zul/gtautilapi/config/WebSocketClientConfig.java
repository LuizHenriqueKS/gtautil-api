package br.zul.gtautilapi.config;

import java.net.URI;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import br.zul.gtautilapi.controller.WebSocketClientController;
import br.zul.gtautilapi.exception.UnexpectedException;
import br.zul.gtautilapi.properties.ServerProperties;
import br.zul.gtautilapi.service.FileService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Configuration
@Log4j2
@RequiredArgsConstructor
public class WebSocketClientConfig {

    private final ServerProperties serverProperties;
    private final FileService fileService;
    private final WebSocketClientController webSocketClientController;

    @PostConstruct
    public void connect() {
        var url = serverProperties.getWebsocketURL();
        if (url != null) {

            WebSocketHttpHeaders handshakeHeaders = new WebSocketHttpHeaders();
            handshakeHeaders.set("Origin", "heaven");

            WebSocketClient client = new StandardWebSocketClient();
            client.execute(webSocketClientController, handshakeHeaders, URI.create(url));
            
            // WebSocketStompClient stompClient = new WebSocketStompClient(client);
            // stompClient.setMessageConverter(new MappingJackson2MessageConverter());

            // StompSessionHandler sessionHandler = webSocketClientController;

            // stompClient.connectAsync(url, handshakeHeaders, sessionHandler);
        }
    }

    @Scheduled(fixedRate = 10000)
    public void reconnect() {
        if (!webSocketClientController.isConnected() && webSocketClientController.wasAlreadyConnected()) {
            log.info("Reconnecting websocket...");
            connect();
        }
    }

    private String getURL() {
        try {
            return fileService.readText("gtautil\\websocket.txt");
        } catch (UnexpectedException ex) {
            log.error("Cannot start websocket!!! {}", ex.getMessage());
            return null;
        }
    }

}
