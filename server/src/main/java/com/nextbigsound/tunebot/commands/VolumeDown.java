package com.nextbigsound.tunebot.commands;


public class VolumeDown extends Command {

	@Override
	public String exec(String param) throws CommandException {
		String result = runOsaScript("tell application \"Spotify\" to set sound volume to (sound volume - 5)");
		return result;
	}

}
