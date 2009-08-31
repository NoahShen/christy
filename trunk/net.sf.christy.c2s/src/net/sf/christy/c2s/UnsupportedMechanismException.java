/**
 * 
 */
package net.sf.christy.c2s;

/**
 * @author noah
 *
 */
public class UnsupportedMechanismException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5213779779987128817L;

	/**
	 * 
	 */
	public UnsupportedMechanismException()
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnsupportedMechanismException(String message, Throwable cause)
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UnsupportedMechanismException(String message)
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public UnsupportedMechanismException(Throwable cause)
	{
		super(cause);
	}

}
