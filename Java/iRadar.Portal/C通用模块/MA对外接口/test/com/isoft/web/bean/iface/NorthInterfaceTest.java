package com.isoft.web.bean.iface;

import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;

public class NorthInterfaceTest {
	
	public static String serverURI = "http://localhost:8080/imon/v1";
	public MultivaluedMap multiParams = new MultivaluedHashMap();
	public Client client = null;
	public WebTarget target = null;
	public Response response = null;
	
	@Before
	public void init(){
		getClient();
	}
	
	public void getClient(){
		client = ClientBuilder.newClient();
	}
	
	public WebTarget setTarget(String uri){
		target = client.target(serverURI + uri);
		return target;
	}
	
	public void setHeader(String param,Object value){
		multiParams.add(param, value);
	}
	
	public Response setResponse(){
		response = target.request().headers(multiParams).get();
		return response;
	}
	
	public Object getValue(Class valueClazz){
		return response.readEntity(valueClazz);
	}

	public void setParams(Map<String,Object> params){
		for(Entry<String,Object> e:params.entrySet()){
			multiParams.add(e.getKey(), e.getValue());
		}
	}
	
	@After
	public void destory(){
		response.close();
	}
	
}
