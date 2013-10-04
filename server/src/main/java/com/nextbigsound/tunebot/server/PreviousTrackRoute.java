package com.nextbigsound.tunebot.server;

import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.commands.Command;
import com.nextbigsound.tunebot.commands.CommandException;
import com.nextbigsound.tunebot.commands.GetTrack;
import com.nextbigsound.tunebot.commands.PreviousTrack;

@Method("POST")
@Path("/previousTrack/?")
class PreviousTrackRoute extends Route {

	private Command previousTrack = new PreviousTrack();
	private Command getTrack = new GetTrack();

	@Override
	String process(JsonObject json) throws CommandException {
		
		// see if we should restart the song or go to the last song
		boolean restart = false;
		if (json != null && json.has("restart")) {
			restart = json.get("restart").getAsBoolean();
		}
		System.out.println("Restart: " + restart);
		
		// get the current song, send the previous command, then see what song is playing
		String currentSong = getTrack.exec();
		previousTrack.exec();
		String previousSong = getTrack.exec();
		
		// if the song has not changed and we are not restarting, go back again
		if(currentSong.equals(previousSong) && !restart){
			previousTrack.exec();
		}
		
		// return the name of the song now playing
		return getTrack.exec();
	}
	
}
