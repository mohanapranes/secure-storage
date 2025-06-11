package com.thors.secure_store.util;

import java.security.SecureRandom;
import java.util.Base64;

public class SecureRandomTokenGenerator {

  SecureRandomTokenGenerator() {}

  public static String generate(int byteLength) {
    byte[] bytes = new byte[byteLength];
    new SecureRandom().nextBytes(bytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
