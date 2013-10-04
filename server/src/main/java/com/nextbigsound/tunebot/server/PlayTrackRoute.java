package com.nextbigsound.tunebot.server;

import java.io.IOException;

import org.apache.http.HttpStatus;

import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.commands.Command;
import com.nextbigsound.tunebot.commands.CommandException;
import com.nextbigsound.tunebot.commands.GetTrack;
import com.nextbigsound.tunebot.commands.PlayTrack;
import com.nextbigsound.tunebot.commands.SearchResultCache;
import com.nextbigsound.tunebot.utils.SongResolver;
import com.nextbigsound.tunebot.utils.SpotifyAPI;

@Method("POST")
@Path("/track/?")
public class PlayTrackRoute extends Route {

	private Command playTrack = new PlayTrack();
	private Command getTrack = new GetTrack();
	@Override
	String process(JsonObject json) throws CommandException {
		
		// get the track
		String track = json.get("track").getAsString();
		
		// check to see if it is a number (corresponds to search results)
		if (isInteger(track)) {
			System.out.println("Resolving " + track + "...");
			int index = Integer.parseInt(track);
			String resultTrack = SearchResultCache.getInstance().getSongFromLastSearch(index);
			if (resultTrack == null) {
				throw new CommandException("Did you pick a valid search result?", HttpStatus.SC_BAD_REQUEST);
			} else {
				track = resultTrack;
				System.out.println("Resolved to " + track);
			}
		}

		// validate the track against Spotify's API
		try {
			SpotifyAPI.lookup(track);
		} catch (IOException e) {
			// in some cases (e.g. playlists) the lookup will fail when the song should go through
			track = SongResolver.resolveSong(track);
			if (track == null) {
				throw new CommandException("Looks like that song might not be valid...", HttpStatus.SC_NOT_FOUND, e);
			}
		}
		
		// is this track to be queued?
		if (json.has("enqueue") && json.get("enqueue").getAsBoolean() == true) {
			Queue.getInstance().enqueue(track);
			return null;
		} else {
			playTrack.exec(track);
			return getTrack.exec();
		}
	}
	
	private static boolean isInteger(String input) {
		try {
			Integer.parseInt(input);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
