package com.nextbigsound.tunebot.server;

import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.commands.CommandException;

@Method("GET")
@Path("/queue/?")
public class GetQueueRoute extends Route {

	Queue queue = Queue.getInstance();
	
	@Override
	String process(JsonObject json) throws CommandException {
		return queue.toJson().toString();
	}

}
