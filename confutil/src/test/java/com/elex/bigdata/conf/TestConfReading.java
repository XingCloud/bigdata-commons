package com.elex.bigdata.conf;

import org.apache.commons.configuration.Configuration;
import org.junit.Test;

/**
 * User: Z J Wu Date: 14-2-27 Time: 下午2:25 Package: com.elex.bigdata.conf
 */
public class TestConfReading {

  @Test
  public void testConfReading() {
    Configuration configuration = Config.createConfig("/conf.site.properties", Config.ConfigFormat.properties);
    System.out.println(configuration.getString("a"));

    System.out.println("---------------------------------");
    Configuration configuration2 = Config
      .createConfig("/conf.default.properties", "/conf.site.properties", Config.ConfigFormat.properties);
    System.out.println(configuration2.getString("a"));
    System.out.println(configuration2.getString("b"));
    System.out.println(configuration2.getString("c"));
  }
}
