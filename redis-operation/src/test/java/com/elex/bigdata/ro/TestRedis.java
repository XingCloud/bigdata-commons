package com.elex.bigdata.ro;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午3:24 Package: com.elex.bigdata.ro
 */
public class TestRedis {

  @Test
  public void test1() throws RedisOperationException, InterruptedException {
    final BasicRedisOperation bro = new BasicRedisOperation("/redis.site.properties");

    Thread t;
    int threadCount = 1000;
    final CountDownLatch latch = new CountDownLatch(threadCount);
    for (int i = 0; i < threadCount; i++) {
      t = new Thread(new Runnable() {
        @Override
        public void run() {
          int c = 0, cnt = 100;
          while (c < cnt) {
            try {
              System.out.println(bro.get("a"));
              ++c;
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
          latch.countDown();
        }
      });
      t.start();
    }
    latch.await();
  }
}
