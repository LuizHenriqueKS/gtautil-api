package br.zul.gtautilapi.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WsMessage {
    
    private Double requestId;
    private Double clientId;
    private String cmd;
    private Object data;

}
