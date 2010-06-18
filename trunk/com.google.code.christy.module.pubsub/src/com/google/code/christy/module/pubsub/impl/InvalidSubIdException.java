package com.google.code.christy.module.pubsub.impl;

public class InvalidSubIdException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3571545318456987585L;

	public InvalidSubIdException()
	{
		super();
	}

	public InvalidSubIdException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public InvalidSubIdException(String message)
	{
		super(message);
	}

	public InvalidSubIdException(Throwable cause)
	{
		super(cause);
	}

}
