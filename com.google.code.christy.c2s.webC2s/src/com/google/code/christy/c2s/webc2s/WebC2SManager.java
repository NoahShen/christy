/**
 * 
 */
package com.google.code.christy.c2s.webc2s;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.christy.c2s.C2SManager;
import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.util.StringUtils;

/**
 * @author noah
 *
 */
public class WebC2SManager extends AbstractPropertied implements C2SManager
{

	private static String prefix = StringUtils.randomString(10) + "-";

	private static long id = 0;

	public static synchronized String nextStreamId()
	{
		return prefix + Long.toString(id++);
	}
	
	private final Logger logger = LoggerFactory.getLogger(WebC2SManager.class);
	
	
	@Override
	public void exit()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public int getClientLimit()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDomain()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHostName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRouterIp()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getRouterPassword()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getRouterPort()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isRouterConnected()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isStarted()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setClientLimit(int clientLimit)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setDomain(String domain)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setHostName(String hostName)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setName(String name)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setRouterIp(String routerIp)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setRouterPassword(String routerPassword)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void setRouterPort(int routerPort)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void start()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void addPropertyListener(PropertyListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public boolean containsProperty(String key)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void firePropertyRemoved(String key, Object oldValue)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void firePropertyUpdated(String key, Object newValue, Object oldValue)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Object getProperty(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getPropertyKeys()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PropertyListener[] getPropertyListeners()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeAllProperties()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Object removeProperty(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removePropertyListener(PropertyListener listener)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Object setProperty(String key, Object value)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object setProperty(String key)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
