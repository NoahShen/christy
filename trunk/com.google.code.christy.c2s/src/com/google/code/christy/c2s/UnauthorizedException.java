/**
 * 
 */
package com.google.code.christy.c2s;

/**
 * @author noah
 *
 */
public class UnauthorizedException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5066919843169220643L;

	/**
	 * 
	 */
	public UnauthorizedException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnauthorizedException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UnauthorizedException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public UnauthorizedException(Throwable cause)
	{
		super(cause);
	}

}
