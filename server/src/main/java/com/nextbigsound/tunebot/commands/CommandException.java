package com.nextbigsound.tunebot.commands;

import org.apache.http.HttpStatus;

public class CommandException extends Exception {
	
	public final int code;
	
	public CommandException(String message) {
		this(message, HttpStatus.SC_INTERNAL_SERVER_ERROR);
	}

    //Constructor that accepts a message
    public CommandException(String message, int code)
    {
       this(message, code, null);
    }
    
    public CommandException(String message, Throwable cause)
    {
       this(message, HttpStatus.SC_INTERNAL_SERVER_ERROR, cause);
    }

    public CommandException(String message, int code, Throwable cause) {
    	super(message, cause);
    	this.code = code;
    }
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
