package com.tokirin.whereserver.models;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

	public String id;
	public String password;
	public Location residence;
	public Integer[] category;
	public Location[] favorite;
	
	//TODO: need to add these attribute in user DB
	public String mobileKey;
	public int rad;

}
