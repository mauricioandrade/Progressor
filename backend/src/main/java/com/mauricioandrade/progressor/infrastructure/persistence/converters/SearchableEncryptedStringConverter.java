package com.mauricioandrade.progressor.infrastructure.persistence.converters;

import com.mauricioandrade.progressor.infrastructure.security.crypto.AesEncryptor;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Deterministic AES-256/GCM encryption for columns that are used in WHERE clauses (e.g. email).
 * Same plaintext always produces the same ciphertext, so {@code findByEmail(encrypt(email))} works.
 * Gracefully falls back to returning the raw value for legacy plaintext rows.
 */
@Converter
public class SearchableEncryptedStringConverter implements AttributeConverter<String, String> {

  @Override
  public String convertToDatabaseColumn(String attribute) {
    return AesEncryptor.encryptDeterministic(attribute);
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

  /** Convenience: encrypt {@code plaintext} for use as a query parameter. */
  public static String encrypt(String plaintext) {
    return AesEncryptor.encryptDeterministic(plaintext);
  }
}
