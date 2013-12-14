package com.tokirin.whereserver.modules;

import java.util.ArrayList;

import com.google.gson.Gson;
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
	
	User user;
	
	
	public WhereJoin(JsonObject info){
		user = new Gson().fromJson(info.toString(),User.class);
	}
	
	public int join(){
		MongoManager mongo = new MongoManager();
		DBCollection coll = mongo.db.getCollection("Users");
		ArrayList favorite = new ArrayList();
		ArrayList category = new ArrayList();
		BasicDBObject query = new BasicDBObject();
		query.put("id",user.id);
		
		if(coll.find(query).hasNext()){
			System.out.println("Where Error :: " + user.id + " is duplicated id value");
			return 403;
		}
		
		BasicDBObject info = new BasicDBObject();
		BasicDBObject residence = new BasicDBObject();
		residence.put("latitude", user.residence.latitude);
		residence.put("longitude", user.residence.longitude);
		
		info.put("id", user.id);
		info.put("password", user.password);
		info.put("residence", residence);

		for(int i =0; i<user.favorite.length;i++){
			BasicDBObject temp = new BasicDBObject();
			temp.put("latitude", user.favorite[i].latitude);
			temp.put("longitude", user.favorite[i].longitude);
			favorite.add(temp);
			
		}

		info.put("favorite", favorite);
		
		for(int i =0; i<user.category.length;i++){
			category.add(user.category[i]);
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
