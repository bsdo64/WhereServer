package com.tokirin.whereserver.modules;

import java.util.ArrayList;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;
import com.tokirin.whereserver.lib.MongoManager;
import com.tokirin.whereserver.models.Location;
import com.tokirin.whereserver.models.User;

public class WhereJoin {
	
	public String id;
	public String password;
	public long residence_latitude;
	public long residence_longitude;
	public JsonArray category;
	public JsonArray favorite;
	
	
	public WhereJoin(JsonObject info){
		
		id = info.getString("id");
		password = info.getString("password");
		JsonObject residence = new JsonObject(info.getString("residence"));
		residence_latitude = residence.getLong("latitude", (long) 200.00);
		residence_longitude = residence.getLong("longitude",(long) 200.00);
		category = info.getArray("category");
		favorite=info.getArray("favorite");
		
		
	}
	
	public int join(){
		MongoManager mongo = new MongoManager();
		DBCollection coll = mongo.db.getCollection("Users");
		ArrayList favorite = new ArrayList();
		ArrayList category = new ArrayList();
		BasicDBObject query = new BasicDBObject();
		query.put("id",id);
		
		if(coll.find(query).hasNext()){
			System.out.println("Where Error :: " + id + " is duplicated id value");
			return 403;
		}
		
		BasicDBObject info = new BasicDBObject();
		BasicDBObject residence = new BasicDBObject();
		residence.put("latitude", residence_latitude);
		residence.put("longitude", residence_longitude);
		
		info.put("id", id);
		info.put("password", password);
		info.put("residence", residence);
		/*
		for(JsonObject loc : favor){
			BasicDBObject temp = new BasicDBObject();
			temp.put("latitude", loc.getString("latitude"));
			temp.put("longitude", loc.getString("longitude"));
			favorite.add(temp);
		}
		
		info.put("favorite", favorite);
		*/

		for(int i =0; i<this.favorite.size();i++){
			
			JsonObject temp = new JsonObject(this.favorite.get(i).toString());
			BasicDBObject temp2 = new BasicDBObject();
			temp2.put("latitude", temp.getLong("latitude"));
			temp2.put("longitude", temp.getLong("longitude"));
			favorite.add(temp2);
			
		}
		
		info.put("favorite", favorite);
		
		for(int i =0; i<this.category.size();i++){
			category.add(Integer.parseInt(this.category.get(i).toString()));
		}
		
		info.put("category", category);
		
		try{
			coll.insert(info);
			System.out.println("Join Success");
			return 200;
		}catch(MongoException e){
			System.out.println("Join Failed");
			return 500;
		}
	}
}
