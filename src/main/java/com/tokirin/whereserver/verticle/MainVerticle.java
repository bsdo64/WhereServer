package com.tokirin.whereserver.verticle;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.tokirin.whereserver.models.User;
import com.tokirin.whereserver.modules.WhereGetRooms;
import com.tokirin.whereserver.modules.WhereJoin;
import com.tokirin.whereserver.modules.WhereLogin;
import com.tokirin.whereserver.modules.WhereMakeChat;

public class MainVerticle extends Verticle {

	@Override
	public void start() {
		final int PORT = 8920;
		HttpServer server = vertx.createHttpServer();
		server.requestHandler(makeRoute()).listen(PORT);
		System.out.println("Where Server is deployed on PORT " + PORT);
		
	}
	
	public RouteMatcher makeRoute(){
		RouteMatcher rm = new RouteMatcher();
		
		rm.post("/login", new Handler<HttpServerRequest>(){
			@Override
			public void handle(final HttpServerRequest req) {

				final Buffer body = new Buffer(0);
				req.dataHandler(new Handler<Buffer>(){
					@Override
					public void handle(Buffer bf) {
						body.appendBuffer(bf);
					}
				});
				
				req.endHandler(new VoidHandler(){

					@Override
					protected void handle() {
						System.out.println("Login Request is Arrived!");
						System.out.println(req.uri());
                        System.out.println(body.getString(0,body.length(),"UTF-8"));
                        JsonObject json = new JsonObject(body.getString(0,body.length()));

                        String id = json.getString("id");
                        String pwd = json.getString("pwd");
						System.out.println("ID is " + id);
						WhereLogin wl = new WhereLogin(id,pwd);
						req.response().setStatusCode(wl.authenticate()).setStatusMessage(wl.getInfo()).end();
					}
					
				});
			}
		});
		
		
		rm.post("/join",new Handler<HttpServerRequest>(){

			@Override
			public void handle(final HttpServerRequest req) {
				
				final Buffer body = new Buffer(0);
				req.dataHandler(new Handler<Buffer>(){
					@Override
					public void handle(Buffer bf) {
						body.appendBuffer(bf);
					}
				});
				
				req.endHandler(new VoidHandler(){
					@Override
					protected void handle() {
						System.out.println("Join Request is Arrived!");
						System.out.println(body.getString(0,body.length(),"UTF-8"));
						JsonObject json = new JsonObject(body.getString(0,body.length()));
						WhereJoin join = new WhereJoin(json);
						req.response().setStatusCode(join.join()).end();
					}
					
				});
				
			}
			
		});
		
		rm.post("/:userId/query", new Handler<HttpServerRequest>(){

			@Override
			public void handle(final HttpServerRequest req) {

				final Buffer body = new Buffer(0);
				req.dataHandler(new Handler<Buffer>(){
					@Override
					public void handle(Buffer bf) {
						body.appendBuffer(bf);
					}
				});
				
				req.endHandler(new VoidHandler(){
					@Override
					protected void handle() {
						System.out.println(body.getString(0,body.length()));
						JsonObject json = new JsonObject(body.getString(0,body.length()));
						String id = req.params().get("userId");
						System.out.println(id);
						System.out.println(json.toString());

						WhereMakeChat room = new WhereMakeChat(id,json);

						Date now = new Date();
						SimpleDateFormat fm = new SimpleDateFormat("yy-MM-dd a hh:mm:ss");
						
						JsonObject response = new JsonObject();
						response.putString("owner",room.owner);
						response.putString("hash", room.getHash());
						System.out.println(room.getHash());
						response.putString("question", room.question.query);
						response.putString("time", room.question.time);
						req.response().setChunked(true).write(response.toString(), "UTF-8").setStatusCode(200).end();
					}
				});
				
			}
			
			
		});
		
		rm.get("/getRooms", new Handler<HttpServerRequest>(){

			@Override
			public void handle(HttpServerRequest req) {
				String res = WhereGetRooms.get();
				System.out.println("GetAnswerList");
				System.out.println(res);
				req.response().setChunked(true).write(res, "UTF-8").setStatusCode(200).end();
			}
			
			
		});
		
		return rm;
		
		
	}
	//Deploy Where Modules
	public void deployModules(){
		
		
	}
	
	public void checkLogin(){
		
		
	}

}
