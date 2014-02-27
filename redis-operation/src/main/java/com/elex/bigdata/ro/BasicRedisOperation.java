package com.elex.bigdata.ro;

import redis.clients.jedis.ShardedJedis;

import java.util.UUID;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午2:43 Package: com.elex.bigdata.ro
 */
public class BasicRedisOperation implements RedisOperation {

  private final BasicRedisShardedPoolManager manager;

  public BasicRedisOperation(String classPathConf) {
    manager = new BasicRedisShardedPoolManager(UUID.randomUUID().toString());
    manager.init("/redis.default.properties", classPathConf);
  }

  @Override
  public String get(String key) throws RedisOperationException {
    boolean successful = true;
    ShardedJedis shardedJedis = null;
    try {
      shardedJedis = manager.borrowShardedJedis();
      return shardedJedis.get(key);
    } catch (Exception e) {
      successful = false;
      throw new RedisOperationException(e);
    } finally {
      if (successful) {
        manager.returnShardedJedis(shardedJedis);
      } else {
        manager.returnBrokenShardedJedis(shardedJedis);
      }
    }
  }

  @Override
  public void set(String key, String value) throws RedisOperationException {
    boolean successful = true;
    ShardedJedis shardedJedis = null;
    try {
      shardedJedis = manager.borrowShardedJedis();
      shardedJedis.set(key, value);
    } catch (Exception e) {
      successful = false;
      throw new RedisOperationException(e);
    } finally {
      if (successful) {
        manager.returnShardedJedis(shardedJedis);
      } else {
        manager.returnBrokenShardedJedis(shardedJedis);
      }
    }
  }

  @Override
  public boolean exist(String key) throws RedisOperationException {
    boolean successful = true;
    ShardedJedis shardedJedis = null;
    try {
      shardedJedis = manager.borrowShardedJedis();
      return shardedJedis.exists(key);
    } catch (Exception e) {
      successful = false;
      throw new RedisOperationException(e);
    } finally {
      if (successful) {
        manager.returnShardedJedis(shardedJedis);
      } else {
        manager.returnBrokenShardedJedis(shardedJedis);
      }
    }
  }
}
