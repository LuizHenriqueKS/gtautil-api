package br.zul.gtautilapi.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(fluent = true)
public class GtaUtilCommand {

    private String name;
    private List<String> params = new ArrayList<>();

    public GtaUtilCommand param(String param) {
        params.add(param);
        return this;
    }

}
