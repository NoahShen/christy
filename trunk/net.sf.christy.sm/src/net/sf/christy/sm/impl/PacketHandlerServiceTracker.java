package net.sf.christy.sm.impl;

import net.sf.christy.sm.OnlineUser;
import net.sf.christy.sm.PacketHandler;
import net.sf.christy.sm.SmManager;
import net.sf.christy.sm.UserResource;
import net.sf.christy.xmpp.Packet;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author noah
 * 
 */
public class PacketHandlerServiceTracker extends ServiceTracker
{

	public PacketHandlerServiceTracker(BundleContext context)
	{
		super(context, PacketHandler.class.getName(), null);
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
			PacketHandler packetHandler = (PacketHandler) obj;
			if (packetHandler.accept(smManager, onlineUser, userResource, packet))
			{
				packetHandler.handleClientPacket(smManager, onlineUser, userResource, packet);
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
			PacketHandler packetHandler = (PacketHandler) obj;
			if (packetHandler.accept(smManager, onlineUser, userResource, packet))
			{
				packetHandler.handleOtherUserPacket(smManager, onlineUser, userResource, packet);
				return true;
			}
		}
		
		return false;
	}
}
