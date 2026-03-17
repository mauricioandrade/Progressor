package com.mauricioandrade.progressor.infrastructure.persistence.converters;

import com.mauricioandrade.progressor.infrastructure.security.crypto.AesEncryptor;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Random-nonce AES-256/GCM encryption for non-searchable text columns
 * (student notes, professional feedback, etc.).
 * Gracefully falls back to returning the raw value for legacy plaintext rows.
 */
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

  @Override
  public String convertToDatabaseColumn(String attribute) {
    return AesEncryptor.encryptString(attribute);
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    if (dbData == null) return null;
    try {
      return AesEncryptor.decryptString(dbData);
    } catch (Exception e) {
      return dbData; // legacy plaintext row — still readable
    }
  }
}
