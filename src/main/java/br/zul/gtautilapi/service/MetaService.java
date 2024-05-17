package br.zul.gtautilapi.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.stereotype.Service;

import br.zul.gtautilapi.controller.ReadMeataResponseBody;
import br.zul.gtautilapi.exception.UnexpectedException;
import br.zul.gtautilapi.model.GtaUtilCommand;
import br.zul.gtautilapi.model.ReadMetaRequestBody;
import br.zul.gtautilapi.model.ReadMetaWsRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MetaService {

    private final FileService fileService;
    private final GtaUtilService gtaUtilService;
    private final FileServerService fileServerService;

    public ReadMeataResponseBody readMeta(ReadMetaRequestBody requestBody) {
        var file = fileService.createTempFileByBase64(requestBody.getBase64());
        var command = new GtaUtilCommand()
            .name("exportmeta")
            .param("--input")
            .param(file);
        var commandResponse = gtaUtilService.run(command);
        var xmlFile = commandResponse.getLastLine() + ".xml";
        return ReadMeataResponseBody
            .builder()
            .name(fileService.getFilename(xmlFile))
            .content(fileService.readText(xmlFile))
            .build();
    }

    public void readMeta(ReadMetaWsRequest request) {
        var file = request.getFile();
        var inIs = fileServerService.read(file);
        var inBuffer = fileService.readAllBytes(inIs);
        var inBase64 = Base64.getEncoder().encodeToString(inBuffer);
        var requestBody = ReadMetaRequestBody
            .builder()
            .name(fileService.getFilename(request.getFile()))
            .base64(inBase64)
            .build();
        var responseBody = readMeta(requestBody);
        var is = new ByteArrayInputStream(responseBody.getContent().getBytes());
        fileServerService.write(file + ".xml", is);
    }

}
