package com.google.code.christy.module.pubsub.impl;

public class SubscribeForbiddenException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5080531510396773374L;

	public SubscribeForbiddenException()
	{
		super();
	}

	public SubscribeForbiddenException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public SubscribeForbiddenException(String message)
	{
		super(message);
	}

	public SubscribeForbiddenException(Throwable cause)
	{
		super(cause);
	}

}
