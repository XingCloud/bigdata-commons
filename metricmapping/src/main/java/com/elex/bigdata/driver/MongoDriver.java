package com.elex.bigdata.driver;

import com.mongodb.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

/**
 * Author: liqiang
 * Date: 14-3-28
 * Time: 下午7:24
 */
public class MongoDriver {

    public static final Log LOG = LogFactory.getLog(MongoDriver.class);
    private Mongo mongo;
    private DB db;
    public DBCollection nationColl;
    private static final String mongodbHost = "mongodb";
    private static final int mongodbPort = 27017;
    private static final String mongodbDBName = "bigdata";
    private static final String nationCollectionName = "nation";

    public MongoDriver() {
        try {
            mongo = new Mongo(mongodbHost, mongodbPort);
            db = mongo.getDB(mongodbDBName);
            nationColl = db.getCollection(nationCollectionName);

            LOG.info("connect to " + mongodbHost + " " + mongodbPort + " " + mongodbDBName
                    + " " + nationCollectionName);

        } catch (Exception e) {
            LOG.error("mongodb init error", e);
        }
    }


    static private MongoDriver instance;

    static public MongoDriver getInstance() {
        if (instance == null) {
            instance = new MongoDriver();
        }
        return instance;
    }

    public List<String> getNationsByProjectName(){
        return null;
    }

    static public DBCollection getNationCollection() {
        try {
            return getInstance().nationColl;
        } catch (Exception e) {
            LOG.error("get collection catch Exception", e);
        }
        return null;
    }

    static public DB getInstanceDB() {
        if (instance == null) {
            instance = new MongoDriver();
        }
        return instance.db;
    }

    static public Mongo getInstanceMongo() {
        if (instance == null) {
            instance = new MongoDriver();
        }
        return instance.mongo;
    }

/*    static public DBObject getADDetail(String id) throws Exception{
        //get from local
        DBObject queryObject = new BasicDBObject();
        queryObject.put(OBJ_ID,String.valueOf(id));
        DBCursor dc = getADCollection().find(queryObject);
        if(dc.size() >0){
            if(dc.hasNext()){
                return dc.next();
            }
        }

        try {
            String json = getRemoteAD(id);
            if(json == null){
                return null;
            }
            BasicBSONList bsonList = (BasicBSONList) JSON.parse(json);

            DBObject bson = (DBObject) bsonList.get(0);
            bson.put(OBJ_ID,bson.get("id"));
            bson.put(createdAt,new Date());
            MongoDriver.getADCollection().insert(bson);
            return bson;

        } catch (Exception e) {
            throw new Exception("Fail to get the AD detail for aid("+id+")",e);
        }

    }*/
/*
    static private String getRemoteAD(String id) throws IOException, InterruptedException {
        int tryNum = 5;

        for (int i=0; i< 5 ; i++) {
            try{
                HttpClient httpClient = new HttpClient();
                String url = "http://admin.adplus.goo.mx/api/getitems?id=" + id;
                GetMethod get = new GetMethod(url);
                get.releaseConnection();

                httpClient.executeMethod(get);

                return get.getResponseBodyAsString();
            }catch (IOException e){
                if( i < tryNum -1 ){
                    LOG.info("Try "+ (i+1) + " times to get the ad as get exception [" + e.getMessage() + "]");
                    Thread.sleep(ConnectionUtils.getPauseTime(1000, i));
                }else{
                    throw e;
                }
            }
        }
        return null;
    }*/
}
