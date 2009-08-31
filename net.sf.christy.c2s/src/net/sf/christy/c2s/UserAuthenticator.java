/**
 * 
 */
package net.sf.christy.c2s;

/**
 * @author noah
 * 
 */
public interface UserAuthenticator
{
	/**
	 * 
	 * @return
	 */
	public String getMechanismName();

	/**
	 * Returns true if the username and password are valid; otherwise
	 * returns false.
	 * <p>
	 * 
	 * @param username
	 *                  the username.
	 * @param password
	 *                  the password
	 * @throws UnauthorizedException
	 *                   if the username and password do not match any
	 *                   existing user.
	 */
	public void authenticate(String username, String password) throws UnauthorizedException;

	/**
	 * Returns the user's password.
	 * 
	 * @param username
	 *                  the username
	 * @return the user's password.
	 * @throws UserNotFoundException
	 *                   if the given user's password could not be loaded.
	 */
	public String getPassword(String username) throws UserNotFoundException;
}
