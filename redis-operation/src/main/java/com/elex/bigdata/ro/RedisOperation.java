package com.elex.bigdata.ro;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午2:39 Package: com.elex.bigdata.ro
 */
public interface RedisOperation {

  public String get(String key) throws RedisOperationException;

  public void set(String key, String value) throws RedisOperationException;

  public void set(String key, String value, int expire) throws RedisOperationException;

  public boolean exist(String key) throws RedisOperationException;

}
