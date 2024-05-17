package br.zul.gtautilapi.model;

import lombok.Getter;

public class GtaUtilCommandResponse {
    
    @Getter
    private String text;

    public GtaUtilCommandResponse(String text) {
        this.text = text;
    }

    public String getLastLine() {
        var lines = text.trim().replace("\r", "").split("\n");
        return lines[lines.length - 1];
    }

}
