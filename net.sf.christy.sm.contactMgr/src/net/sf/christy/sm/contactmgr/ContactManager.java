package net.sf.christy.sm.contactmgr;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.christy.sm.OnlineUser;
import net.sf.christy.sm.PacketHandler;
import net.sf.christy.sm.SmManager;
import net.sf.christy.sm.UserResource;
import net.sf.christy.util.collections.LRULinkedHashMap;
import net.sf.christy.xmpp.Iq;
import net.sf.christy.xmpp.IqRoster;
import net.sf.christy.xmpp.JID;
import net.sf.christy.xmpp.Packet;
import net.sf.christy.xmpp.Presence;
import net.sf.christy.xmpp.XmppError;

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
	public boolean accept(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
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
	public void handleClientPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		
		if (packet instanceof Iq)
		{
			handleRoster(smManager, onlineUser, userResource, (Iq) packet);
		}
		else if (packet instanceof Presence)
		{
			handlePresence(smManager, onlineUser, userResource, (Presence) packet);
		}
		
	}

	private void handlePresence(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		Presence.Type type = presence.getType();
		if (type == Presence.Type.available
				|| type == Presence.Type.unavailable)
		{
			handleStateChanged(smManager, onlineUser, userResource, presence);
		}
		// TODO subscription
	}

	private void handleStateChanged(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		if (presence.getTo() != null)
		{
			Presence presenceError;
			try
			{
				presenceError = (Presence) presence.clone();
				presenceError.setType(Presence.Type.error);
				presenceError.setFrom(null);
				presenceError.setTo(presence.getFrom());
				XmppError error = new XmppError(XmppError.Condition.bad_request);
				presenceError.setError(error);
				userResource.sendToSelfClient(presenceError);
			}
			catch (CloneNotSupportedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;
			
		}
		
		String username = userResource.getOnlineUser().getNode().toLowerCase();
		IqRoster roster = null;
		try
		{
			roster = getIqRoster(username);
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		boolean firstPresence = false;
		if (userResource.getPresence() == null || !userResource.getPresence().isAvailable())
		{
			firstPresence = true;
		}
		
		userResource.setPresence(presence);
		
		Presence toRosterPresence = null;
		try
		{
			toRosterPresence = (Presence) presence.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		
		for (IqRoster.Item item : roster.getRosterItems())
		{
			IqRoster.Subscription subscription = item.getSubscription();
			if (subscription == IqRoster.Subscription.both
					|| subscription == IqRoster.Subscription.from)
			{
				toRosterPresence.setTo(item.getJid());
				userResource.sendToOtherUser(toRosterPresence);
				
				if (firstPresence)
				{
					Presence presenceProbe = new Presence(Presence.Type.probe);
					presenceProbe.setFrom(new JID(onlineUser.getNode(), smManager.getDomain(), userResource.getResource()));
					presenceProbe.setTo(item.getJid());
					
					userResource.sendToOtherUser(presenceProbe);
				}
				
			}
			else if (subscription == IqRoster.Subscription.from)
			{
				toRosterPresence.setTo(item.getJid());
				userResource.sendToOtherUser(toRosterPresence);
			}
			else if (subscription == IqRoster.Subscription.to)
			{
				if (firstPresence)
				{
					Presence presenceProbe = new Presence(Presence.Type.probe);
					presenceProbe.setFrom(new JID(onlineUser.getNode(), smManager.getDomain(), userResource.getResource()));
					presenceProbe.setTo(item.getJid());
					
					userResource.sendToOtherUser(presenceProbe);
				}
				
			}
		}
		
	}

	private void handleRoster(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Iq iq)
	{
		Iq.Type type = iq.getType();
		if (type == Iq.Type.get)
		{
			
			
			String username =  onlineUser.getNode();
			IqRoster iqRoster = null;
			try
			{
				iqRoster = getIqRoster(username);
				Iq iqResult = new Iq(Iq.Type.result);
				iqResult.setStanzaId(iq.getStanzaId());
				
				iqResult.addExtension(iqRoster);
				
				userResource.sendToSelfClient(iqResult);
				
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
//				e.printStackTrace();
				
				Iq iqError = null;
				try
				{
					iqError = (Iq) iq.clone();
				}
				catch (CloneNotSupportedException e1)
				{
					// TODO Auto-generated catch block
//					e1.printStackTrace();
					return;
				}
				iqError.setType(Iq.Type.error);
				iqError.setFrom(new JID(null, smManager.getDomain(), null));
				iqError.setTo(iq.getFrom());
				
				userResource.sendToSelfClient(iqError);
			}
			
			

		}
		// TODO add remove update roster
		
	}

	private IqRoster getIqRoster(String username) throws Exception
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

	@Override
	public void handleOtherUserPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		if (packet instanceof Presence)
		{
			handleOtherPresence(smManager, onlineUser, userResource, (Presence) packet);
		}
	}

	private void handleOtherPresence(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		Presence.Type type = presence.getType();
		if (type == Presence.Type.available
				|| type == Presence.Type.unavailable)
		{
			handleOtherStateChanged(smManager, onlineUser, userResource, presence);
		}
		// TODO subscription
	}

	private void handleOtherStateChanged(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		if (presence.getFrom() == null)
		{
			// TODO
			return;
		}
		userResource.sendToSelfClient(presence);
	}


}
