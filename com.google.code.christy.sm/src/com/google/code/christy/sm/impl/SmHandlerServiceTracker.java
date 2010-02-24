package com.google.code.christy.sm.impl;


import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.UserResource;
import com.google.code.christy.xmpp.Packet;

/**
 * @author noah
 * 
 */
public class SmHandlerServiceTracker extends ServiceTracker
{

	public SmHandlerServiceTracker(BundleContext context)
	{
		super(context, SmHandler.class.getName(), null);
	}

	public boolean handleClientPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		Object[] services = getServices();
		if (services == null)
		{
			return false;
		}
		
		for (Object obj : services)
		{
			SmHandler smHandler = (SmHandler) obj;
			if (smHandler.accept(smManager, onlineUser, packet))
			{
				smHandler.handleClientPacket(smManager, onlineUser, userResource, packet);
				return true;
			}
		}
		
		return false;
	}
	
	public boolean handleOtherUserPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		Object[] services = getServices();
		if (services == null)
		{
			return false;
		}
		
		for (Object obj : services)
		{
			SmHandler smHandler = (SmHandler) obj;
			if (smHandler.accept(smManager, onlineUser, packet))
			{
				smHandler.handleOtherUserPacket(smManager, onlineUser, userResource, packet);
				return true;
			}
		}
		
		return false;
	}
	
	public UserResource[] checkResource(SmManager smManager, OnlineUser onlineUser, Packet packet)
	{
		Object[] services = getServices();
		if (services == null)
		{
			return new UserResource[]{};
		}
		
		for (Object obj : services)
		{
			SmHandler smHandler = (SmHandler) obj;
			if (smHandler.accept(smManager, onlineUser, packet))
			{
				return smHandler.checkResource(smManager, onlineUser);
			}
		}
		
		return new UserResource[]{};
	}
	
	public void userResourceAdded(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		Object[] services = getServices();
		if (services == null)
		{
			return;
		}
		
		for (Object obj : services)
		{
			SmHandler smHandler = (SmHandler) obj;
			smHandler.userResourceAdded(smManager, onlineUser, userResource);
		}
	}
	
	public void userResourceRemoved(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		Object[] services = getServices();
		if (services == null)
		{
			return;
		}
		
		for (Object obj : services)
		{
			SmHandler smHandler = (SmHandler) obj;
			smHandler.userResourceRemoved(smManager, onlineUser, userResource);
		}

	}
	
	public void userResourceAvailable(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		Object[] services = getServices();
		if (services == null)
		{
			return;
		}
		
		for (Object obj : services)
		{
			SmHandler smHandler = (SmHandler) obj;
			smHandler.userResourceAvailable(smManager, onlineUser, userResource);
		}
	}
}
