package com.mauricioandrade.progressor.infrastructure.security;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageValidator {

  private static final long MAX_SIZE = 5 * 1024 * 1024;

  private static final List<byte[]> MAGIC_BYTES = List.of(
      new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF},            // JPEG
      new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47},                    // PNG
      new byte[]{0x52, 0x49, 0x46, 0x46}                             // WebP (RIFF header)
  );

  public byte[] validateAndRead(MultipartFile file) throws Exception {
    if (file.getSize() > MAX_SIZE) {
      throw new IllegalArgumentException("Imagem muito grande. Máximo 5MB.");
    }
    byte[] bytes = file.getBytes();
    if (!hasSupportedMagicBytes(bytes)) {
      throw new IllegalArgumentException("Formato não suportado. Use JPEG, PNG ou WebP.");
    }
    return bytes;
  }

  private boolean hasSupportedMagicBytes(byte[] data) {
    for (byte[] magic : MAGIC_BYTES) {
      if (data.length >= magic.length && startsWith(data, magic)) {
        return true;
      }
    }
    return false;
  }

  private boolean startsWith(byte[] data, byte[] magic) {
    for (int i = 0; i < magic.length; i++) {
      if (data[i] != magic[i]) return false;
    }
    return true;
  }
}
