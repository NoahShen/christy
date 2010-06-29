package com.google.code.christy.module.pubsub.defaultpubsub;

public class CannotAccessException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1698026087434901013L;

	/**
	 * 
	 */
	public CannotAccessException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public CannotAccessException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public CannotAccessException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public CannotAccessException(Throwable cause)
	{
		super(cause);
	}

}
