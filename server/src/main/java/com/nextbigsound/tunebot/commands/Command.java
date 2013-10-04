package com.nextbigsound.tunebot.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.commons.io.FileUtils;

public abstract class Command {
	
	/**
	 * Number of seconds to sleep after executing a command that plays a song to avoid
	 * timing issues with retrieving the currently playing track.
	 */
	protected static final long SLEEP = 500;
	
	public abstract String exec(String param) throws CommandException;

	public final String exec() throws CommandException {
		return exec(null);
	}

	protected final String runOsaScript(String param) throws CommandException {
		//System.out.println("Running osascript -e with input '" + param + "'");
		String[] cmd = { "osascript", "-e", param };
		return execCommand(cmd);
	}
	
	protected final String runOsaScriptWithResource(URL resource) throws CommandException {
		try {
			File destination = new File("cmd.applescript");
			FileUtils.copyURLToFile(resource, destination);
			//System.out.println("Running osascript with file " + destination.getAbsolutePath());
			String[] cmd = { "osascript", destination.getAbsolutePath() };
			String output = execCommand(cmd);
			FileUtils.deleteQuietly(destination);
			return output;
		} catch (IOException e) {
			throw new CommandException("Unable to execute osascript with resource file.", e);
		}
	}

	private String execCommand(String[] cmd) throws CommandException {
		try {
			Process process = Runtime.getRuntime().exec(cmd);
			// System.out.println("Process exitValue: " + process.exitValue());
			int exitVal = process.waitFor();
			//System.out.println("Process exitValue: " + exitVal);
			if (exitVal != 0) {
				BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream()));
				String line = null;
				while ((line = r.readLine()) != null) {
					System.err.println(line);
				}
				throw new CommandException("AppleScript execution error");
			} else {
				BufferedReader rd = new BufferedReader(new InputStreamReader(process.getInputStream()));
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ((line = rd.readLine()) != null) {
					builder.append(line);
				}
				String s = builder.toString();
				//System.out.println(s);
				return s;
			}
		} catch (IOException e) {
			throw new CommandException("Internal Server Error due to IOException", e);
		} catch (InterruptedException e) {
			throw new CommandException("Internal Server Error due to InterruptedException", e);
		}
	}

}
