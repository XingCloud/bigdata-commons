package com.elex.bigdata.hashing;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User: Z J Wu Date: 14-3-6 Time: 上午11:50 Package: com.elex.bigdata.hashing
 */
public class BDMD5 {
  private final String CHARSET = "utf8";
  private final String NAME = "MD5";

  private static final class INNER_HOLDER {
    private static final BDMD5 INSTANCE = new BDMD5();
  }

  public static BDMD5 getInstance() {
    return INNER_HOLDER.INSTANCE;
  }

  private BDMD5() {
  }

  public String toMD5(String s) throws HashingException {
    try {
      return _toMD5(s);
    } catch (Exception e) {
      throw new HashingException(e);
    }
  }

  private String _toMD5(String s) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    byte[] bytes = s.getBytes(CHARSET);
    MessageDigest md = MessageDigest.getInstance(NAME);
    StringBuilder sb = new StringBuilder();
    byte[] out = md.digest(bytes);
    for (int i = 0; i < out.length; ++i) {
      sb.append(Integer.toHexString((out[i] & 0xFF) | 0x100).substring(1, 3));
    }
    return sb.toString();
  }

  public static void main(String[] args) throws HashingException {
    System.out.println(BDMD5.getInstance().toMD5("a"));
  }
}
