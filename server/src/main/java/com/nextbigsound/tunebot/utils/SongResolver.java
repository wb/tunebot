package com.nextbigsound.tunebot.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

/**
 * Returns a URL of File path that Spotify can use to open the given song.
 * 
 * @author Walter
 *
 */
public class SongResolver {

	/**
	 * Return URL for song that does not match the Spotify API.
	 * 
	 * @param song
	 * @return
	 */
	public static String resolveSong(String song) {
		
		// Spotify
		if (song.matches("spotify:user:([a-zA-Z0-9]+):playlist:([a-zA-Z0-9]+)")) {
			return song;
		} else if (song.matches("spotify:local:(.+)")) {
			return song;
		}
		
		// CloudApp
		if (song.matches("http://cl.ly/(.+)")) {
			try {
				URL url = new URL(song);
				InputStream is = url.openStream();
				Writer writer = new StringWriter();
				IOUtils.copy(is, writer);
				String body = writer.toString();
				String downloadRegex = "(<a href=\"http://cl.ly/([a-zA-Z0-9]+)/(.+)\">view</a>)";
				Pattern p = Pattern.compile(downloadRegex);
				Matcher m = p.matcher(body);
				m.find();
				String href = m.group().replace("<a href=\"", "").replace("\">view</a>", "");	
				System.out.println(href);
//				URL download = new URL(href);
//				File temp = File.createTempFile("tunebot", ".mp3");
//				temp.deleteOnExit();
//				FileUtils.copyURLToFile(download, temp);
//				System.out.println(temp.getAbsolutePath());
//				Thread.sleep(60*1000);

				
			} catch (MalformedURLException e) {
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return null;
	}
	
	/**
	 * For test only.
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(resolveSong("http://cl.ly/3M1g0y0V0a2x"));
	}
}
