package net.sf.christy.sm.contactmgr;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.christy.sm.PacketHandler;
import net.sf.christy.sm.UserResource;
import net.sf.christy.util.collections.LRULinkedHashMap;
import net.sf.christy.xmpp.Iq;
import net.sf.christy.xmpp.IqRoster;
import net.sf.christy.xmpp.Packet;
import net.sf.christy.xmpp.Presence;

/**
 * 
 * @author noah
 *
 */
public class ContactManager implements PacketHandler
{
	private boolean cacheRoster;
	
	private int cacheSize;
	
	private LRULinkedHashMap<String, IqRoster> rosterCache;
	
	private RosterItemDbHelperTracker rosterItemDbHelperTracker;

	private final Lock lock = new ReentrantLock();
	
	public ContactManager(RosterItemDbHelperTracker rosterItemDbHelperTracker)
	{
		cacheRoster = true;
		cacheSize = 1000;
		rosterCache = new LRULinkedHashMap<String, IqRoster>(cacheSize);
		
		this.rosterItemDbHelperTracker = rosterItemDbHelperTracker;
	}

	public boolean isCacheRoster()
	{
		return cacheRoster;
	}

	public void setCacheRoster(boolean cacheRoster)
	{
		this.cacheRoster = cacheRoster;
		if (!cacheRoster)
		{
			rosterCache.clear();
		}
	}

	public int getCacheSize()
	{
		return cacheSize;
	}

	public void setCacheSize(int cacheSize)
	{
		this.cacheSize = cacheSize;
		rosterCache.clear();
		rosterCache = new LRULinkedHashMap<String, IqRoster>(cacheSize);
	}

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
		// TODO check stanza's "to" attribute
		
	}

	private void handleRoster(UserResource userResource, Iq iq)
	{
		Iq.Type type = iq.getType();
		if (type == Iq.Type.get)
		{
			Iq iqResult = new Iq(Iq.Type.result);
			iqResult.setStanzaId(iq.getStanzaId());
			
			String username =  userResource.getOnlineUser().getNode();
			IqRoster iqRoster = getIqRoster(username);
			
			iqResult.addExtension(iqRoster);
			
			userResource.sendToSelfClient(iqResult);

		}
		
	}

	private IqRoster getIqRoster(String username)
	{
		if (isCacheRoster())
		{
			lock.lock();
			try
			{
				if (rosterCache.containsKey(username.toLowerCase()))
				{
					return rosterCache.get(username.toLowerCase());
				}
			}
			finally
			{
				lock.unlock();
			}
			
		}
		
		
		IqRoster iqRoster = new IqRoster();
		
		RosterItemDbHelper rosterItemDbHelper = 
			rosterItemDbHelperTracker.getRosterItemDbHelper();
		
		RosterItem[] rosterItems = rosterItemDbHelper.getRosterItems(username);
		if (rosterItems != null)
		{
			for (RosterItem rosterItem : rosterItems)
			{
				IqRoster.Item item = new IqRoster.Item(rosterItem.getRosterJID());
				item.setName(rosterItem.getNickname());
				if (rosterItem.getAsk() != null)
				{
					item.setAsk(IqRoster.Ask.fromString(rosterItem.getAsk().name()));
				}
				if (rosterItem.getSubscription() != null)
				{
					item.setSubscription(IqRoster.Subscription.valueOf(rosterItem.getSubscription().name()));
				}
				
				String[] groups = rosterItem.getGroups();
				if (groups != null)
				{
					for (String group : groups)
					{
						item.addGroupName(group);
					}
				}
				iqRoster.addRosterItem(item);
			}
		}
		
		lock.lock();
		try
		{
			rosterCache.put(username.toLowerCase(), iqRoster);
		}
		finally
		{
			lock.unlock();
		}
		
		return iqRoster;
	}

}
