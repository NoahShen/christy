package com.google.code.christy.sm.privatexml;

import com.google.code.christy.dbhelper.PrivateXmlDbHelper;
import com.google.code.christy.dbhelper.PrivateXmlDbHelperTracker;
import com.google.code.christy.dbhelper.PrivateXmlEntity;
import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.UserResource;
import com.google.code.christy.xmpp.Iq;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.XmppError;
import com.google.code.christy.xmpp.privatexml.PrivateXmlExtension;
import com.google.code.christy.xmppparser.UnknownPacketExtension;

public class PrivateXmlHandler implements SmHandler
{
	private PrivateXmlDbHelperTracker privateXmlDbHelperTracker;
	
	/**
	 * @param privateXmlDbHelperTracker
	 */
	public PrivateXmlHandler(PrivateXmlDbHelperTracker privateXmlDbHelperTracker)
	{
		this.privateXmlDbHelperTracker = privateXmlDbHelperTracker;
	}

	@Override
	public boolean accept(SmManager smManager, OnlineUser onlineUser, Packet packet)
	{
		return (packet instanceof Iq) 
			&& packet.containExtension(PrivateXmlExtension.ELEMENTNAME, PrivateXmlExtension.NAMESPACE);
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
		PrivateXmlExtension privateXmlExtension = 
			(PrivateXmlExtension) iq.getExtension(PrivateXmlExtension.ELEMENTNAME, PrivateXmlExtension.NAMESPACE);
		
		UnknownPacketExtension unknownX = privateXmlExtension.getUnknownPacketExtension();
		if (unknownX == null)
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
		
		String elementName = unknownX.getElementName();
		String namespace = unknownX.getNamespace();
		
		String stanzaKey = createStanzaKey(elementName, namespace);
		
		
		PrivateXmlDbHelper privateXmlDbHelper = privateXmlDbHelperTracker.getPrivateXmlDbHelper();
		if (privateXmlDbHelper == null)
		{
			try
			{
				Iq iqError = (Iq) iq.clone();
				iqError.setType(Iq.Type.error);
				iqError.setTo(userResource.getFullJid());
				iqError.setFrom(null);
				iqError.setError(new XmppError(XmppError.Condition.internal_server_error));
				userResource.sendToSelfClient(iqError);
			}
			catch (CloneNotSupportedException e2)
			{
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
			return;
		}
		String username = onlineUser.getNode();
		Iq.Type type = iq.getType();
		if (type == Iq.Type.set)
		{
			PrivateXmlEntity entity = new PrivateXmlEntity();
			entity.setUsername(username);
			entity.setStanzaKey(stanzaKey);
			entity.setStanzaXml(unknownX.toXml());
			
			try
			{
				privateXmlDbHelper.updatePrivateXml(entity);
				
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
					iqError.setError(new XmppError(XmppError.Condition.internal_server_error));
					userResource.sendToSelfClient(iqError);
				}
				catch (CloneNotSupportedException e2)
				{
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
			}
			
			
		}
		else if (type == Iq.Type.get)
		{
			
			try
			{
				PrivateXmlEntity entity = privateXmlDbHelper.getPrivateXml(username, stanzaKey);
				
				Iq iqResponse = new Iq(Iq.Type.result);
				iqResponse.setStanzaId(iq.getStanzaId());
				iqResponse.setTo(userResource.getFullJid());
				
				PrivateXmlExtension privateXml = new PrivateXmlExtension();
				
				UnknownPacketExtension unknownExtension = new UnknownPacketExtension(elementName, namespace);
				if (entity != null)
				{
					unknownExtension.setContent(entity.getStanzaXml());
				}
				
				
				privateXml.setUnknownPacketExtension(unknownExtension);
				
				iqResponse.addExtension(privateXml);
				
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
					iqError.setError(new XmppError(XmppError.Condition.internal_server_error));
					userResource.sendToSelfClient(iqError);
				}
				catch (CloneNotSupportedException e2)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
		}
		
		
		
		
		
	}

	private String createStanzaKey(String elementName, String namespace)
	{
		return "[" + elementName + "][" + namespace + "]";
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
