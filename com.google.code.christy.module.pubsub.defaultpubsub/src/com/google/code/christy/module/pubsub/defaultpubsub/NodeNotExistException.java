package com.google.code.christy.module.pubsub.defaultpubsub;

public class NodeNotExistException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6004688402898690988L;

	public NodeNotExistException()
	{
		super();
	}

	public NodeNotExistException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public NodeNotExistException(String message)
	{
		super(message);
	}

	public NodeNotExistException(Throwable cause)
	{
		super(cause);
	}

}
