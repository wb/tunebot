package com.nextbigsound.tunebot.utils;

import java.io.File;
import java.net.URL;

public class Resource {

	public static URL find(Class<?> c, String path){
		path = path.trim();
		
		// Try the path as is
		URL url = tryPath(c, path);
		if (url != null)
			return url;
		
		// Try the path as an absolute path, if it wasn't already absolute
		url = tryAbsoluteFetch(c, path);
		if (url != null)
			return url;
		
		// Try the path as an relative path, if it wasn't already relative
		url = tryRelativeFetch(c, path);
		if (url != null)
			return url;
		
		// Try the same lookups, using only the file name and not the parent path
		try {
			path = path.substring(path.lastIndexOf(File.separator));
		} catch (Exception e1) {
			// There are no parents so checking further is not necessary
			return null;
		}

		url = tryAbsoluteFetch(c, path);
		if (url != null)
			return url;

		return tryRelativeFetch(c, path);
	}
	
	private static URL tryAbsoluteFetch(Class<?> c, String path){
		if (path.startsWith(File.separator)){
			return null;
		}
		path = File.separator+path;
		return tryPath(c, path);
	}
	
	private static URL tryRelativeFetch(Class<?> c, String path){
		if (!path.startsWith(File.separator)){
			return null;
		}
		path = path.substring(1);
		return tryPath(c, path);
	}
	
	private static URL tryPath(Class<?> c, String path){
		URL url = c.getClassLoader().getResource(path);
		if (url != null)
			return url;
		return c.getResource(path);
	}
}