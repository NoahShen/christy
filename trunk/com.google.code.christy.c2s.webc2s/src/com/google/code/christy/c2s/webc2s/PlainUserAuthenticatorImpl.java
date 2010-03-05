/**
 * 
 */
package com.google.code.christy.c2s.webc2s;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import com.google.code.christy.c2s.ClientSession;
import com.google.code.christy.c2s.UnauthorizedException;
import com.google.code.christy.c2s.UserAuthenticator;
import com.google.code.christy.util.Base64;
import com.google.code.christy.util.StringUtils;
import com.google.code.christy.xmpp.CloseStream;
import com.google.code.christy.xmpp.Failure;


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
