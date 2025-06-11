package com.thors.secure_store.util;

import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

@Component
public class EncryptionUtils {

  private static final String ENCRYPTION_ALGO = "AES/CBC/PKCS5Padding";

  public SecretKey generateKey() throws NoSuchAlgorithmException {
    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
    keyGenerator.init(256); // 256-bit key
    return keyGenerator.generateKey();
  }

  public byte[] generateIV() {
    byte[] iv = new byte[16]; // 128-bit IV
    new SecureRandom().nextBytes(iv);
    return iv;
  }

  public byte[] encrypt(byte[] data, SecretKey key, byte[] iv)
      throws NoSuchPaddingException,
          NoSuchAlgorithmException,
          InvalidAlgorithmParameterException,
          InvalidKeyException,
          IllegalBlockSizeException,
          BadPaddingException {
    Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGO);
    IvParameterSpec ivSpec = new IvParameterSpec(iv);
    cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
    return cipher.doFinal(data);
  }

  public byte[] decrypt(byte[] data, byte[] encodedKey, byte[] iv)
      throws NoSuchPaddingException,
          NoSuchAlgorithmException,
          InvalidAlgorithmParameterException,
          InvalidKeyException,
          IllegalBlockSizeException,
          BadPaddingException {

    SecretKey key = new SecretKeySpec(encodedKey, "AES");

    Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGO);
    IvParameterSpec ivSpec = new IvParameterSpec(iv);
    cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
    return cipher.doFinal(data);
  }
}
