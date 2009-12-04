/**
 * 
 */
package net.sf.christy.c2s.impl;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import net.sf.christy.c2s.ClientSession;
import net.sf.christy.c2s.UnauthorizedException;
import net.sf.christy.c2s.UserAuthenticator;
import net.sf.christy.util.Base64;
import net.sf.christy.util.StringUtils;
import net.sf.christy.xmpp.CloseStream;
import net.sf.christy.xmpp.Failure;

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
		
		try
		{
			String decodedContent = new String(StringUtils.decodeBase64(content), "UTF-8");
			String[] splits = decodedContent.split("\0");
			if (splits.length != 3)
			{
				throw new UnauthorizedException();
			}
			
			
			// TODO Auto-generated method stub
			String username = splits[1];
			String password = splits[2];
			clientSession.setUsername(username);
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public void response(ClientSession clientSession, String content) throws UnauthorizedException
	{
		// TODO Auto-generated method stub
		
	}


}
