package com.nextbigsound.tunebot.server;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.nextbigsound.tunebot.commands.Command;
import com.nextbigsound.tunebot.commands.CommandException;
import com.nextbigsound.tunebot.commands.GetState;
import com.nextbigsound.tunebot.commands.PlayTrack;

public class Queue {

	private static Queue instance = null;
	
	private Command getState = new GetState();
	private Command playTrack = new PlayTrack();
	private JsonParser jsonParser = new JsonParser();
	private java.util.Queue<String> queue = new LinkedList<String>();
	
	// if this many seconds or fewer are left on the current song, a song from the queue will be played
	private static final int SKIP_SECONDS_LEFT = 5;
	
	private Queue() {
				
		// set up service to poll spotify every second to determine if we should
		ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleAtFixedRate(new Runnable() {

			public void run() {
				try {
					// only proceed if there is a song in the queue
					if (size() > 0) {
						
						// get state and proceed
						try {
							JsonObject state = (JsonObject) jsonParser.parse(getState.exec());
							int position = state.get("position").getAsInt();
							int duration = state.get("duration").getAsInt();
							String previousSong = state.get("track_id").getAsString();
							//System.out.printf("Current song is at %d of %d and is %s%n", position, duration, playerState);
				
							// if there is little time left, play a new song!
							int timeLeft = duration - position; // in seconds
							if (timeLeft <= SKIP_SECONDS_LEFT) {
								
								try {
									Thread.sleep(1000 * (timeLeft - 1));
								} catch (Exception e) {
									// ignore!
								}
								
								while (size() > 0) {
									
									// get a song to play
									String song = dequeue();
									if (song == null) {
										continue;
									}
									
									System.out.println("Trying to play song " + song);
									
									// play the song
									playTrack.exec(song);
									
									// make sure we are playing a new song now
									JsonObject newState = (JsonObject) jsonParser.parse(getState.exec());
									String newSong = newState.get("track_id").getAsString();
									if (!previousSong.equals(newSong)) {
										break; // break out of loop now that we are playing a new song
									}
								}
							}
							
						} catch (JsonSyntaxException e) {
							System.err.println("Unable to parse JSON.");
						} catch (CommandException e) {
							System.err.println("Unable to execute command.");
						}
						
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
			
		}, 0, 1, TimeUnit.SECONDS);
	}
	
	/**
	 * Make sure we only have one instance of this Queue per app.
	 * 
	 * @return
	 */
	public static Queue getInstance() {
		if (instance == null) {
			instance = new Queue();
		}
		return instance;
	}
	
	/**
	 * Return an item off the queue or null.
	 * 
	 * @return
	 */
	public synchronized String dequeue() {
		return queue.poll();
	}
	
	public synchronized void enqueue(String item) {
		if (item != null) {
			queue.add(item);
		}
	}
	
	public synchronized int size() {
		return queue.size();
	}
	
	public synchronized JsonObject toJson() {
		JsonObject json = new JsonObject();
		JsonArray array = new JsonArray();
		for (String s : queue) {
			array.add(new JsonPrimitive(s));
		}
		json.add("queue", array);
		return json;	
	}
	
	@Override
	public synchronized String toString() {
		if (queue.isEmpty()) {
			return "The queue is empty!";
		} else {
			return Joiner.on("\n").join(queue);
		}
	}

}
