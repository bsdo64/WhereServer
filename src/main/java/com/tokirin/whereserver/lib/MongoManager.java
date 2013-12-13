package com.tokirin.whereserver.lib;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;

public class MongoManager {
	
	public static Mongo mongo; 
	public static DB db;
	
	public MongoManager(){
		try {
			mongo = new Mongo("localhost", 27017);
			db = mongo.getDB("where");
		} catch (UnknownHostException e) {
			System.out.println("Invalid Host name");
		}
	}
	

}
