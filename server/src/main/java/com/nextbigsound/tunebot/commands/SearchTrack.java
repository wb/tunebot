package com.nextbigsound.tunebot.commands;

import java.util.Iterator;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.utils.SpotifyAPI;

public class SearchTrack extends Command {

	@Override
	public String exec(String param) throws CommandException {
		JsonArray results = new JsonArray();

		try {

			JsonObject result = SpotifyAPI.search(param);
			JsonArray tracks = result.get("tracks").getAsJsonArray();
			Iterator<JsonElement> i = tracks.iterator();

			while (i.hasNext() && results.size() < 5) {

				JsonObject theTrack = new JsonObject();

				JsonElement track = i.next();
				JsonElement href = track.getAsJsonObject().get("href");
				JsonElement name = track.getAsJsonObject().get("name");
				JsonElement album = track.getAsJsonObject().get("album").getAsJsonObject().get("name");
				JsonElement artist = track.getAsJsonObject().get("artists").getAsJsonArray().get(0).getAsJsonObject().get("name");

				theTrack.add("href", href);
				theTrack.add("name", name);
				theTrack.add("album", album);
				theTrack.add("artist", artist);

				results.add(theTrack);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		JsonObject really = new JsonObject();
		really.add("results", results);

		SearchResultCache.getInstance().saveLastSearch(really);

		return really.toString();
	}

	/**
	 * Test only.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SearchTrack pt = new SearchTrack();
		try {
			System.out.println(pt.exec("eye of the tiger"));
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
