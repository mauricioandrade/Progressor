package com.mauricioandrade.progressor.infrastructure.security.crypto;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AES-256/GCM encryption utility.
 *
 * <p>Reads the 64-char hex key from the {@code ENCRYPTION_KEY} environment variable.
 * Falls back to an all-zero dev key if the variable is absent (logs a warning).
 *
 * <p>Two modes:
 * <ul>
 *   <li>{@code encryptString} / {@code encryptBytes} — random 12-byte nonce (non-deterministic,
 *       used for fields that are never queried by value: notes, feedback, photo data).</li>
 *   <li>{@code encryptDeterministic} — nonce derived as SHA-256(key ∥ plaintext)[0..11],
 *       so the same input always produces the same ciphertext. Used for email (must be
 *       queryable in {@code WHERE email = ?}).</li>
 * </ul>
 */
public final class AesEncryptor {

  private static final Logger log = LoggerFactory.getLogger(AesEncryptor.class);

  private static final int IV_LEN = 12;
  private static final int TAG_BITS = 128;
  private static final String ALGO = "AES/GCM/NoPadding";

  private static final byte[] KEY;
  private static final SecureRandom RANDOM = new SecureRandom();

  static {
    String hexKey = System.getenv("ENCRYPTION_KEY");
    if (hexKey == null || hexKey.isBlank()) {
      log.warn("[AesEncryptor] ENCRYPTION_KEY not set — using zero dev key. " +
          "Set ENCRYPTION_KEY=<64 hex chars> in production.");
      hexKey = "0000000000000000000000000000000000000000000000000000000000000000";
    }
    if (hexKey.length() != 64) {
      throw new IllegalStateException("ENCRYPTION_KEY must be exactly 64 hex characters (32 bytes).");
    }
    byte[] bytes = new byte[32];
    for (int i = 0; i < 32; i++) {
      bytes[i] = (byte) Integer.parseInt(hexKey.substring(i * 2, i * 2 + 2), 16);
    }
    KEY = bytes;
  }

  private AesEncryptor() {}

  // ── String helpers ────────────────────────────────────────────────────────

  /** Random-nonce encrypt → base64(iv[12] || ciphertext+tag). */
  public static String encryptString(String plaintext) {
    if (plaintext == null) return null;
    byte[] iv = randomIv();
    byte[] cipher = gcmEncrypt(plaintext.getBytes(StandardCharsets.UTF_8), iv);
    return Base64.getEncoder().encodeToString(concat(iv, cipher));
  }

  /** Deterministic encrypt — same input → same output. Safe for indexed/queried columns. */
  public static String encryptDeterministic(String plaintext) {
    if (plaintext == null) return null;
    byte[] iv = deterministicIv(plaintext);
    byte[] cipher = gcmEncrypt(plaintext.getBytes(StandardCharsets.UTF_8), iv);
    return Base64.getEncoder().encodeToString(concat(iv, cipher));
  }

  /**
   * Decrypt a string produced by either {@code encryptString} or {@code encryptDeterministic}.
   * Throws on failure — callers should catch for legacy plaintext fallback.
   */
  public static String decryptString(String encoded) {
    if (encoded == null) return null;
    byte[] combined = Base64.getDecoder().decode(encoded);
    byte[] iv = Arrays.copyOfRange(combined, 0, IV_LEN);
    byte[] cipher = Arrays.copyOfRange(combined, IV_LEN, combined.length);
    return new String(gcmDecrypt(cipher, iv), StandardCharsets.UTF_8);
  }

  // ── Byte-array helpers ────────────────────────────────────────────────────

  /** Random-nonce encrypt → iv[12] || ciphertext+tag. */
  public static byte[] encryptBytes(byte[] data) {
    if (data == null) return null;
    byte[] iv = randomIv();
    return concat(iv, gcmEncrypt(data, iv));
  }

  /**
   * Decrypt bytes produced by {@code encryptBytes}.
   * Throws on failure — callers should catch for legacy plaintext fallback.
   */
  public static byte[] decryptBytes(byte[] encrypted) {
    if (encrypted == null) return null;
    byte[] iv = Arrays.copyOfRange(encrypted, 0, IV_LEN);
    byte[] cipher = Arrays.copyOfRange(encrypted, IV_LEN, encrypted.length);
    return gcmDecrypt(cipher, iv);
  }

  // ── Internals ─────────────────────────────────────────────────────────────

  private static byte[] gcmEncrypt(byte[] data, byte[] iv) {
    try {
      Cipher c = Cipher.getInstance(ALGO);
      c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(KEY, "AES"), new GCMParameterSpec(TAG_BITS, iv));
      return c.doFinal(data);
    } catch (Exception e) {
      throw new RuntimeException("AES-GCM encryption failed", e);
    }
  }

  private static byte[] gcmDecrypt(byte[] data, byte[] iv) {
    try {
      Cipher c = Cipher.getInstance(ALGO);
      c.init(Cipher.DECRYPT_MODE, new SecretKeySpec(KEY, "AES"), new GCMParameterSpec(TAG_BITS, iv));
      return c.doFinal(data);
    } catch (Exception e) {
      throw new RuntimeException("AES-GCM decryption failed", e);
    }
  }

  private static byte[] randomIv() {
    byte[] iv = new byte[IV_LEN];
    RANDOM.nextBytes(iv);
    return iv;
  }

  private static byte[] deterministicIv(String plaintext) {
    try {
      MessageDigest sha = MessageDigest.getInstance("SHA-256");
      sha.update(KEY);
      sha.update(plaintext.getBytes(StandardCharsets.UTF_8));
      return Arrays.copyOfRange(sha.digest(), 0, IV_LEN);
    } catch (Exception e) {
      throw new RuntimeException("Deterministic IV derivation failed", e);
    }
  }

  private static byte[] concat(byte[] a, byte[] b) {
    byte[] r = new byte[a.length + b.length];
    System.arraycopy(a, 0, r, 0, a.length);
    System.arraycopy(b, 0, r, a.length, b.length);
    return r;
  }
}
