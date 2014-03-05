package com.elex.bigdata.ro;

import com.elex.bigdata.conf.Config;
import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午2:34 Package: com.elex.bigdata.ro
 */
public class BasicRedisShardedPoolManager {
  private static final Logger LOGGER = Logger.getLogger(BasicRedisShardedPoolManager.class);
  protected String id;
  protected ShardedJedisPool pool;

  public BasicRedisShardedPoolManager(String id) {
    this.id = id;
  }

  public BasicRedisShardedPoolManager(String id,String classPathConf){
    this.id=id;
    this.init("/redis.default.properties", classPathConf);
  }

  protected void init(String defaultConfFile, String classpathFile) {
    if (pool != null) {
      return;
    }
    Configuration config = Config.createConfig(defaultConfFile, classpathFile, Config.ConfigFormat.properties);

    int maxActive = config.getInt("max_active", 4096);
    int maxIdle = config.getInt("max_idle", 1024);
    int timeout = config.getInt("timeout", 300000);
    int maxWait = config.getInt("max_wait", 600000);

    LOGGER.info("[REDIS-INIT] - Max active - " + maxActive);
    LOGGER.info("[REDIS-INIT] - Max idle - " + maxIdle);
    LOGGER.info("[REDIS-INIT] - Timeout - " + timeout);
    LOGGER.info("[REDIS-INIT] - Max wait - " + maxWait);

    String urls = config.getString("urls");
    String[] urlArray = urls.split("#");
    String[] urlParam;
    List<JedisShardInfo> shardList = new ArrayList<JedisShardInfo>(urlArray.length);
    String host;
    int port;
    JedisShardInfo shard;
    for (String url : urlArray) {
      urlParam = url.split(":");
      host = urlParam[0];
      port = Integer.parseInt(urlParam[1]);
      shard = new JedisShardInfo(host, port, timeout);
      shardList.add(shard);
      LOGGER.info("[REDIS-INIT] - Add redis shard - " + host + ":" + port);
    }

    JedisPoolConfig poolConfig = new JedisPoolConfig();
    poolConfig.setMaxActive(maxActive);
    poolConfig.setMaxIdle(maxIdle);
    poolConfig.setMaxWait(maxWait);
    poolConfig.setTestOnBorrow(true);

    pool = new ShardedJedisPool(poolConfig, shardList);

    LOGGER.info("[REDIS-INIT] - Manager(" + this.id + ") init finished.");
  }

  public ShardedJedis borrowShardedJedis() {
    return pool.getResource();
  }

  public void returnShardedJedis(ShardedJedis shardedJedis) {
    if (shardedJedis == null) {
      LOGGER.info("[REDIS-INIT] - Empty sharded jedis, ignore return.");
      return;
    }
    pool.returnResource(shardedJedis);
  }

  public void returnBrokenShardedJedis(ShardedJedis shardedJedis) {
    if (shardedJedis == null) {
      LOGGER.info("[REDIS-INIT] - Empty sharded jedis, ignore return broken.");
      return;
    }
    pool.returnBrokenResource(shardedJedis);
  }
}
