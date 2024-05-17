package br.zul.gtautilapi.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.zul.gtautilapi.exception.UnexpectedException;
import br.zul.gtautilapi.model.GtaUtilCommand;
import br.zul.gtautilapi.model.GtaUtilCommandResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GtaUtilService {

    private final FileService fileService;

    private String gtaVFolderCache;

    public GtaUtilCommandResponse run(GtaUtilCommand command) {
        try {
            var processBuilder = new ProcessBuilder(buildCommands(command));
            processBuilder.redirectInput();
            processBuilder.redirectOutput();
            var process = processBuilder.start();
            writeLn(process.getOutputStream(), getGTAVFolder());
            var text = readAll(process.getInputStream());
            return new GtaUtilCommandResponse(text);
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    private String readAll(InputStream is) {
        var builder = new StringBuilder();
        var buffer = new char[4096];
        int len;
        try (var isr = new InputStreamReader(is)) {
            while ((len = isr.read(buffer)) != -1) {
                builder.append(buffer, 0, len);
            }
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
        return builder.toString();
    }

    private void writeLn(OutputStream outputStream, String  str) {
        try {
            outputStream.write(str.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.flush();
        } catch (IOException ex) {
            throw new UnexpectedException(ex);
        }
    }

    private List<String> buildCommands(GtaUtilCommand command) {
        var result = new ArrayList<String>();
        result.add("gtautil\\GTAUtil.exe");
        result.add(command.name());
        result.addAll(command.params());
        return result;
    }

    private String getGTAVFolder() {
        if (gtaVFolderCache == null) {
            gtaVFolderCache = fileService.readText("gtautil/gta.txt").trim();
        }
        return gtaVFolderCache;
    }

}
