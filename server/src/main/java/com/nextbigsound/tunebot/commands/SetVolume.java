package com.nextbigsound.tunebot.commands;

public class SetVolume extends Command {

	@Override
	public String exec(String param) throws CommandException {
		String result = runOsaScript(String.format("tell application \"Spotify\" to set sound volume to %s", param));
		return result;
	}
	
	public static void main(String[] args) {
		Command c = new SetVolume();
		try {
			c.exec("spotify:track:2Foc5Q5nqNiosCNqttzHof");
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
