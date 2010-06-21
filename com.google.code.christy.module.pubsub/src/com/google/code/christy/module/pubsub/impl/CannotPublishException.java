package com.google.code.christy.module.pubsub.impl;

public class CannotPublishException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -88118981703513904L;

	public CannotPublishException()
	{
		super();
	}

	public CannotPublishException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CannotPublishException(String message)
	{
		super(message);
	}

	public CannotPublishException(Throwable cause)
	{
		super(cause);
	}

}
