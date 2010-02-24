package com.google.code.christy.sm.message;

import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.UserResource;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.Message;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.PacketUtils;
import com.google.code.christy.xmpp.XmppError;


/**
 * 
 * @author Noah
 *
 */
public class MessageHandler implements SmHandler
{

	@Override
	public boolean accept(SmManager smManager, OnlineUser onlineUser, Packet packet)
	{
		return (packet instanceof Message);
	}

	@Override
	public UserResource[] checkResource(SmManager smManager, OnlineUser onlineUser)
	{
		if (onlineUser != null)
		{
			return onlineUser.getAllActiveUserResources();
		}
		return new UserResource[]{null};
	}

	@Override
	public void handleClientPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		Message message = (Message) packet;
		JID to = message.getTo();
		JID from = message.getFrom();
		
		
		if ((to == null)
			 || (from != null && !from.equals(userResource.getFullJid())))
		{
			Message messageError = PacketUtils.createErrorMessage(message);
			messageError.setError(new XmppError(XmppError.Condition.bad_request));
			userResource.sendToSelfClient(messageError);
			return;
		}
		
		if (from == null)
		{
			message.setFrom(userResource.getFullJid());
		}
		
		// TODO message history
		
		userResource.sendToOtherUser(message);
		
	}

	@Override
	public void handleOtherUserPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		// TODO check the user is exist in DB
		if (onlineUser == null || userResource == null)
		{
			// TODO offlineMessage
			return;
		}
		
		Message message = (Message) packet;
		// TODO message history
		
		userResource.sendToSelfClient(message);
	}

	@Override
	public void userResourceAdded(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void userResourceAvailable(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		// TODO offlineMessage

	}

	@Override
	public void userResourceRemoved(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		// TODO Auto-generated method stub

	}

}
