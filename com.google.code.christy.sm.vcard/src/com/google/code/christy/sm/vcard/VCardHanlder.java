package com.google.code.christy.sm.vcard;

import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.UserResource;
import com.google.code.christy.xmpp.Iq;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.XmppError;

public class VCardHanlder implements SmHandler
{

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
			
			// TODO get vcard from db
			
			// TODO test code
			Iq iqResponse = new Iq(Iq.Type.result);
			iqResponse.setStanzaId(iq.getStanzaId());
			iqResponse.setTo(userResource.getFullJid());
			VCardPacketExtension vCard = new VCardPacketExtension();
			vCard.setNickName("Noah");
			vCard.setPhotoType("image/gif");
			vCard.setPhotoBinval("R0lGODlhDwAPAKECAAAAzMzM/////wAAACwAAAAADwAPAAACIISPeQHsrZ5ModrLlN48CXF8m2iQ3YmmKqVlRtW4MLwWACH+H09wdGltaXplZCBieSBVbGVhZCBTbWFydFNhdmVyIQAAOw==");
			iqResponse.addExtension(vCard);
			userResource.sendToSelfClient(iqResponse);
		}
		else if (iq.getType() == Iq.Type.set)
		{
			// TODO set vcard
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
