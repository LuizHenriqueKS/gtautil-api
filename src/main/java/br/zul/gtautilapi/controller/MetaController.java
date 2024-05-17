package br.zul.gtautilapi.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.zul.gtautilapi.model.ReadMetaRequestBody;
import br.zul.gtautilapi.service.MetaService;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("meta")
@RequiredArgsConstructor
public class MetaController {
    
    private final MetaService metaService;

    @PostMapping("read")
    public ReadMeataResponseBody readMeta(@RequestBody ReadMetaRequestBody requestBody) {
        return metaService.readMeta(requestBody);
    }

}
