package com.elex.bigdata.util;


import com.elex.bigdata.conf.Config;
import org.apache.commons.configuration.Configuration;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Author: liqiang
 * 名字和字节的映射
 * Date: 14-4-1
 * Time: 下午3:21
 */
public class MetricMapping {

    private static final String DEFAULT_PROJECT_PATH = "/project.properties";
    public static final String DOMAIN_SEPARATOR = ".";

    private static final Map<String,Byte> projectMapping= new HashMap<String,Byte>();

    private static MetricMapping instance ;
    private static Pattern domain;

    private MetricMapping(String path){
        loadConfig(path,projectMapping);
    }

    public static synchronized MetricMapping getInstance(){
        if(instance == null){
            instance = new MetricMapping(null);
        }
        return instance;
    }

    public static synchronized MetricMapping getInstance(String customerPath){
        if(instance == null){
            instance = new MetricMapping(customerPath);
        }
        return instance;
    }

    public Byte getProjectURLByte(String projectName){
        Byte pid = projectMapping.get(projectName.toLowerCase());
        if(pid == null){
            int start = projectName.indexOf(DOMAIN_SEPARATOR);
            if(start > 0){
                int end = projectName.lastIndexOf(DOMAIN_SEPARATOR,start+1);
                if(end > 0 && start > end){
                    String domain = projectName.substring(start,end);
                    pid = projectMapping.get(domain.toLowerCase());
                }
            }

        }
        return pid;
    }

    private void loadConfig(String path,Map<String,Byte> mapping) {
        Configuration config = Config.createConfig(DEFAULT_PROJECT_PATH, path, Config.ConfigFormat.properties);
        Iterator<String> metrics = config.getKeys();
        while(metrics.hasNext()){
            String k = metrics.next();
            mapping.put(k.toLowerCase(),config.getByte(k));
        }
    }

    public static void main(String[] args){
//        System.out.println(MetricMapping.getProjectURLByte("www.lollygame.com"));
    }

}
