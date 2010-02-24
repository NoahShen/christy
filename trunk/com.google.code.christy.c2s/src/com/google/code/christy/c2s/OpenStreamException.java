package com.google.code.christy.c2s;

public class OpenStreamException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6626312709476602991L;

	/**
	 * 
	 */
	public OpenStreamException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public OpenStreamException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public OpenStreamException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public OpenStreamException(Throwable cause)
	{
		super(cause);
	}

}
