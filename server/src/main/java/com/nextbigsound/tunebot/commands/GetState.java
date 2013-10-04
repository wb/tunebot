package com.nextbigsound.tunebot.commands;

import java.net.URL;

import com.nextbigsound.tunebot.utils.Resource;

public class GetState extends Command {

	@Override
	public String exec(String param) throws CommandException {
		URL url = Resource.find(GetTrack.class, "resources/get_state.applescript");
		return runOsaScriptWithResource(url);
	}
	
	/**
	 * Testing only.
	 * @param args
	 */
	public static void main(String[] args) {
		Command getState = new GetState();
		String response;
		try {
			response = getState.exec();
			System.out.println(response);
		} catch (CommandException e) {
			System.err.println(e.getMessage());
		}
		
	}

}
