package com.nextbigsound.tunebot.server;

import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.commands.CommandException;
import com.nextbigsound.tunebot.commands.GetTrack;

@Method("GET")
@Path("/track/?")
public class GetTrackRoute extends Route {

	private GetTrack getTrack = new GetTrack();
	
	@Override
	String process(JsonObject json) throws CommandException {
		return getTrack.exec();
	}

}
