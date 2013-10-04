package com.nextbigsound.tunebot.commands;

public class PlayTrack extends Command {

	@Override
	public String exec(String param) throws CommandException {

		String result = runOsaScript(String.format("tell application \"Spotify\" to play track \"%s\"", param));
		try {
			Thread.sleep(Command.SLEEP);
		} catch (InterruptedException e) {
			// dont care!
		}
		return result;
	}
	
	public static void main(String[] args) {
		Command c = new PlayTrack();
		try {
			c.exec("spotify:track:2Foc5Q5nqNiosCNqttzHof");
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
