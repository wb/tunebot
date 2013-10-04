package com.nextbigsound.tunebot.server;

import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.commands.CommandException;
import com.nextbigsound.tunebot.commands.SearchTrack;

@Method("POST")
@Path("/search/?")
public class SearchTrackRoute extends Route {

	private SearchTrack searchTrack = new SearchTrack();
	
	@Override
	String process(JsonObject json) throws CommandException {
		String query = json.get("query").getAsString();
		return searchTrack.exec(query);
	}
}
