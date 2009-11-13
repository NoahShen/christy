package net.sf.christy.sm.impl;

import net.sf.christy.sm.PacketHandler;
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

	public boolean handlePacket(UserResource userResource, Packet packet)
	{
		Object[] services = getServices();
		if (services == null)
		{
			return false;
		}
		
		for (Object obj : services)
		{
			PacketHandler packetHandler = (PacketHandler) obj;
			if (packetHandler.accept(userResource, packet))
			{
				packetHandler.handlePacket(userResource, packet);
				return true;
			}
		}
		
		return false;
	}
}
