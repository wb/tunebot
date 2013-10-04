package com.nextbigsound.tunebot.server;

import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.commands.Command;
import com.nextbigsound.tunebot.commands.CommandException;
import com.nextbigsound.tunebot.commands.GetTrack;
import com.nextbigsound.tunebot.commands.NextTrack;
import com.nextbigsound.tunebot.commands.PlayTrack;

@Method("POST")
@Path("/nextTrack/?")
class NextTrackRoute extends Route {

	private Command nextTrack = new NextTrack();
	private Command getTrack = new GetTrack();
	private Command playTrack = new PlayTrack();
	private Queue queue = Queue.getInstance();
	
	@Override
	String process(JsonObject json) throws CommandException {
			
		String next = queue.dequeue();
		if (next != null) {
			playTrack.exec(next);
		} else {
			nextTrack.exec();
		}
		return getTrack.exec();
	}
	
}
