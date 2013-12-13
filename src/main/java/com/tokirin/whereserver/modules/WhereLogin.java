package com.tokirin.whereserver.modules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.tokirin.whereserver.lib.MongoManager;

public class WhereLogin {
	private String id;
	private String pwd;
	private String userInfo = "";
	
	public WhereLogin(String id, String pwd){
		
		this.id = id;
		this.pwd = pwd;		
	}
	
	public int authenticate(){
		
		MongoManager m = new MongoManager();
		DBCollection coll = m.db.getCollection("Users");
		BasicDBObject query = new BasicDBObject();
		query.put("id", id);
		DBCursor cur = coll.find(query);
		
		if(!cur.hasNext()){
			System.out.println("There is no Such ID");
			return 404;
		}else{
			DBObject obj = cur.next();
			System.out.println("Authenticating");
			String _pwd = obj.get("password").toString();
			if(pwd.equals(_pwd)){
				System.out.println("Login Success");
				System.out.println(obj.toString());
				userInfo = obj.toString();
				return 200;
			}else{
				System.out.println("Invalid Password");
				return 401;
			}
		}
		
	}
	
	public String getInfo(){
		
		return userInfo;
	}

}
