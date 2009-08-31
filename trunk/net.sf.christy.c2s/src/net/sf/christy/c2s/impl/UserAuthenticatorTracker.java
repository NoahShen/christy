/**
 * 
 */
package net.sf.christy.c2s.impl;

import net.sf.christy.c2s.ClientSession;
import net.sf.christy.c2s.UnauthorizedException;
import net.sf.christy.c2s.UnsupportedMechanismException;
import net.sf.christy.c2s.UserAuthenticator;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author noah
 *
 */
public class UserAuthenticatorTracker extends ServiceTracker
{

	public UserAuthenticatorTracker(BundleContext context)
	{
		super(context, UserAuthenticator.class.getName(), null);
	}

	public void authenticate(ClientSession clientSession, String content, String mechanism) throws UnauthorizedException, UnsupportedMechanismException
	{
		Object[] services = getServices();
		if (services == null)
		{
			throw new UnsupportedMechanismException("no UserAuthenticator");
		}
		
		for (Object obj : services)
		{
			UserAuthenticator authenticator = (UserAuthenticator) obj;
			if (mechanism.equals(authenticator.getMechanismName()))
			{
				authenticator.authenticate(clientSession, content);
				return;
			}
		}
		
		throw new UnauthorizedException("username or password invalided");
	}
	
	public void response(ClientSession clientSession, String content) throws UnauthorizedException
	{
		UserAuthenticator authenticator = (UserAuthenticator) clientSession.getProperty("currentMechanism");
		authenticator.response(clientSession, content);
	}
	
	public String[] getAllMechanisms()
	{
		Object[] services = getServices();
		if (services == null)
		{
			return new String[]{};
		}
		
		String[] mechanisms = new String[services.length];
		for (int i = 0; i < services.length; ++i)
		{
			UserAuthenticator authenticator = (UserAuthenticator) services[i];
			mechanisms[i] = authenticator.getMechanismName();
		}
		return mechanisms;
	}
	
}
