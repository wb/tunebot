package com.nextbigsound.tunebot.commands;

import java.net.URL;

import com.nextbigsound.tunebot.utils.Resource;

public class GetTrack extends Command {

	@Override
	public String exec(String param) throws CommandException {
		URL url = Resource.find(GetTrack.class, "resources/get_track.applescript");
		return runOsaScriptWithResource(url);
	}

	/**
	 * Test only.
	 * @param args
	 */
	public static void main(String[] args) {
		GetTrack pt = new GetTrack();
		try {
			pt.exec();
		} catch (CommandException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
