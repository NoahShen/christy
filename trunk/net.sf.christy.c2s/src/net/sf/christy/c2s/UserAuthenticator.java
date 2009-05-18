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
	     * Returns true if the username and password are valid; otherwise returns
	     * false.<p>
	     *
	     * @param username the username.
	     * @param password the password
	     * @throws UnauthorizedException if the username and password do
	     *  not match any existing user.
	     */
	    public void authenticate(String username, String password) throws UnauthorizedException;
	    
	    /**
	     * Returns true if the username, token, and digest are valid; otherwise this
	     * method throws an UnauthorizedException.<p>
	     *
	     * If {@link #isDigestSupported()} returns false, this method should
	     * throw an UnsupportedOperationException.
	     *
	     * @param username the username or full JID.
	     * @param token the token that was used with plain-text password to
	     *      generate the digest.
	     * @param digest the digest generated from plain-text password and unique token.
	     * @throws UnauthorizedException if the username and password do
	     *  not match any existing user.
	     */
	    public void authenticate(String username, String token, String digest) throws UnauthorizedException;
	    
	    /**
	     * Returns the user's password.
	     *
	     * @param username the username
	     * @return the user's password.
	     * @throws UserNotFoundException if the given user's password could not be loaded.
	     */
	    public String getPassword(String username) throws UserNotFoundException;
}
