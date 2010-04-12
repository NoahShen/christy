package com.google.code.christy.sm.contactmgr;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.UserResource;
import com.google.code.christy.sm.impl.SmManagerImpl;
import com.google.code.christy.sm.user.User;
import com.google.code.christy.sm.user.UserDbHelper;
import com.google.code.christy.sm.user.UserDbHelperTracker;
import com.google.code.christy.util.collections.LRULinkedHashMap;
import com.google.code.christy.xmpp.Iq;
import com.google.code.christy.xmpp.IqRoster;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.PacketUtils;
import com.google.code.christy.xmpp.Presence;
import com.google.code.christy.xmpp.XmppError;
import com.google.code.christy.xmpp.IqRoster.Item;


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
	
	public UserResource[] checkResource(SmManager smManager, OnlineUser onlineUser, Packet packet)
	{
		if (onlineUser != null)
		{
			return onlineUser.getAllActiveUserResources();
		}
		return new UserResource[]{null};
	}

	public void handlePresence(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		Presence.Type type = presence.getType();
		if (type == Presence.Type.available
				|| type == Presence.Type.unavailable)
		{
			handleStateChanged(smManager, onlineUser, userResource, presence);
		}
		else if (type == Presence.Type.subscribe)
		{
			handleSubscribe(smManager, onlineUser, userResource, presence);
		}
		else if (type == Presence.Type.subscribed)
		{
			handleSubscribed(smManager, onlineUser, userResource, presence);
		}
		else if (type == Presence.Type.unsubscribed)
		{
			handleUnsubscribed(smManager, onlineUser, userResource, presence);
		}
		else if (type == Presence.Type.unsubscribe)
		{
			handleUnsubscribe(smManager, onlineUser, userResource, presence);
		}
	}

	private void handleUnsubscribe(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		JID to = presence.getTo();
		if (to == null || to.getResource() != null)
		{
			Presence presenceError = PacketUtils.createErrorPresence(presence);
			XmppError error = new XmppError(XmppError.Condition.bad_request);
			presenceError.setError(error);
			userResource.sendToSelfClient(presenceError);
			return;
		}
		
		String username = onlineUser.getNode();
		

		try
		{
			IqRoster iqRoster = getIqRoster(username);
			IqRoster.Item iqRosterItem = iqRoster.getRosterItem(to);
			if (iqRosterItem == null)
			{
				return;
			}
			
			RosterItem item = new RosterItem();
			item.setUsername(username);
			item.setRosterJID(to);
			item.setNickname(iqRosterItem.getName());
			item.setGroups(iqRosterItem.getGroupNames());
			
			IqRoster.Subscription subs = iqRosterItem.getSubscription();
			IqRoster.Ask ask = iqRosterItem.getAsk();
			item.setAsk(ask == null ? null : RosterItem.Ask.valueOf(ask.toString()));
			
			if (subs == IqRoster.Subscription.to || subs == IqRoster.Subscription.both)
			{
				if (subs == IqRoster.Subscription.to)
				{
					iqRosterItem.setSubscription(IqRoster.Subscription.none);
					item.setSubscription(RosterItem.Subscription.none);
				}
				else
				{
					iqRosterItem.setSubscription(IqRoster.Subscription.from);
					item.setSubscription(RosterItem.Subscription.from);
				}
				
				
				updateRosterItem(item);

				notifyResourceRosterChanged(onlineUser, userResource, iqRosterItem);
				
				presence.setFrom(new JID(username, smManager.getDomain(), null));
				userResource.sendToOtherUser(presence);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleUnsubscribed(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		JID to = presence.getTo();
		if (to == null || to.getResource() != null)
		{
			Presence presenceError = PacketUtils.createErrorPresence(presence);
			XmppError error = new XmppError(XmppError.Condition.bad_request);
			presenceError.setError(error);
			userResource.sendToSelfClient(presenceError);
			return;
		}
		
		String username = onlineUser.getNode();
		
		try
		{
			IqRoster iqRoster = getIqRoster(username);
			IqRoster.Item iqRosterItem = iqRoster.getRosterItem(to);
			if (iqRosterItem == null)
			{
				return;
			}
			
			RosterItem item = new RosterItem();
			item.setUsername(username);
			item.setRosterJID(to);
			item.setNickname(iqRosterItem.getName());
			item.setGroups(iqRosterItem.getGroupNames());
			
			IqRoster.Subscription subs = iqRosterItem.getSubscription();
			IqRoster.Ask ask = iqRosterItem.getAsk();
			item.setAsk(ask == null ? null : RosterItem.Ask.valueOf(ask.toString()));
			
			if (subs == IqRoster.Subscription.from || subs == IqRoster.Subscription.both)
			{
				if (subs == IqRoster.Subscription.from)
				{
					iqRosterItem.setSubscription(IqRoster.Subscription.none);
					item.setSubscription(RosterItem.Subscription.none);
				}
				else
				{
					iqRosterItem.setSubscription(IqRoster.Subscription.to);
					item.setSubscription(RosterItem.Subscription.to);
				}
				
				
				updateRosterItem(item);

				notifyResourceRosterChanged(onlineUser, userResource, iqRosterItem);
				
				presence.setFrom(new JID(username, smManager.getDomain(), null));
				userResource.sendToOtherUser(presence);
				
				notifyAllResourceUnavailableState(smManager, onlineUser, userResource, to);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void handleSubscribed(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		JID to = presence.getTo();
		if (to == null || to.getResource() != null)
		{
			Presence presenceError = PacketUtils.createErrorPresence(presence);
			XmppError error = new XmppError(XmppError.Condition.bad_request);
			presenceError.setError(error);
			userResource.sendToSelfClient(presenceError);
			return;
		}
		
		String username = onlineUser.getNode();
		
		try
		{
			IqRoster iqRoster = getIqRoster(username);
			IqRoster.Item iqRosterItem = iqRoster.getRosterItem(to);
			if (iqRosterItem == null)
			{
				iqRosterItem = new IqRoster.Item(to);
				iqRosterItem.setSubscription(IqRoster.Subscription.none);
			}
			RosterItem item = new RosterItem();
			item.setUsername(username);
			item.setRosterJID(to);
			item.setNickname(iqRosterItem.getName());
			item.setGroups(iqRosterItem.getGroupNames());
			
			IqRoster.Subscription subs = iqRosterItem.getSubscription();
			IqRoster.Ask ask = iqRosterItem.getAsk();
			item.setAsk(ask == null ? null : RosterItem.Ask.valueOf(ask.toString()));
			
			if (subs == IqRoster.Subscription.none || subs == IqRoster.Subscription.to)
			{
				if (subs == IqRoster.Subscription.none)
				{
					iqRosterItem.setSubscription(IqRoster.Subscription.from);
					item.setSubscription(RosterItem.Subscription.from);
				}
				else
				{
					iqRosterItem.setSubscription(IqRoster.Subscription.both);
					item.setSubscription(RosterItem.Subscription.both);
				}
				
				
				updateRosterItem(item);

				notifyResourceRosterChanged(onlineUser, userResource, iqRosterItem);
				
				presence.setFrom(new JID(username, smManager.getDomain(), null));
				userResource.sendToOtherUser(presence);
				
				notifyAllResourceState(onlineUser, userResource, to);
				
			}
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void notifyAllResourceState(OnlineUser onlineUser, UserResource userResource, JID rosterJID)
	{
		for (UserResource res : onlineUser.getAllActiveUserResources())
		{
			Presence statePresence = res.getPresence();
			try
			{
				Presence clonePresence = (Presence) statePresence.clone();
				clonePresence.setTo(rosterJID);
				clonePresence.setStanzaId(null);
				res.sendToOtherUser(clonePresence);
			}
			catch (CloneNotSupportedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
				notifyResourceRosterChanged(onlineUser, userResource, newItem);

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
					rosterCache.remove(node);
				}
				finally
				{
					lock.unlock();
				}
				
				IqRoster newRoster = getIqRoster(node);
				IqRoster.Item newItem = newRoster.getRosterItem(bareJID);
				notifyResourceRosterChanged(onlineUser, userResource, newItem);
				
				
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
			userResource.sendToOtherUser(presence);
			return;
		}
		
		String username = userResource.getOnlineUser().getNode();
		try
		{
			IqRoster roster = getIqRoster(username);

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
			
			sendPresenceToOtherResource(smManager, onlineUser, userResource, toRosterPresence);
			
			if (firstPresence)
			{
				notifyOtherResourceState(smManager, onlineUser, userResource);
				((SmManagerImpl) smManager).fireUserResourceAvailable(onlineUser, userResource);
			}
			
		}
		catch (Exception e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	private void sendPresenceToOtherResource(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence toPresence)
	{
		for (UserResource res : onlineUser.getAllActiveUserResources())
		{
			if (res != userResource)
			{
				try
				{
					Presence clonePresence = (Presence) toPresence.clone();
					clonePresence.setFrom(new JID(onlineUser.getNode(), smManager.getDomain(), userResource.getResource()));
					clonePresence.setTo(new JID(onlineUser.getNode(), smManager.getDomain(), res.getResource()));
					res.sendToSelfClient(clonePresence);
				}
				catch (CloneNotSupportedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private void notifyOtherResourceState(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		
		for (UserResource res : onlineUser.getAllActiveUserResources())
		{
			if (res != userResource)
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
					notifyResourceRosterChanged(onlineUser, userResource, newItem);
					
					Presence unsubscribePresence = new Presence(Presence.Type.unsubscribe);
					unsubscribePresence.setFrom(new JID(onlineUser.getNode(), smManager.getDomain(), null));
					unsubscribePresence.setTo(item.getJid());
					userResource.sendToOtherUser(unsubscribePresence);
					
					Presence unsubscribedPresence = new Presence(Presence.Type.unsubscribed);
					unsubscribedPresence.setFrom(new JID(onlineUser.getNode(), smManager.getDomain(), null));
					unsubscribedPresence.setTo(item.getJid());
					userResource.sendToOtherUser(unsubscribedPresence);
					
					notifyAllResourceUnavailableState(smManager, onlineUser, userResource, item.getJid());
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
				newrosterItem.setGroups(item.getGroupNames());
				newrosterItem.setSubscription(RosterItem.Subscription.valueOf(item.getSubscription().name()));
				if (item.getAsk() != null)
				{
					newrosterItem.setAsk(RosterItem.Ask.valueOf(item.getAsk().toString()));
				}
				
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
					notifyResourceRosterChanged(onlineUser, userResource, newItem);
					
					
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

	private void notifyAllResourceUnavailableState(SmManager smManager, OnlineUser onlineUser, UserResource userResource, JID rosterJID)
	{
		Presence presence = new Presence(Presence.Type.unavailable);
		presence.setTo(rosterJID);
		for (UserResource res : onlineUser.getAllActiveUserResources())
		{
			presence.setFrom(new JID(onlineUser.getNode(), smManager.getDomain(), res.getResource()));
			presence.setStanzaId(null);
			res.sendToOtherUser(presence);
		}
			
	}

	private void notifyResourceRosterChanged(OnlineUser onlineUser, UserResource userResource, Item newItem)
	{
		Iq iq = new Iq(Iq.Type.set);
		IqRoster roster = new IqRoster();
		roster.addRosterItem(newItem);
		iq.addExtension(roster);
		for (UserResource res : onlineUser.getAllActiveUserResources())
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
				if (rosterCache.containsKey(username))
				{
					return rosterCache.get(username);
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
			rosterCache.put(username, iqRoster);
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
		else if (type == Presence.Type.subscribe)
		{
			handleOtherSubscribe(smManager, onlineUser, userResource, presence);
		}
		else if (type == Presence.Type.subscribed)
		{
			handleOtherSubscribed(smManager, onlineUser, userResource, presence);
		}
		else if (type == Presence.Type.unsubscribed)
		{
			handleOtherUnsubscribed(smManager, onlineUser, userResource, presence);
		}
		else if (type == Presence.Type.unsubscribe)
		{
			handleOtherUnsubscribe(smManager, onlineUser, userResource, presence);
		}
		else if (type == Presence.Type.probe)
		{
			handleOtherProbe(smManager, onlineUser, userResource, presence);
		}
	}

	private void handleOtherProbe(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		if (onlineUser == null 
				|| onlineUser.getResourceCount() == 0
				|| userResource == null
				|| !userResource.isAvailable())
		{
			return;
		}
		
		JID from = presence.getFrom();
		String username = onlineUser.getNode();
		
		try
		{
			IqRoster iqRoster = getIqRoster(username);
			IqRoster.Item item = iqRoster.getRosterItem(from);
			if (item != null)
			{
				IqRoster.Subscription subs = item.getSubscription();
				if (subs == IqRoster.Subscription.from 
						|| subs == IqRoster.Subscription.both)
				{
					Presence resPresence = userResource.getPresence();
					if (resPresence != null)
					{
						Presence clonePresence = (Presence) resPresence.clone();
						clonePresence.setStanzaId(null);
						clonePresence.setFrom(new JID(username, smManager.getDomain(), userResource.getResource()));
						clonePresence.setTo(from);
						userResource.sendToOtherUser(clonePresence);
					}
				}
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private void handleOtherUnsubscribe(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		JID to = presence.getTo();
		JID from = presence.getFrom();
		
		String username = to.getNode();
		
		try
		{
			IqRoster iqRoster = getIqRoster(username);
			IqRoster.Item item = iqRoster.getRosterItem(from);
			if (item == null)
			{
				return;
			}
			
			IqRoster.Subscription subs = item.getSubscription();
			if (subs == IqRoster.Subscription.from || subs == IqRoster.Subscription.both)
			{
				RosterItem rosterItem = new RosterItem();
				rosterItem.setUsername(username);
				rosterItem.setNickname(item.getName());
				rosterItem.setRosterJID(item.getJid());
				rosterItem.setAsk(null);
				if (subs == IqRoster.Subscription.from)
				{
					item.setSubscription(IqRoster.Subscription.none);
					rosterItem.setSubscription(RosterItem.Subscription.none);
				}
				else
				{
					item.setSubscription(IqRoster.Subscription.to);
					rosterItem.setSubscription(RosterItem.Subscription.to);
				}
				rosterItem.setGroups(item.getGroupNames());
				updateRosterItem(rosterItem);
				
				
				if (onlineUser != null && onlineUser.getResourceCount() != 0)
				{
					notifyResourceRosterChanged(onlineUser, userResource, item);
					notifyAllResourceUnavailableState(smManager, onlineUser, userResource, from);
				}

			}
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void handleOtherUnsubscribed(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		JID to = presence.getTo();
		JID from = presence.getFrom();
		
		String username = to.getNode();
		
		
		try
		{
			IqRoster iqRoster = getIqRoster(username);
			IqRoster.Item item = iqRoster.getRosterItem(from);
			if (item == null)
			{
				return;
			}
			
			IqRoster.Subscription subs = item.getSubscription();
			if (subs == IqRoster.Subscription.to || subs == IqRoster.Subscription.both)
			{
				RosterItem rosterItem = new RosterItem();
				rosterItem.setUsername(username);
				rosterItem.setNickname(item.getName());
				rosterItem.setRosterJID(item.getJid());
				rosterItem.setAsk(null);
				if (subs == IqRoster.Subscription.to)
				{
					item.setSubscription(IqRoster.Subscription.none);
					rosterItem.setSubscription(RosterItem.Subscription.none);
				}
				else
				{
					item.setSubscription(IqRoster.Subscription.from);
					rosterItem.setSubscription(RosterItem.Subscription.from);
				}
				rosterItem.setGroups(item.getGroupNames());
				updateRosterItem(rosterItem);
				
				
				if (onlineUser != null && onlineUser.getResourceCount() != 0)
				{
					notifyResourceRosterChanged(onlineUser, userResource, item);
				}
			}
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void handleOtherSubscribed(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Presence presence)
	{
		JID to = presence.getTo();
		JID from = presence.getFrom();
		
		String username = to.getNode();
		
		try
		{
			IqRoster iqRoster = getIqRoster(username);
			IqRoster.Item item = iqRoster.getRosterItem(from);
			if (item == null)
			{
				return;
			}
			
			IqRoster.Subscription subs = item.getSubscription();
			IqRoster.Ask ask = item.getAsk();
			if ((subs == IqRoster.Subscription.none || subs == IqRoster.Subscription.from)
					&& ask == IqRoster.Ask.SUBSCRIPTION_PENDING)
			{
				RosterItem rosterItem = new RosterItem();
				rosterItem.setUsername(username);
				rosterItem.setNickname(item.getName());
				rosterItem.setRosterJID(item.getJid());
				rosterItem.setAsk(null);
				if (subs == IqRoster.Subscription.none)
				{
					item.setSubscription(IqRoster.Subscription.to);
					rosterItem.setSubscription(RosterItem.Subscription.to);
				}
				else
				{
					item.setSubscription(IqRoster.Subscription.both);
					rosterItem.setSubscription(RosterItem.Subscription.both);
				}
				rosterItem.setGroups(item.getGroupNames());
				updateRosterItem(rosterItem);
				
				
				if (onlineUser != null && onlineUser.getResourceCount() != 0)
				{
					notifyResourceRosterChanged(onlineUser, userResource, item);
				}
			}
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		if (onlineUser == null || userResource == null)
		{
			return;
		}
		
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
				oldRosterItem.setSubscription(rosterItem.getSubscription());
				oldRosterItem.setAsk(rosterItem.getAsk());
				
				oldRosterItem.setGroups(rosterItem.getGroups());
				rosterItemDbHelper.updateRosterItem(oldRosterItem);
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
			rosterCache.remove(username);
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
			rosterCache.remove(username);
		}
		finally
		{
			lock.unlock();
		}
	}

}
