/**
 * 
 */
package net.sf.christy.c2s;

/**
 * @author noah
 *
 */
public class UserNotFoundException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7989799992402494225L;

	/**
	 * 
	 */
	public UserNotFoundException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UserNotFoundException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UserNotFoundException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public UserNotFoundException(Throwable cause)
	{
		super(cause);
	}

}
