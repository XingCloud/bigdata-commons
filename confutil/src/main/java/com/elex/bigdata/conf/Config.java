package com.elex.bigdata.conf;

import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CombinedConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.FileConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.plist.PropertyListConfiguration;
import org.apache.commons.configuration.tree.OverrideCombiner;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 * 提供配置文件。 比如 Configuration config = Config.createConfig("/hadoop-default.xml", "./conf/hadoop-site.xml",
 * ConfigFormat.xml)
 * <p/>
 * 这个config会用运行时文件系统上./conf/hadoop-site.xml 的内容为优先（如果有的话）； 如果找不到，则以classpath中 /hadoop-default.xml 的内容为默认。
 */
public class Config {

  private static final Logger LOGGER = Logger.getLogger(Config.class);

  private static HashMap<ConfigFormat, Constructor<? extends FileConfiguration>> constructors = null;

  /**
   * 根据参数创建一个config对象。 config配置项的寻找优先级为 类路径下配置文件 同时忽略加载默认配置
   *
   * @param classPathOverridePath 用户指定的classpath配置项作为输入文件.
   * @param format                配置文件的格式。支持xml, properties, proplist三种。
   * @return
   */
  public static Configuration createConfig(String classPathOverridePath, ConfigFormat format) {
    return createConfig(null, classPathOverridePath, format, true);
  }

  /**
   * 根据参数创建一个config对象。 config配置项的寻找优先级为 1. 类路径下配置文件 2. 默认配置文件
   *
   * @param classPathOverridePath 用户指定的classpath配置项作为输入文件.
   * @param format                配置文件的格式。支持xml, properties, proplist三种。
   * @return
   */
  public static Configuration createConfig(String defaultPath, String classPathOverridePath, ConfigFormat format) {
    return createConfig(defaultPath, classPathOverridePath, format, false);
  }

  /**
   * 根据参数创建一个config对象。 config配置项的寻找优先级为 1. 类路径下配置文件 2. 默认配置文件
   *
   * @param defaultPath           这个config默认配置文件的路径。默认配置文件需要在class path里面，比如说是打在jar包里面的。这个文件必须存在。
   * @param classPathOverridePath 这个config是用户指定的配置路径, 该文件必须位于classpath下, 运行时如果有会加载, 如放在tomcat的classes目录下. 这个文件可以不存在。
   * @param format                配置文件的格式。支持xml, properties, proplist三种。
   * @param ignoreDefault         是否忽略默认配置, 如果不忽略, 并且默认配置为空将报错
   * @return
   */
  private static Configuration createConfig(String defaultPath, String classPathOverridePath, ConfigFormat format,
                                            boolean ignoreDefault) {
    if (constructors == null) {
      try {
        initConstructors();
      } catch (NoSuchMethodException e) {
        throw new IllegalArgumentException("Cannot init class.", e);
      }
    }
    Constructor<? extends FileConfiguration> constructor = constructors.get(format);
    try {
      FileConfiguration defaultConfig = constructor.newInstance();
      InputStream defaultIS;
      if (!ignoreDefault) {
        if (defaultPath == null) {
          throw new NullPointerException("Default conf path must be assigned.");
        }
        defaultIS = Config.class.getResourceAsStream(defaultPath);
        if (defaultIS == null) {
          throw new NullPointerException("Default conf file does not exist(" + defaultPath + ").");
        }
        defaultConfig.load(defaultIS);
        LOGGER.info("Default conf file has been loaded(" + defaultPath + ").");
      }

      FileConfiguration classPathOverrideConfig = constructor.newInstance();
      if (classPathOverridePath != null) {
        defaultIS = Config.class.getResourceAsStream(classPathOverridePath);
        if (defaultIS != null) {
          classPathOverrideConfig.load(defaultIS);
          LOGGER.info("Classpath conf file has been loaded(" + classPathOverridePath + ").");
        }
      }

      CombinedConfiguration out = new CombinedConfiguration(new OverrideCombiner());
      out.addConfiguration((AbstractConfiguration) classPathOverrideConfig);
      out.addConfiguration((AbstractConfiguration) defaultConfig);
      return out;
    } catch (Exception e) {
      throw new IllegalArgumentException("Cannot read conf files.", e);
    }
  }

  private static void initConstructors() throws NoSuchMethodException {
    HashMap<ConfigFormat, Constructor<? extends FileConfiguration>> m = new HashMap<ConfigFormat, Constructor<? extends FileConfiguration>>();
    m.put(ConfigFormat.xml, XMLConfiguration.class.getConstructor());
    m.put(ConfigFormat.properties, PropertiesConfiguration.class.getConstructor());
    m.put(ConfigFormat.proplist, PropertyListConfiguration.class.getConstructor());
    constructors = m;
  }

  public static enum ConfigFormat {
    xml, properties, proplist
  }

}
