/**
 * 
 */
package net.sf.christy.c2s.impl;

import net.sf.christy.c2s.ClientSession;
import net.sf.christy.c2s.UnauthorizedException;
import net.sf.christy.c2s.UserAuthenticator;

/**
 * @author noah
 *
 */
public class PlainUserAuthenticatorImpl implements UserAuthenticator
{


	@Override
	public String getMechanismName()
	{
		return "PLAIN";
	}
	
	@Override
	public void authenticate(ClientSession clientSession, String content) throws UnauthorizedException
	{
		// TODO Auto-generated method stub
		System.out.println("===================" + content);
		clientSession.setUsername("Noah");
	}


	@Override
	public void response(ClientSession clientSession, String content) throws UnauthorizedException
	{
		// TODO Auto-generated method stub
		
	}


}
