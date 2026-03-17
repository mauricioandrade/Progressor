package com.mauricioandrade.progressor.infrastructure.persistence.converters;

import com.mauricioandrade.progressor.infrastructure.security.crypto.AesEncryptor;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * AES-256/GCM encryption for binary columns (progress photo bytea).
 * Gracefully falls back to returning raw bytes for legacy unencrypted rows
 * (GCM tag verification failure signals plaintext data).
 */
@Converter
public class EncryptedByteArrayConverter implements AttributeConverter<byte[], byte[]> {

  @Override
  public byte[] convertToDatabaseColumn(byte[] attribute) {
    return AesEncryptor.encryptBytes(attribute);
  }

  @Override
  public byte[] convertToEntityAttribute(byte[] dbData) {
    if (dbData == null) return null;
    try {
      return AesEncryptor.decryptBytes(dbData);
    } catch (Exception e) {
      return dbData; // legacy unencrypted photo — still served as-is
    }
  }
}
