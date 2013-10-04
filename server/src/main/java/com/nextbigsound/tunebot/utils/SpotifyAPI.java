package com.nextbigsound.tunebot.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SpotifyAPI {

	private static JsonParser jsonParser = new JsonParser();

	private SpotifyAPI() {

	}

	public static JsonObject search(String query) throws IOException {
		String encodedQuery = URLEncoder.encode(query, "UTF-8");
		return readJsonFromUrl("http://ws.spotify.com/search/1/track?q=" + encodedQuery);
	}

	public static JsonObject lookup(String spotifyUri) throws IOException {
		return readJsonFromUrl("http://ws.spotify.com/lookup/1/?uri=" + spotifyUri);
	}

	private static JsonObject readJsonFromUrl(String url) throws IOException {
		URLConnection urlConnection = new URL(url).openConnection();
		urlConnection.addRequestProperty("Accept", "application/json");
		InputStream is = urlConnection.getInputStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			// System.out.println(jsonText);
			JsonObject json = (JsonObject) jsonParser.parse(jsonText);
			return json;
		} finally {
			is.close();
		}
	}

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/**
	 * Test only.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		System.out.println(search("Danza Kuduro"));
		System.out.println(lookup("spotify:track:1kAZhbcsXqfUjnVeqPywn2"));
	}

}
