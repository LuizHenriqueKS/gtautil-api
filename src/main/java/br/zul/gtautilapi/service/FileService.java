package br.zul.gtautilapi.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.springframework.stereotype.Service;

import br.zul.gtautilapi.exception.UnexpectedException;

@Service
public class FileService {

    public String createTempFileByBase64(String content) {
        return createTempFileByBase64(content, null);
    }

    public String createTempFileByBase64(String content, String name) {
        try {
            var dir = Files.createTempDirectory("gtauil-api");
            if (name == null) {
                name = "file.ymap";
            }
            var file = new File(dir.toFile(), name).getAbsolutePath();
            var buffer = Base64.getDecoder().decode(content);
            Files.write(Path.of(file), buffer);
            return file;
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public String getFilename(String file) {
        return new File(file).getName();
    }

    public String readText(String file) {
        try {
            return Files.readString(Path.of(file));
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    public byte[] readAllBytes(InputStream is) {
        try {
            var stream = new ByteArrayOutputStream();
            var buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) != -1) {
                stream.write(buffer, 0, len);
            }
            return stream.toByteArray();
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

}
