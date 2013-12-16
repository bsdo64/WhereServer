package com.tokirin.whereserver.modules;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.vertx.java.core.Vertx;
import org.vertx.java.core.json.JsonObject;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoException;
import com.tokirin.whereserver.lib.MongoManager;
import com.tokirin.whereserver.models.Global;
import com.tokirin.whereserver.models.Question;

public class WhereMakeChat {
	public String owner;
	public Question question;
	final String roomHash = UUID.randomUUID().toString();
	public int qRad;
	ArrayList<String> gcmKeys = new ArrayList<String>();
	
	public WhereMakeChat(String id, JsonObject json) {
		this.question = new Gson().fromJson(json.toString(), Question.class);
		this.owner = id;
		this.qRad = question.rad;
		makeRoom();
		try {
			pushAllUsers();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getHash(){
		return roomHash;
	}
	
	public int makeRoom(){
		MongoManager mongo = new MongoManager();
		DBCollection coll = mongo.db.getCollection("Rooms");
		
		BasicDBObject room = new BasicDBObject();
		
		room.put("owner", owner);
		room.put("question",question.query);
		room.put("category", question.category);
		room.put("hash", roomHash);
		room.put("isFinish", false);
		room.put("rad",question.rad);		
		room.put("time",question.time);
		
		try{
			coll.insert(room);
			System.out.println("Query Success");
			return 200;
		}catch(MongoException e){
			System.out.println("Query Failed");
			return 500;
		}
		
	}

	public int matchUsers(){
		System.out.println("Process for Matching Users");
		int i = 0;
		MongoManager mongo = new MongoManager();
		DBCollection coll = mongo.db.getCollection("Users");
		DBCursor cur = coll.find();
		
		while(cur.hasNext()){
			BasicDBObject row = (BasicDBObject) cur.next();
			
			if(row.getString("id").equals(owner)){
				break;
			}
			
			BasicDBObject residence = (BasicDBObject) row.get("residence");
			String mobileKey = row.getString("mobileKey");
			int aRad = row.getInt("rad");
			ArrayList<BasicDBObject> favorite = (ArrayList<BasicDBObject>)row.get("favorite");
			
			double qLat = Double.valueOf(question.loc.latitude);
			double qLng = Double.valueOf(question.loc.longitude);
			
			double aLat = Double.valueOf(residence.getString("latitude"));
			double aLng = Double.valueOf(residence.getString("longitude"));
			
			double res = calDistance(qLat, qLng, aLat, aLng);
			
			if((double)(aRad+qRad) >= res){
				System.out.println("Residence Match!");
				System.out.println(mobileKey);
				gcmKeys.add(mobileKey);
				i++;
			}else{
				for(BasicDBObject obj : favorite){
					
					aLat = Double.valueOf(obj.getString("latitude"));
					aLng = Double.valueOf(obj.getString("longitude"));
					res = Global.calDistance(qLat, qLng, aLat, aLng);
					if((double)(aRad+qRad) <= res){
						System.out.println("Favorite Match!");
						System.out.println(mobileKey);
						gcmKeys.add(mobileKey);
						i++;
					}
				}
			}
		}
		
		return i;
	}
	
	public void pushAllUsers() throws IOException{
		
		System.out.println("1");
		MongoManager mongo = new MongoManager();
		DBCollection coll = mongo.db.getCollection("Users");
		DBCursor cur = coll.find();
		
		while(cur.hasNext()){
			System.out.println("2");
			BasicDBObject row = (BasicDBObject) cur.next();
			if(row.getString("id").equals(owner)){
				System.out.println("3");
				break;
			}
			System.out.println("4");
			String mobileKey = row.getString("mobileKey");
			gcmKeys.add(mobileKey);
		}
		
		System.out.println("5");
		Sender sender = new Sender("AIzaSyBc0MGxr2DyexObUMWQWOCYNSiosdUsIrs");
		System.out.println("6");
		Message msg = new Message.Builder()
							.addData("event","WhereQuestionPush")
							.addData("channel", roomHash)
							.addData("data", question.query)
							.build();
		
		for(String gcmkey:gcmKeys){
			System.out.println("7");
			//TODO:implement push module
			System.out.println(gcmkey);
			Result result = sender.send(msg, gcmkey,5);
			if(result!=null){
				System.out.println("8");
				System.out.println("push success");
			}else{
				String error = result.getErrorCodeName();
				System.out.println(error);
			}
		}
	}
	
	public void pushUsers(){
		
		int count = matchUsers();
		
		if(count == 0){	
			System.out.println("there is no matched person");
			try {
				pushAllUsers();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			Sender sender = new Sender("AIzaSyBc0MGxr2DyexObUMWQWOCYNSiosdUsIrs");
			Message msg = new Message.Builder()
								.addData("event","WhereQuestionPush")
								.addData("channel", roomHash)
								.addData("data", question.query)
								.build();
			for(String gcmkey:gcmKeys){
				//TODO:implement push module
				System.out.println(gcmkey);
				Result result;
				try {
					result = sender.send(msg, gcmkey,5);
					if(result!=null){
						System.out.println("push success");
					}else{
						String error = result.getErrorCodeName();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}

	public double calDistance(double lat1,double lng1, double lat2,double lng2){
		
		double EarthR=6371000.0;
		double R = Math.PI/180;
		double radLat1 = R*lat1;
		double radLat2 = R*lat2;
		double radDist = R*(lng1-lng2);
		
		double distance = Math.sin(radLat1) * Math.sin(radLat2);
		
		distance = distance + Math.cos(radLat1) * Math.cos(radLat2) * Math.cos(radDist);
		double ret = EarthR * Math.acos(distance);
		System.out.println(ret);
		return ret;
		
	}

}

