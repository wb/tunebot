package com.nextbigsound.tunebot.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class SearchResultCache {

	private static SearchResultCache searchResultCache = null;
	
	private JsonObject resultsJson;
	private long timestamp;
	
	private static final long TTL = 90 * 1000; // 90 seconds
	
	private SearchResultCache() {
		
	}
	
	/**
	 * Ensure we only have one copy of this per application.
	 * 
	 * @return
	 */
	public static SearchResultCache getInstance() {
		if (searchResultCache == null) {
			searchResultCache = new SearchResultCache();
		}
		return searchResultCache;
	}
	
	public void saveLastSearch(JsonObject results) {
		this.resultsJson = results;
		timestamp = System.currentTimeMillis();
	}
	
	public String getSongFromLastSearch(int index) {
		
		// if there are no results, return
		if (resultsJson == null) {
			return null;
		}
		
		// decrement by 1 since people aren't 0-indexed (and input is given 1 indexed)
		index--;
				
		long now = System.currentTimeMillis();
		long ellapsed = now - timestamp;
		JsonArray r = resultsJson.get("results").getAsJsonArray();
		if (index < 0 || index >= r.size() || ellapsed > TTL) {
			return null;
		} else {
			return r.get(index).getAsJsonObject().get("href").getAsString();
		}
	}
	
}
