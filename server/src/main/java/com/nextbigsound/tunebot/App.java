package com.nextbigsound.tunebot;

import com.nextbigsound.tunebot.server.WebServer;

public class App {

	private static final int DEFAULT_PORT = 8080;

	public static void main(String[] args) {

		int port = getPort();

		try {
			WebServer.start(port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int getPort() {
		Integer port = DEFAULT_PORT;
		if (System.getenv("PORT") != null) {
			try {
				port = Integer.parseInt(System.getenv("PORT"));
			} catch (NumberFormatException e) {
				System.err.println("Unable to read PORT from environment.");
			}
		}
		return port;
	}
}
