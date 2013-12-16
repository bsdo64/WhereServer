package com.tokirin.whereserver.modules;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.tokirin.whereserver.lib.MongoManager;

public class WhereGetRooms {

	public WhereGetRooms(){
		
	}
	
	public static String get(){
		MongoManager m = new MongoManager();
		DBCollection coll = m.db.getCollection("Rooms");
		DBCursor cur = coll.find();
		JsonArray response = new JsonArray();
		while(cur.hasNext()){
			BasicDBObject obj = (BasicDBObject) cur.next();
			String owner = (String) obj.get("owner");
			String hash = (String) obj.get("hash");
			String question = (String) obj.get("question");
			String time = (String) obj.get("time");
			
			JsonObject json = new JsonObject();
			json.putString("owner", owner);
			json.putString("hash", hash);
			json.putString("question", question);
			json.putString("time", time);
			
			response.addObject(json);
			
		}
		return response.toString();
		
		
		
	}
}
