package com.google.code.christy.sm.vcard;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.UserResource;
import com.google.code.christy.xmpp.Iq;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.XmppError;
import com.google.code.christy.xmppparser.UnknownPacketExtension;

public class VCardHanlder implements SmHandler
{
	private VCardDbHelperTracker vCardDbHelperTracker;
	
	
	public VCardHanlder(VCardDbHelperTracker cardDbHelperTracker)
	{
		super();
		vCardDbHelperTracker = cardDbHelperTracker;
	}

	@Override
	public boolean accept(SmManager smManager, OnlineUser onlineUser, Packet packet)
	{

		return (packet instanceof Iq) 
			&& packet.containExtension(VCardPacketExtension.ELEMENTNAME, VCardPacketExtension.NAMESPACE);
	}

	@Override
	public UserResource[] checkResource(SmManager smManager, OnlineUser onlineUser)
	{
		return new UserResource[]{null};
	}

	@Override
	public void handleClientPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		Iq iq = (Iq) packet;
		if (iq.getType() == Iq.Type.get)
		{
			String username = onlineUser.getNode();
			JID to = iq.getTo();
			if (to != null)
			{
				if (!username.equals(to.getNode()))
				{
					userResource.sendToOtherUser(iq);
					return;
				}
				
			}
			
			VCardDbHelper vCardDbHelper = vCardDbHelperTracker.getVCardDbHelper();
			if (vCardDbHelper == null)
			{
				try
				{
					Iq iqError = (Iq) iq.clone();
					iqError.setType(Iq.Type.error);
					iqError.setTo(userResource.getFullJid());
					iqError.setFrom(null);
					iqError.setError(new XmppError(XmppError.Condition.bad_request));
					userResource.sendToSelfClient(iqError);
				}
				catch (CloneNotSupportedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			
			
			
			try
			{
				VCardEntity vCardEntity = vCardDbHelper.getVCardEntity(username);
				
				Iq iqResponse = new Iq(Iq.Type.result);
				iqResponse.setStanzaId(iq.getStanzaId());
				iqResponse.setTo(userResource.getFullJid());
				UnknownPacketExtension unknownEx = 
						new UnknownPacketExtension(VCardPacketExtension.ELEMENTNAME, VCardPacketExtension.NAMESPACE);
				if (vCardEntity != null)
				{
					unknownEx.setContent(vCardEntity.getVCardContent());
				}
				iqResponse.addExtension(unknownEx);
				
				userResource.sendToSelfClient(iqResponse);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}
		else if (iq.getType() == Iq.Type.set)
		{
			VCardDbHelper vCardDbHelper = vCardDbHelperTracker.getVCardDbHelper();
			if (vCardDbHelper == null)
			{
				try
				{
					Iq iqError = (Iq) iq.clone();
					iqError.setType(Iq.Type.error);
					iqError.setTo(userResource.getFullJid());
					iqError.setFrom(null);
					iqError.setError(new XmppError(XmppError.Condition.bad_request));
					userResource.sendToSelfClient(iqError);
				}
				catch (CloneNotSupportedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			
			String username = onlineUser.getNode();
			
			VCardEntity vCardEntity = new VCardEntity();
			vCardEntity.setUsername(username);
			
			VCardPacketExtension vCard = 
				(VCardPacketExtension) iq.getExtension(VCardPacketExtension.ELEMENTNAME, VCardPacketExtension.NAMESPACE);			
			
			vCardEntity.setVCardContent(vCard.toXml());
			try
			{
				vCardDbHelper.updateVCardEntity(vCardEntity);
				
				Iq iqResponse = new Iq(Iq.Type.result);
				iqResponse.setStanzaId(iq.getStanzaId());
				iqResponse.setTo(userResource.getFullJid());
				
				userResource.sendToSelfClient(iqResponse);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				try
				{
					Iq iqError = (Iq) iq.clone();
					iqError.setType(Iq.Type.error);
					iqError.setTo(userResource.getFullJid());
					iqError.setFrom(null);
					iqError.setError(new XmppError(XmppError.Condition.bad_request));
					userResource.sendToSelfClient(iqError);
				}
				catch (CloneNotSupportedException e2)
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
			
		}
		else
		{
			
			try
			{
				Iq iqError = (Iq) iq.clone();
				iqError.setTo(userResource.getFullJid());
				iqError.setFrom(null);
				iqError.setError(new XmppError(XmppError.Condition.bad_request));
			}
			catch (CloneNotSupportedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	@Override
	public void handleOtherUserPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		Iq iq = (Iq) packet;
		if (iq.getType() == Iq.Type.get)
		{
			JID to = iq.getTo();
			JID from = iq.getFrom();
			if (from == null)
			{
				return;
			}
			if (to == null)
			{
				return;
			}
			
			VCardDbHelper vCardDbHelper = vCardDbHelperTracker.getVCardDbHelper();
			if (vCardDbHelper == null)
			{
				try
				{
					
					Iq iqError = (Iq) iq.clone();
					iqError.setTo(from);
					iqError.setType(Iq.Type.error);
					iqError.setFrom(to);
					iqError.setError(new XmppError(XmppError.Condition.bad_request));
					RouteMessage routeMessage = new RouteMessage(smManager.getName());
					routeMessage.setXmlStanza(iqError);
					
					if (from.getDomain().equals(smManager.getDomain()))
					{
						String toUserNode = from.getNode();
						routeMessage.setToUserNode(toUserNode);
					}
					smManager.sendToRouter(routeMessage);
				}
				catch (CloneNotSupportedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return;
			}
			
			
			
			try
			{
				VCardEntity vCardEntity = vCardDbHelper.getVCardEntity(to.getNode());
				
				Iq iqResponse = new Iq(Iq.Type.result);
				iqResponse.setStanzaId(iq.getStanzaId());
				iqResponse.setTo(from);
				iqResponse.setFrom(to);
				UnknownPacketExtension unknownEx = 
						new UnknownPacketExtension(VCardPacketExtension.ELEMENTNAME, VCardPacketExtension.NAMESPACE);
				if (vCardEntity != null)
				{
					unknownEx.setContent(vCardEntity.getVCardContent());
				}
				iqResponse.addExtension(unknownEx);
				
				RouteMessage routeMessage = new RouteMessage(smManager.getName());
				routeMessage.setXmlStanza(iqResponse);
				if (from.getDomain().equals(smManager.getDomain()))
				{
					String toUserNode = from.getNode();
					routeMessage.setToUserNode(toUserNode);
				}
				smManager.sendToRouter(routeMessage);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else if (iq.getType() == Iq.Type.result)
		{
			if (userResource != null)
			{
				userResource.sendToSelfClient(iq);
			}
		}
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
