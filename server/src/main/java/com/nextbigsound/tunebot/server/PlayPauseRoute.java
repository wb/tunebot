package com.nextbigsound.tunebot.server;

import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.commands.Command;
import com.nextbigsound.tunebot.commands.CommandException;
import com.nextbigsound.tunebot.commands.PlayPause;

@Method("POST")
@Path("/playPause/?")
class PlayPauseRoute extends Route {

	private Command playPause = new PlayPause();

	
	@Override
	String process(JsonObject json) throws CommandException {
		String state = json.get("state").getAsString();
		return playPause.exec(state);
	}
	
}
