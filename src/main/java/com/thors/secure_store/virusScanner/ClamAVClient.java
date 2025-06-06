package com.thors.secure_store.virusScanner;

import com.thors.secure_store.config.ClamAVConfig;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ClamAVClient {

  private ClamAVConfig clamAVConfig;

  public boolean scanFile(InputStream fileStream) throws IOException {
    try (Socket socket = new Socket(clamAVConfig.getHost(), clamAVConfig.getPort());
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream()) {

      out.write("zINSTREAM\0".getBytes());
      byte[] buffer = new byte[2048];
      int read;
      while ((read = fileStream.read(buffer)) >= 0) {
        out.write(ByteBuffer.allocate(4).putInt(read).array());
        out.write(buffer, 0, read);
      }
      out.write(new byte[] {0, 0, 0, 0});

      BufferedReader reader = new BufferedReader(new InputStreamReader(in));
      String response = reader.readLine();
      return response != null && response.contains("OK");
    }
  }
}
