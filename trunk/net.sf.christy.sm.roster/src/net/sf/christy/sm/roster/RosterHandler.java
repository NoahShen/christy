package net.sf.christy.sm.roster;

import net.sf.christy.sm.PacketHandler;
import net.sf.christy.sm.UserResource;
import net.sf.christy.xmpp.Iq;
import net.sf.christy.xmpp.IqRoster;
import net.sf.christy.xmpp.JID;
import net.sf.christy.xmpp.Packet;

/**
 * 
 * @author noah
 *
 */
public class RosterHandler implements PacketHandler
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
		return false;
	}

	@Override
	public void handlePacket(UserResource userResource, Packet packet)
	{
		Iq iq = (Iq) packet;
		Iq iqResult = new Iq(Iq.Type.result);
		iqResult.setStanzaId(iq.getStanzaId());
		
		// TODO
		IqRoster iqRoster = new IqRoster();
		IqRoster.Item item = new IqRoster.Item(new JID("Noah.Shen87@gmail.com"));
		item.setSubscription(IqRoster.Subscription.both);
		iqRoster.addRosterItem(item);
		
		iqResult.addExtension(iqRoster);
		
		userResource.sendToSelfClient(iqResult);
	}

}
