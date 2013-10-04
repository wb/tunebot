package com.nextbigsound.tunebot.commands;


public class NextTrack extends Command {

	@Override
	public String exec(String param) throws CommandException {
		String result = runOsaScript("tell application \"Spotify\" to next track");
		
		try {
			Thread.sleep(Command.SLEEP);
		} catch (InterruptedException e) {
			// dont care!
		}
		
		return result;
	}

}
