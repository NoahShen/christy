package net.sf.christy.sm.contactmgr;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import net.sf.christy.sm.OnlineUser;
import net.sf.christy.sm.SmManager;
import net.sf.christy.sm.UserResource;
import net.sf.christy.sm.impl.SmManagerImpl;
import net.sf.christy.sm.user.User;
import net.sf.christy.sm.user.UserDbHelper;
import net.sf.christy.sm.user.UserDbHelperTracker;
import net.sf.christy.util.collections.LRULinkedHashMap;
import net.sf.christy.xmpp.Iq;
import net.sf.christy.xmpp.IqRoster;
import net.sf.christy.xmpp.JID;
import net.sf.christy.xmpp.PacketUtils;
import net.sf.christy.xmpp.Presence;
import net.sf.christy.xmpp.XmppError;
import net.sf.christy.xmpp.IqRoster.Item;

/**
 * 
 * @author noah
 *
 */
public class ContactManager
{
	private boolean cacheRoster;
	
	private int cacheSize;
	
	private LRULinkedHashMap<String, IqRoster> rosterCache;
	
	private RosterItemDbHelperTracker rosterItemDbHelperTracker;

	private OfflineSubscribeMsgDbHelperTracker offlineSubscribeMsgDbHelperTracker;
	
	private UserDbHelperTracker userDbHelperTracker;
	
	private final Lock lock = new ReentrantLock();
	
	public ContactManager(RosterItemDbHelperTracker rosterItemDbHelperTracker,
					OfflineSubscribeMsgDbHelperTracker offlineSubscribeMsgDbHelperTracker,
					UserDbHelperTracker userDbHelperTracker)
	{
		cacheRoster = true;
		cacheSize = 1000;
		rosterCache = new LRULinkedHashMap<String, IqRoster>(cacheSize);
		
		this.rosterItemDbHelperTracker = rosterItemDbHelperTracker;
		this.offlineSubscribeMsgDbHelperTracker = offlineSubscribeMsgDbHelperTracker;
		this.userDbHelperTracker = userDbHelperTracker;
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

	public void handlePresence(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		Presence.Type type = presence.getType();
		if (type == Presence.Type.available
				|| type == Presence.Type.unavailable)
		{
			handleStateChanged(smManager, onlineUser, userResource, presence);
		}
		// TODO subscription
		else if (type == Presence.Type.subscribe)
		{
			handleSubscribe(smManager, onlineUser, userResource, presence);
		}
		else if (type == Presence.Type.subscribed)
		{
			handleSubscribed(smManager, onlineUser, userResource, presence);
		}
	}

	private void handleSubscribed(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		// TODO Auto-generated method stub
		
	}

	private void handleSubscribe(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		JID to = presence.getTo();
		JID bareJID = new JID(to.getNode(), to.getDomain(), null);
		String node = onlineUser.getNode();
		try
		{
			IqRoster iqRoster = getIqRoster(node);
			// roster not exist
			if (!iqRoster.containRosterItem(bareJID))
			{
				RosterItem item = new RosterItem();
				item.setUsername(node);
				item.setRosterJID(bareJID);
				item.setSubscription(RosterItem.Subscription.none);
				updateRosterItem(item);
				
				IqRoster.Item newItem = new IqRoster.Item(bareJID);
				newItem.setSubscription(IqRoster.Subscription.none);
				notifyResource(onlineUser, userResource, newItem);

			}
			
			//change to ask
			iqRoster = getIqRoster(node);
			IqRoster.Item oldItem = iqRoster.getRosterItem(bareJID);
			IqRoster.Ask ask = oldItem.getAsk();
			// check ask
			if (ask == null)
			{

				RosterItemDbHelper rosterItemDbHelper = rosterItemDbHelperTracker.getRosterItemDbHelper();
				rosterItemDbHelper.updateRosterItemAsk(node, bareJID, RosterItem.Ask.subscribe);
				
				lock.lock();
				try
				{
					rosterCache.remove(node.toLowerCase());
				}
				finally
				{
					lock.unlock();
				}
				
				IqRoster newRoster = getIqRoster(node);
				IqRoster.Item newItem = newRoster.getRosterItem(bareJID);
				notifyResource(onlineUser, userResource, newItem);
				
				
				presence.setFrom(new JID(onlineUser.getNode(), smManager.getDomain(), null));
				userResource.sendToOtherUser(presence);
			}
			
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	private void handleStateChanged(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		if (presence.getTo() != null)
		{
			Presence presenceError = PacketUtils.createErrorPresence(presence);
			XmppError error = new XmppError(XmppError.Condition.bad_request);
			presenceError.setError(error);
			userResource.sendToSelfClient(presenceError);
			
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
			if (subscription == IqRoster.Subscription.both)
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
		
		if (firstPresence)
		{
			notifyOtherResourceState(smManager, onlineUser, userResource);
			((SmManagerImpl) smManager).fireUserResourceAvailable(onlineUser, userResource);
		}
		
	}

	private void notifyOtherResourceState(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		
		for (UserResource res : onlineUser.getAllUserResources())
		{
			if (res != userResource && res.isAvailable())
			{
				Presence presence = res.getPresence();
				try
				{
					Presence clonePresence = (Presence) presence.clone();
					clonePresence.setFrom(new JID(onlineUser.getNode(), smManager.getDomain(), res.getResource()));
					clonePresence.setTo(null);
					userResource.sendToSelfClient(clonePresence);
				}
				catch (CloneNotSupportedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void handleRoster(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Iq iq)
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
				e.printStackTrace();
				
				Iq iqError = PacketUtils.createErrorIq(iq);
				iqError.setError(new XmppError(XmppError.Condition.internal_server_error));
				userResource.sendToSelfClient(iqError);
			}
			
			

		}
		// add remove update roster
		else if (type == Iq.Type.set)
		{
			IqRoster roster = (IqRoster) iq.getExtension(IqRoster.ELEMENTNAME, IqRoster.NAMESPACE);
			int count = roster.getRosterItemCount();
			if (count != 1)
			{
				Iq iqError = PacketUtils.createErrorIq(iq);
				iqError.setError(new XmppError(XmppError.Condition.bad_request));
				userResource.sendToSelfClient(iqError);
				return;
			}
			
			IqRoster.Item item = roster.getRosterItems().iterator().next();
			RosterItemDbHelper rosterItemDbHelper = rosterItemDbHelperTracker.getRosterItemDbHelper();
			if (item.getSubscription() == IqRoster.Subscription.remove)
			{
				
				try
				{
					removeRosterItem(onlineUser.getNode(), item.getJid());
					IqRoster.Item newItem = new IqRoster.Item(item.getJid());
					newItem.setSubscription(IqRoster.Subscription.remove);
					notifyResource(onlineUser, userResource, newItem);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				
				
			}
			else
			{
				String username = onlineUser.getNode();
				RosterItem newrosterItem = new RosterItem();
				newrosterItem.setUsername(username);
				newrosterItem.setRosterJID(item.getJid());
				newrosterItem.setNickname(item.getName());
				newrosterItem.setGroups(item.getGroupNames().toArray(new String[]{}));
				
				try
				{
					updateRosterItem(newrosterItem);
					
					RosterItem rosterItem = rosterItemDbHelper.getRosterItem(username, item.getJid());
					
					IqRoster.Item newItem = new IqRoster.Item(item.getJid());
					newItem.setName(rosterItem.getNickname());
					newItem.setSubscription(IqRoster.Subscription.valueOf(rosterItem.getSubscription().name()));
					if (rosterItem.getAsk() != null)
					{
						newItem.setAsk(IqRoster.Ask.fromString(rosterItem.getAsk().name()));
					}
					newItem.addGroups(rosterItem.getGroups());
					notifyResource(onlineUser, userResource, newItem);
					
					
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
			
			
			Iq iqResult = PacketUtils.createResultIq(iq);
			userResource.sendToSelfClient(iqResult);

		}
		
	}

	private void notifyResource(OnlineUser onlineUser, UserResource userResource, Item newItem)
	{
		Iq iq = new Iq(Iq.Type.set);
		IqRoster roster = new IqRoster();
		roster.addRosterItem(newItem);
		iq.addExtension(roster);
		for (UserResource res : onlineUser.getAllUserResources())
		{
			iq.setStanzaId(null);
			res.sendToSelfClient(iq);
		}
		
	}

	public IqRoster getIqRoster(String username) throws Exception
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

	public void handleOtherPresence(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		Presence.Type type = presence.getType();
		if (type == Presence.Type.available
				|| type == Presence.Type.unavailable)
		{
			handleOtherStateChanged(smManager, onlineUser, userResource, presence);
		}
		// TODO subscription
		else if (type == Presence.Type.subscribe)
		{
			handleOtherSubscribe(smManager, onlineUser, userResource, presence);
		}
	}

	private void handleOtherSubscribe(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		// user offline
		if (onlineUser == null || onlineUser.getResourceCount() == 0)
		{
			JID to = presence.getTo();
			String username = to.getNode();
			UserDbHelper helper = userDbHelperTracker.getUserDbHelper();
			try
			{
				User user = helper.getUser(username);
				// user not exist
				if (user != null)
				{
					OfflineSubscribeMsgDbHelper msghelper = offlineSubscribeMsgDbHelperTracker.getOfflineSubscribeMsgDbHelper();
					OfflineSubscribeMsg msg = new OfflineSubscribeMsg();
					msg.setUsername(username);
					msg.setFrom(presence.getFrom());
					msg.setExtensions(presence.getExtensionsXML());
					msghelper.addOfflineSubscribeMsg(msg);
				}
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return;

		}
		
		if (userResource != null)
		{
			userResource.sendToSelfClient(presence);
		}
		
		
	}

	private void handleOtherStateChanged(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		if (presence.getFrom() == null)
		{
			return;
		}
		userResource.sendToSelfClient(presence);
	}

	
	private void updateRosterItem(RosterItem rosterItem)
	{
		String username = rosterItem.getUsername();
		RosterItemDbHelper rosterItemDbHelper = rosterItemDbHelperTracker.getRosterItemDbHelper();
		
		
		try
		{
			RosterItem oldRosterItem = rosterItemDbHelper.getRosterItem(username, rosterItem.getRosterJID());
			// add roster
			if (oldRosterItem == null)
			{
				if (rosterItem.getSubscription() == null)
				{
					rosterItem.setSubscription(RosterItem.Subscription.none);
				}
				rosterItemDbHelper.addRosterItem(rosterItem);
			}
			// update roster
			else
			{
				oldRosterItem.setNickname(rosterItem.getNickname());
				if (rosterItem.getSubscription() != null)
				{
					oldRosterItem.setSubscription(rosterItem.getSubscription());
				}
				if (rosterItem.getAsk() != null)
				{
					oldRosterItem.setAsk(rosterItem.getAsk());
				}
				
				oldRosterItem.setGroups(rosterItem.getGroups());
				rosterItemDbHelper.updateRosterItem(rosterItem);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lock.lock();
		try
		{
			rosterCache.remove(username.toLowerCase());
		}
		finally
		{
			lock.unlock();
		}
	}
	
	private void removeRosterItem(String username, JID rosterJID)
	{
		RosterItemDbHelper rosterItemDbHelper = rosterItemDbHelperTracker.getRosterItemDbHelper();
		try
		{
			rosterItemDbHelper.removeRosterItem(username, rosterJID);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lock.lock();
		try
		{
			rosterCache.remove(username.toLowerCase());
		}
		finally
		{
			lock.unlock();
		}
	}

}
