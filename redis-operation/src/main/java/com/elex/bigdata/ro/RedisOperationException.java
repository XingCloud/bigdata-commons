package com.elex.bigdata.ro;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午2:40 Package: com.elex.bigdata.ro
 */
public class RedisOperationException extends Exception {
  public RedisOperationException() {
  }

  public RedisOperationException(String message) {
    super(message);
  }

  public RedisOperationException(String message, Throwable cause) {
    super(message, cause);
  }

  public RedisOperationException(Throwable cause) {
    super(cause);
  }
}
