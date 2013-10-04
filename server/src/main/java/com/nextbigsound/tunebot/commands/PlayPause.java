package com.nextbigsound.tunebot.commands;


public class PlayPause extends Command {

	@Override
	public String exec(String param) throws CommandException {
		if(!param.equals("play") && !param.equals("pause"))
			param = "playpause";
		String result = runOsaScript("tell application \"Spotify\" to " + param);
		return result;
	}

}
