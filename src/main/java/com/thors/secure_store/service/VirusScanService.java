package com.thors.secure_store.service;

import com.thors.secure_store.config.ClamAVConfig;
import com.thors.secure_store.dto.others.VirusScanResult;
import com.thors.secure_store.exception.VirusScanFailedException;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Service
public class VirusScanService {

    private final ClamAVConfig clamAVConfig;
    private static final String INSTREAM_COMMAND = "zINSTREAM\0";


    public VirusScanService(ClamAVConfig clamAVConfig) {
        this.clamAVConfig = clamAVConfig;
    }

    public VirusScanResult scan(InputStream file) {
        try (Socket socket = new Socket()) {
            socket.connect(
                    new InetSocketAddress(clamAVConfig.getHost(),clamAVConfig.getPort()),
                    clamAVConfig.getConnectTimeoutMs()
            );
            socket.setSoTimeout(clamAVConfig.getReadTimeoutMs());

            OutputStream out = socket.getOutputStream();
            InputStream in = socket.getInputStream();

            out.write(INSTREAM_COMMAND.getBytes(StandardCharsets.US_ASCII));
            out.flush();

            byte[] buffer = new byte[2048];
            int bytesRead;

            while ((bytesRead = file.read(buffer)) != -1){
                byte[] size = ByteBuffer.allocate(4).putInt(bytesRead).array();
                out.write(size);
                out.write(buffer, 0, bytesRead);
            }

            out.write(new byte[]{0, 0, 0, 0});
            out.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String response = reader.readLine();

            boolean infected = response.contains("FOUND");
            String virusName = infected ? response.split("FOUND")[0].split(":")[1].trim() : null;

            return new VirusScanResult(infected, virusName, response);

        } catch (Exception e) {
            throw new VirusScanFailedException("Virus scan failed: " + e.getMessage(), e);
        }
    }
}
