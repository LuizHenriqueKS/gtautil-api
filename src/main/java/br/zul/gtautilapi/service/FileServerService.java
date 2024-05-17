package br.zul.gtautilapi.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.zul.gtautilapi.exception.UnexpectedException;
import br.zul.gtautilapi.properties.ServerProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;


@Service
@RequiredArgsConstructor
@Log4j2
public class FileServerService {

    private final ServerProperties serverProperties;
    private RestTemplate restTemplate = new RestTemplate();

    public InputStream read(String file) {
        try {
            var body = Map.of("file", file);
            var resource = restTemplate.postForObject(buildURL("file/read"), body, Resource.class);
            return resource.getInputStream();
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public void write(String file, InputStream is) {
        try {
            var stream = new ByteArrayOutputStream();
            var buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                stream.write(buffer, 0, len);
            }
            var base64 = Base64.getEncoder().encodeToString(stream.toByteArray());
            var body = Map.of("file", file, "base64", base64);
            restTemplate.postForEntity(buildURL("file/write"), body, Void.class);
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    private URI buildURL(String path) {
        var urlBuilder = new StringBuilder();
        urlBuilder.append(serverProperties.getUrl());
        if (urlBuilder.toString().endsWith("/") && path.startsWith("/")) {
            urlBuilder.append(path.substring(1));
        } else if (urlBuilder.toString().endsWith("/") ^ path.startsWith("/")) {
            urlBuilder.append(path);
        } else {
            urlBuilder.append("/");
            urlBuilder.append(path);
        }
        return URI.create(urlBuilder.toString());
    }
    
}
