package br.zul.gtautilapi.controller;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.zul.gtautilapi.exception.UnexpectedException;
import br.zul.gtautilapi.model.ReadMetaWsRequest;
import br.zul.gtautilapi.model.WsMessage;
import br.zul.gtautilapi.service.MetaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
@RequiredArgsConstructor
public class WebSocketClientController implements WebSocketHandler {

    private final MetaService metaService;

    private ObjectMapper objectMapper = new ObjectMapper();
    private WebSocketSession session;
    private boolean alreadyConnected;

    public boolean isConnected() {
        return session != null;
    }

    public boolean wasAlreadyConnected() {
        return alreadyConnected;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        if (this.session != null) {
            this.session.close();
        }
        this.session = session;
        alreadyConnected = true;
        log.info("Websocket connected");
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        WsMessage wsMsg = objectMapper.readValue(message.getPayload().toString(), WsMessage.class);
        if ("ping".equals(wsMsg.getCmd())) {
            reply(wsMsg, "pong");
        } else if ("readMetaFile".equals(wsMsg.getCmd())) {
            metaService.readMeta(ReadMetaWsRequest.builder().file((String)wsMsg.getData()).build());
            reply(wsMsg, "readMetaFile");
        }
    }

    public void reply(WsMessage wsMsg, String mg) {
        reply(wsMsg, mg, null);
    }

    public void reply(WsMessage wsMsg, String cmd, Object data) {
        try {
            var repliedMsg = WsMessage.builder()
                .clientId(wsMsg.getClientId())
                .requestId(wsMsg.getRequestId())
                .cmd(cmd)
                .data(data)
                .build();
            var payload = objectMapper.writeValueAsString(repliedMsg);
            session.sendMessage(new TextMessage(payload));
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        this.session = null;
        log.error(exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        this.session = null;
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    public void send(WsMessage message) {
        try {
            var str = objectMapper.writeValueAsString(message);
            this.session.sendMessage(new WebSocketMessage<String>() {

                @Override
                public String getPayload() {
                    return str;
                }

                @Override
                public int getPayloadLength() {
                    return str.length();
                }

                @Override
                public boolean isLast() {
                    return true;
                }

            });
        } catch (Exception ex) {
            log.error(ex);
        }
    }
    
}
