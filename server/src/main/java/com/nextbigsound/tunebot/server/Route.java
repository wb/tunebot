package com.nextbigsound.tunebot.server;

import com.google.gson.JsonObject;
import com.nextbigsound.tunebot.commands.CommandException;

abstract class Route {
	
	String process() throws CommandException {
		return process(null);
	}

	abstract String process(JsonObject json) throws CommandException;
	
	boolean matches(String method, String path) {
		
		// 1. validate input
		if (method == null) {
			System.err.println("Method is null.");
			return false;
		}
		
		if (path == null) {
			System.err.println("Path is null.");
			return false;
		}
		
		// 2. get annotations
		Method methodAnnotation = this.getClass().getAnnotation(Method.class);
		if (methodAnnotation == null) {
			System.err.println("Method annotation not found.");
			return false;
		}
		
		Path pathAnnotation = this.getClass().getAnnotation(Path.class);
		if (pathAnnotation == null) {
			System.err.println("Path annotation not found.");
			return false;
		}
		
		Disabled disabledAnnotation  = this.getClass().getAnnotation(Disabled.class);
		
		// 3. skip if this route is disabled
		if (disabledAnnotation != null) {
			System.err.println("Route disabled.");
			return false;
		}
		
		// 4. skip if this method does not match
		if (!method.equals(methodAnnotation.value())) {
			//System.err.printf("Input method '%s' does not match expected method '%s'%n", method, methodAnnotation.value());
			return false;
		}
		
		// 5. return true if the path matches!
		if (path.matches(pathAnnotation.value())) {
			return true;
		} else {
			//System.err.printf("Input path '%s' does not match expected path '%s'%n", path, pathAnnotation.value());
		}

		// 6. no match? return false
		return false;
	}
}
