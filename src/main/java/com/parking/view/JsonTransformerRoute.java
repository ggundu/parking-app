/**
 * 
 */
package com.parking.view;

import com.google.gson.Gson;

import spark.ResponseTransformerRoute;

/**
 * Json Transformed to be used by App Class for rendering JSON reponses to clients.
 *
 * @author gautam
 *
 */
public abstract class JsonTransformerRoute  extends ResponseTransformerRoute {
	
	

	protected JsonTransformerRoute(String path) {
		super(path, "application/json");
	}

	private Gson gson = new Gson();
	
	@Override
	public String render(Object model) {
		return gson.toJson(model);
	}
	

}
