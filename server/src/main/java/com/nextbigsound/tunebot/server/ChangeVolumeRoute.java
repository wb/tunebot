package com.nextbigsound.tunebot.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpStatus;

import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.commands.Command;
import com.nextbigsound.tunebot.commands.CommandException;
import com.nextbigsound.tunebot.commands.SetVolume;
import com.nextbigsound.tunebot.commands.VolumeDown;
import com.nextbigsound.tunebot.commands.VolumeUp;

@Method("POST")
@Path("/volume/?")
public class ChangeVolumeRoute extends Route {

	private Command setVolume = new SetVolume();
	private Command volumeUp = new VolumeUp();
	private Command volumeDown = new VolumeDown();
	
	@SuppressWarnings("serial")
	@Override
	String process(JsonObject json) throws CommandException {
				
		String value = json.get("value").getAsString();
		
		// is it a number?
		try {
			Integer.parseInt(value);
			return setVolume.exec(value);
		} catch (NumberFormatException e) {
			
			Map<String, String> absoluteShortcuts = new HashMap<String, String>() {
				{
					put("bumpin", "100");
					put("raise the roof", "100");
					put("loud", "80");
					put("on", "60");
					put("unmute", "60");
					put("quiet", "40");
					put("low", "40");
					put("shut up", "0");
					put("off", "0");
					put("mute", "0");
				}

			};
			
			if (absoluteShortcuts.containsKey(value)) {
				return setVolume.exec(absoluteShortcuts.get(value));
			}
			
			Set<String> upShortcuts = new HashSet<String>() {
				{
					add("up");
					add("higher");
					add("louder");
					add("turn it up");
					add("turn it up!");
				}
			};
			
			if (upShortcuts.contains(value)) {
				return volumeUp.exec();
			}
			
			Set<String> downShortcuts = new HashSet<String>() {
				{
					add("down");
					add("quieter");
					add("lower");
					add("softer");
					add("turn it down");
					add("turn it down!");
				}
			};
			
			if (downShortcuts.contains(value)) {
				return volumeDown.exec();
			}
			
			// Error!
			
			StringBuilder builder = new StringBuilder();
			
			for (String s : upShortcuts) {
				builder.append(s + ", ");
			}
			
			for (String s : downShortcuts) {
				builder.append(s + ", ");
			}
			
			for (String s : absoluteShortcuts.keySet()) {
				builder.append(s + ", ");
			}
			
			builder.append("0-100");
			
			throw new CommandException("Available volume options are: " + builder.toString(), HttpStatus.SC_BAD_REQUEST);

		}
		
	}

}
