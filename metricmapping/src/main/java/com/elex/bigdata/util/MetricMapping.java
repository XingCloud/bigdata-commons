package com.elex.bigdata.util;


import com.elex.bigdata.conf.Config;
import com.elex.bigdata.driver.MongoDriver;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.configuration.Configuration;

import java.util.*;

/**
 * Author: liqiang
 * 名字和字节的映射
 * Date: 14-4-1
 * Time: 下午3:21
 */
public class MetricMapping {

    private static final String DEFAULT_PROJECT_PATH = "/project.properties";
    private static final String DOMAIN_SEPARATOR = ".";

    private static final Map<String,Byte> projectMapping= new HashMap<String,Byte>();
    private static final Set<Byte> projects = new HashSet<Byte>();

    private static MetricMapping instance ;

    private MetricMapping(String path){
        //注意： 目前只用一个byte表示项目，范围为-127 +127，配置的时候得注意
        loadConfig(path);
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
                int end = projectName.lastIndexOf(DOMAIN_SEPARATOR);
                if(start == end){
                    String domain = projectName.substring(0,start);
                    pid = projectMapping.get(domain.toLowerCase());
                }else{
                    String domain = projectName.substring(start+1,end);
                    pid = projectMapping.get(domain.toLowerCase());
                }
            }

        }
        return pid;
    }

    /**
     * 人肉将所有projectname转换为短的projectname
     * @return
     */
    public Map<String,Byte> getAllProjectShortNameMapping(){
        Map<String,Byte> shortMapping = new HashMap<String, Byte>();
        for(Map.Entry<String,Byte> pm : projectMapping.entrySet()){
            String pname = pm.getKey();

            int start = pname.indexOf(DOMAIN_SEPARATOR);
            int end = pname.lastIndexOf(DOMAIN_SEPARATOR);

            String shortName;
            if(start == -1){
                shortName = pname; //eg: 22find
            }else if(start == end){
                shortName = pname.substring(0,start); //eg: v9.com
            }else{
                shortName = pname.substring(start+1,end); //eg: www.22find.com
            }

            shortMapping.put(shortName,pm.getValue());
        }
        return shortMapping;
    }

    public Set<Byte> getAllProjectByteValue(){
        return projects;
    }

    private void loadConfig(String path) {
        Configuration config = Config.createConfig(DEFAULT_PROJECT_PATH, path, Config.ConfigFormat.properties);
        Iterator<String> metrics = config.getKeys();
        while(metrics.hasNext()){
            String k = metrics.next();
            Byte v = config.getByte(k);
            projectMapping.put(k.toLowerCase(),v);
            projects.add(v);
        }
    }

    //获取nation的工具方法
    public static Set<String> getNationsByProjectID(Byte pbid){
        DBObject queryObject = new BasicDBObject();

        if(pbid != null){
            queryObject.put("pbid",pbid.intValue());
        }
        DBCollection nationColl = MongoDriver.getNationCollection();
        DBCursor dc = nationColl.find(queryObject);
        Set<String> nations = new HashSet<String>();
        if(dc.size() >0){
            while(dc.hasNext()){
                Object nation = dc.next().get("nation");
                if(nation != null){
                    nations.add(nation.toString());
                }
            }
        }

        return nations;
    }

    public static Set<String> getAllNationsAsSet(){ //组合成set方便判断某个nation是否存在
        DBCollection nationColl = MongoDriver.getNationCollection();
        DBCursor dc = nationColl.find();
        Set<String> nations = new HashSet<String>();
        if(dc.size() >0){
            while(dc.hasNext()){
                DBObject next = dc.next();
                String nation = next.get("nation").toString();
                String pbid = next.get("pbid").toString();
                nations.add(pbid.concat("_").concat(nation));
            }
        }
        return nations;
    }

    public static void insertNations(Set<String> nations){
        DBCollection nationColl = MongoDriver.getNationCollection();
        List<DBObject> dbObjs = new ArrayList<DBObject>();
        DBObject insertObject;
        for(String nation : nations){
            insertObject = new BasicDBObject();
            int pos = nation.indexOf("_");
            insertObject.put("pbid",Integer.valueOf(nation.substring(0,pos)));
            insertObject.put("nation",nation.substring(pos+1).toUpperCase());
            dbObjs.add(insertObject);
        }
        nationColl.insert(dbObjs);
    }

    public static void main(String[] args){
//        System.out.println(MetricMapping.getProjectURLByte("www.lollygame.com"));

        System.out.println(MetricMapping.getInstance().getAllProjectShortNameMapping());
    }

}
