package com.tokirin.whereserver.verticle;

import org.vertx.java.core.Handler;
import org.vertx.java.core.VoidHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.RouteMatcher;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.Verticle;

import com.tokirin.whereserver.models.User;
import com.tokirin.whereserver.modules.WhereJoin;
import com.tokirin.whereserver.modules.WhereLogin;

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
						String params = body.getString(0,body.length(),"UTF-8");
						String[] temp = params.split("&");
						String[] ids = temp[0].split("=");
						String[] pwds = temp[1].split("=");
						String id = ids[1];
						String pwd = pwds[1];

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
		
		return rm;
		
		
	}
	//Deploy Where Modules
	public void deployModules(){
		
		
	}
	
	public void checkLogin(){
		
		
	}

}
