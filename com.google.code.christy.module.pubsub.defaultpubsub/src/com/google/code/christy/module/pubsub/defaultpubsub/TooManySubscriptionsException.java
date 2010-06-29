package com.google.code.christy.module.pubsub.defaultpubsub;

public class TooManySubscriptionsException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8836508464470257086L;

	public TooManySubscriptionsException()
	{
		super();
	}

	public TooManySubscriptionsException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public TooManySubscriptionsException(String message)
	{
		super(message);
	}

	public TooManySubscriptionsException(Throwable cause)
	{
		super(cause);
	}

}
