/**
 * 
 */
package com.google.code.christy.c2s;

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
	 * 
	 * @param clientSession
	 * @param content
	 * @throws UnauthorizedException
	 *                   if the username and password do not match any
	 *                   existing user.
	 */
	public void authenticate(ClientSession clientSession, String content) throws UnauthorizedException;

	/**
	 * 
	 * @param clientSession
	 * @param content
	 * @throws UnauthorizedException
	 */
	public void response(ClientSession clientSession, String content) throws UnauthorizedException;
}
