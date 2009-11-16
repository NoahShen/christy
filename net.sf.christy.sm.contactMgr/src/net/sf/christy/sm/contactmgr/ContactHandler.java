package net.sf.christy.sm.contactmgr;

import net.sf.christy.sm.PacketHandler;
import net.sf.christy.sm.UserResource;
import net.sf.christy.xmpp.Iq;
import net.sf.christy.xmpp.IqRoster;
import net.sf.christy.xmpp.JID;
import net.sf.christy.xmpp.Packet;
import net.sf.christy.xmpp.Presence;

/**
 * 
 * @author noah
 *
 */
public class ContactHandler implements PacketHandler
{

	@Override
	public boolean accept(UserResource userResource, Packet packet)
	{
		if (packet instanceof Iq)
		{
			Iq iq = (Iq) packet;
			if (iq.getExtension(IqRoster.ELEMENTNAME, IqRoster.NAMESPACE) != null)
			{
				return true;
			}
		}
		else if (packet instanceof Presence)
		{
			return true;
		}
		return false;
	}

	@Override
	public void handlePacket(UserResource userResource, Packet packet)
	{
		
		if (packet instanceof Iq)
		{
			handleRoster(userResource, (Iq) packet);
			
		}
		else if (packet instanceof Presence)
		{
			handlePresence(userResource, (Presence) packet);
		}
		
	}

	private void handlePresence(UserResource userResource, Presence presence)
	{
		Presence.Type type = presence.getType();
		if (type == Presence.Type.available
				|| type == Presence.Type.unavailable)
		{
			handleStateChanged(userResource, presence);
		}
	}

	private void handleStateChanged(UserResource userResource, Presence presence)
	{
		// TODO
	}

	private void handleRoster(UserResource userResource, Iq iq)
	{
		Iq iqResult = new Iq(Iq.Type.result);
		iqResult.setStanzaId(iq.getStanzaId());
		
		// TODO change to db
		IqRoster iqRoster = new IqRoster();
		IqRoster.Item item = new IqRoster.Item(new JID("Noah.Shen87@gmail.com"));
		item.setSubscription(IqRoster.Subscription.both);
		iqRoster.addRosterItem(item);
		
		iqResult.addExtension(iqRoster);
		
		userResource.sendToSelfClient(iqResult);
	}

}
