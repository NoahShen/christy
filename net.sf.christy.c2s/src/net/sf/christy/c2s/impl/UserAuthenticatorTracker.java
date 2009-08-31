/**
 * 
 */
package net.sf.christy.c2s.impl;

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

	public void authenticate(String username, String password, String mechanism) throws UnauthorizedException, UnsupportedMechanismException
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
				authenticator.authenticate(username, password);
				return;
			}
		}
		
		throw new UnauthorizedException("username or password invalided");
	}
}
