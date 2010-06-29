package com.google.code.christy.sm.pep;

import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.UserResource;
import com.google.code.christy.xmpp.Iq;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.pubsub.PubSubExtension;

public class PepHandler implements SmHandler
{

	@Override
	public boolean accept(SmManager smManager, OnlineUser onlineUser, Packet packet)
	{
		boolean b1 =  (packet instanceof Iq);
		boolean b2 =  packet.containExtension(PubSubExtension.ELEMENTNAME, PubSubExtension.NAMESPACE);
		boolean b3 =  packet.getTo() == null;
		return b1 & b2 & b3;
	}

	@Override
	public UserResource[] checkResource(SmManager smManager, OnlineUser onlineUser)
	{
		return new UserResource[]{null};
	}

	@Override
	public void handleClientPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		String to = (String) smManager.getProperty("pep-service");
		if (to != null)
		{
			packet.setTo(new JID(to));
			userResource.sendToOtherUser(packet);
		}
		
	}

	@Override
	public void handleOtherUserPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userResourceAdded(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userResourceAvailable(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userResourceRemoved(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		// TODO Auto-generated method stub

	}

}
