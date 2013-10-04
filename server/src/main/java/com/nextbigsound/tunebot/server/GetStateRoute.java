package com.nextbigsound.tunebot.server;

import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.commands.Command;
import com.nextbigsound.tunebot.commands.CommandException;
import com.nextbigsound.tunebot.commands.GetState;

@Method("GET")
@Path("/state/?")
public class GetStateRoute extends Route {

	private Command getState = new GetState();
	
	@Override
	String process(JsonObject json) throws CommandException {
		return getState.exec();
	}

}
