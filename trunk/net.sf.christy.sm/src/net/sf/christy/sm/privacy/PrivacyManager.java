/**
 * 
 */
package net.sf.christy.sm.privacy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.sm.OnlineUser;
import net.sf.christy.sm.UserResource;
import net.sf.christy.sm.impl.OnlineUserImpl;
import net.sf.christy.sm.impl.UserResourceImpl;
import net.sf.christy.xmpp.Iq;
import net.sf.christy.xmpp.Packet;
import net.sf.christy.xmpp.PacketUtils;
import net.sf.christy.xmpp.Privacy;
import net.sf.christy.xmpp.PrivacyItem;
import net.sf.christy.xmpp.PrivacyList;
import net.sf.christy.xmpp.XmppError;

/**
 * @author Noah
 * 
 */
public class PrivacyManager
{


	private UserPrivacyListDbHelperTracker userPrivacyListDbHelperTracker;

	public PrivacyManager(UserPrivacyListDbHelperTracker userPrivacyListDbHelperTracker)
	{
		super();
		this.userPrivacyListDbHelperTracker = userPrivacyListDbHelperTracker;
	}
	
	public void handlePrivacy(OnlineUserImpl onlineUser, UserResourceImpl userResource, RouteMessage routeMessage, Iq iq, Privacy privacy)
	{
		if (userResource == null)
		{
			return;
		}
		
		Iq.Type type = iq.getType();
		if (type == Iq.Type.get)
		{
			handleGet(onlineUser, userResource, routeMessage, iq, privacy);
		}
		else if (type == Iq.Type.set)
		{
			handelSet(onlineUser, userResource, routeMessage, iq, privacy);
		}
		
	}

	private void handelSet(OnlineUserImpl onlineUser, UserResourceImpl userResource, RouteMessage routeMessage, Iq iq, Privacy privacy)
	{
		String node = onlineUser.getNode();
		// decline active list
		if (privacy.isDeclineActiveList())
		{
			userResource.setActivePrivacyList(null);
		}
		// set active list
		else if (privacy.getActiveName() != null)
		{
			String activeName = privacy.getActiveName();
			PrivacyList oldActiveList = userResource.getActivePrivacyList();
			if (oldActiveList == null
					|| !activeName.equals(oldActiveList.getListName()))
			{

				UserPrivacyListDbHelper dbHelper = userPrivacyListDbHelperTracker.getUserPrivacyListDbHelper();
				try
				{
					UserPrivacyList userPrivacyList = dbHelper.getUserPrivacyList(node, activeName);
					if (userPrivacyList == null)
					{
						Iq iqError = PacketUtils.createErrorIq(iq);
						iqError.setError(new XmppError(XmppError.Condition.item_not_found));
						userResource.sendToSelfClient(iqError);
						return;						
					}
					
					PrivacyList activePrivacyList = userPrivacyListToPrivacyList(userPrivacyList);
					userResource.setActivePrivacyList(activePrivacyList);
					
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		// decline default list
		else if (privacy.isDeclineDefaultList())
		{
			// conflict~!
			if (onlineUser.getResourceCount() > 1)
			{
				Iq iqError = PacketUtils.createErrorIq(iq);
				iqError.setError(new XmppError(XmppError.Condition.conflict));
				userResource.sendToSelfClient(iqError);
				return;
			}
			
			
			UserPrivacyListDbHelper dbHelper = userPrivacyListDbHelperTracker.getUserPrivacyListDbHelper();
			try
			{
				dbHelper.cancelDefaultPrivacyList(node);
				onlineUser.setDefaultPrivacyList(null);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// set default list
		else if (privacy.getDefaultName() != null)
		{
			// conflict~!
			if (onlineUser.getResourceCount() > 1)
			{
				Iq iqError = PacketUtils.createErrorIq(iq);
				iqError.setError(new XmppError(XmppError.Condition.conflict));
				userResource.sendToSelfClient(iqError);
				return;
			}
			
			String defaultName = privacy.getDefaultName();
			PrivacyList oldDefaultPrivacyList = onlineUser.getDefaultPrivacyList();
			if (oldDefaultPrivacyList == null
					|| defaultName.equals(oldDefaultPrivacyList.getListName()))
			{

				UserPrivacyListDbHelper dbHelper = userPrivacyListDbHelperTracker.getUserPrivacyListDbHelper();
				try
				{
					dbHelper.setDefaultPrivacyList(node, defaultName);
					UserPrivacyList userPrivacyList = dbHelper.getUserPrivacyList(node, defaultName);
					if (userPrivacyList == null)
					{
						Iq iqError = PacketUtils.createErrorIq(iq);
						iqError.setError(new XmppError(XmppError.Condition.item_not_found));
						userResource.sendToSelfClient(iqError);
						return;
					}
					
					
					PrivacyList defaultList = userPrivacyListToPrivacyList(userPrivacyList);
					onlineUser.setDefaultPrivacyList(defaultList);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
		Collection<PrivacyList> privacyLists = privacy.getPrivacyLists();
		if (privacyLists.isEmpty()
				|| privacyLists.size() > 1)
		{
			Iq iqError = PacketUtils.createErrorIq(iq);
			iqError.setError(new XmppError(XmppError.Condition.bad_request));
			userResource.sendToSelfClient(iqError);
			return;
		}
		
		PrivacyList privacyList = privacyLists.iterator().next();
		if (isConflict(onlineUser, userResource, privacyList.getListName()))
		{
			Iq iqError = PacketUtils.createErrorIq(iq);
			iqError.setError(new XmppError(XmppError.Condition.conflict));
			userResource.sendToSelfClient(iqError);
			return;
		}
		
		UserPrivacyList userPrivacyList = privacyListToUserPrivacyList(node, privacyList);
		UserPrivacyListDbHelper dbHelper = userPrivacyListDbHelperTracker.getUserPrivacyListDbHelper();
		try
		{
			dbHelper.updateUserPrivacyList(userPrivacyList);
			
			notifyOtherResource(onlineUser, userResource, privacyList.getListName());
			// return result-iq
			Iq iqResult = PacketUtils.createResultIq(iq);
			userResource.sendToSelfClient(iqResult);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}


	private void notifyOtherResource(OnlineUserImpl onlineUser, UserResourceImpl userResource, String listName)
	{
		Iq iq = new Iq(Iq.Type.set);
		Privacy privacy = new Privacy();
		privacy.addPrivacyList(new PrivacyList(listName));
		iq.addExtension(privacy);
		
		for (UserResource res : onlineUser.getAllUserResources())
		{
			if (res != userResource)
			{
				res.sendToSelfClient(iq);
			}
		}
	}

	private UserPrivacyList privacyListToUserPrivacyList(String username, PrivacyList privacyList)
	{
		UserPrivacyList userPrivacyList = new UserPrivacyList();
		userPrivacyList.setUsername(username);
		userPrivacyList.setPrivacyName(privacyList.getListName());
		
		List<UserPrivacyListItem> userPItems = new ArrayList<UserPrivacyListItem>();
		for (PrivacyItem item : privacyList.getItems())
		{
			UserPrivacyListItem userPItem = new UserPrivacyListItem();
			userPItem.setUsername(username);
			userPItem.setPrivacyName(privacyList.getListName());
			PrivacyItem.Type type = item.getType();
			if (type != null)
			{
				userPItem.setType(UserPrivacyListItem.Type.valueOf(type.name()));
			}
			userPItem.setValue(item.getValue());
			if (item.isAction())
			{
				userPItem.setAction(UserPrivacyListItem.Action.allow);
			}
			else
			{
				userPItem.setAction(UserPrivacyListItem.Action.deny);
			}
			userPItem.setOrder(item.getOrder());
			userPItem.setIqFilter(item.isFilterIQ());
			userPItem.setMessageFilter(item.isFilterMessage());
			userPItem.setPresenceInFilter(item.isFilterPresence_in());
			userPItem.setPresenceOutFilter(item.isFilterPresence_out());
			
			userPItems.add(userPItem);
		}
		
		userPrivacyList.setItems(userPItems);
		
		return userPrivacyList;
	}

	
	private boolean isConflict(OnlineUserImpl onlineUser, UserResourceImpl userResource, String listName)
	{
		PrivacyList defaultPrivacyList = onlineUser.getDefaultPrivacyList();
		if (defaultPrivacyList != null && listName.equals(defaultPrivacyList.getListName()))
		{
			return true;
		}
		
		for (UserResource res : onlineUser.getAllUserResources())
		{
			PrivacyList activeList = res.getActivePrivacyList();
			if (activeList != null && listName.equals(activeList.getListName()))
			{
				return true;
			}
		}
		return false;
	}

	private void handleGet(OnlineUser onlineUser, UserResourceImpl userResource, RouteMessage routeMessage, Iq iq, Privacy privacy)
	{
		String node = onlineUser.getNode();
		Collection<PrivacyList> privacyLists = privacy.getPrivacyLists();
		// get all privacy list
		if (privacyLists.isEmpty())
		{
			UserPrivacyListDbHelper dbHelper = userPrivacyListDbHelperTracker.getUserPrivacyListDbHelper();
			try
			{
				UserPrivacyList[] lists = dbHelper.getUserPrivacyLists(node);
				Iq resultIq = PacketUtils.createResultIq(iq);
				Privacy privacyResult = new Privacy();
				
				PrivacyList activelist = userResource.getActivePrivacyList();
				if (activelist != null)
				{					
					privacyResult.setActiveName(activelist.getListName());
				}
				for (UserPrivacyList list : lists)
				{
					if (list.isDefaultList())
					{
						privacyResult.setDefaultName(list.getPrivacyName());
					}
					PrivacyList privacyList = new PrivacyList(list.getPrivacyName());
					privacyResult.addPrivacyList(privacyList);
					
				}
				
				resultIq.addExtension(privacyResult);
				userResource.sendToSelfClient(resultIq);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// get specified group
		else if (privacyLists.size() == 1)
		{			
			PrivacyList requestList = privacyLists.iterator().next();
			String privacyListName = requestList.getListName();
			UserPrivacyListDbHelper dbHelper = userPrivacyListDbHelperTracker.getUserPrivacyListDbHelper();
			Iq resultIq = PacketUtils.createResultIq(iq);
			Privacy privacyResult = new Privacy();
			
			try
			{
				UserPrivacyList userPrivacyList = dbHelper.getUserPrivacyList(node, privacyListName);
				PrivacyList privacyList = userPrivacyListToPrivacyList(userPrivacyList);
				privacyResult.addPrivacyList(privacyList);
				resultIq.addExtension(privacyResult);
				userResource.sendToSelfClient(resultIq);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//Client attempts to retrieve more than one list
		else
		{
			Iq iqError = PacketUtils.createErrorIq(iq);
			XmppError error = new XmppError(XmppError.Condition.bad_request);
			iqError.setError(error);
			userResource.sendToSelfClient(iqError);
		}
	}

	
	private PrivacyList userPrivacyListToPrivacyList(UserPrivacyList userPrivacylist)
	{
		PrivacyList privacyList = new PrivacyList(userPrivacylist.getPrivacyName());
		privacyList.setActiveList(userPrivacylist.isActiveList());
		privacyList.setDefaultList(userPrivacylist.isDefaultList());
		
		for (UserPrivacyListItem usrPItem : userPrivacylist.getItems())
		{
			UserPrivacyListItem.Action action = usrPItem.getAction();
			PrivacyItem item = new PrivacyItem(action == UserPrivacyListItem.Action.allow, usrPItem.getOrder());
			UserPrivacyListItem.Type type = usrPItem.getType();
			if (type != null)
			{
				item.setType(PrivacyItem.Type.valueOf(type.name()));
			}
			item.setValue(usrPItem.getValue());
			item.setFilterIQ(usrPItem.isIqFilter());
			item.setFilterMessage(usrPItem.isMessageFilter());
			item.setFilterPresence_in(usrPItem.isPresenceInFilter());
			item.setFilterPresence_out(usrPItem.isPresenceOutFilter());
			
			privacyList.addItem(item);
		}
		
		return privacyList;
	}

	public void userResourceAdded(OnlineUserImpl onlineUser, UserResourceImpl userResource)
	{
		UserPrivacyListDbHelper dbHelper = userPrivacyListDbHelperTracker.getUserPrivacyListDbHelper();
		try
		{
			UserPrivacyList userPrivacyList = dbHelper.getDefaultUserPrivacyList(onlineUser.getNode());
			if (userPrivacyList != null)
			{
				PrivacyList privacyList = userPrivacyListToPrivacyList(userPrivacyList);
				onlineUser.setDefaultPrivacyList(privacyList);
			}
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void userResourceRemoved(OnlineUserImpl onlineUser, UserResourceImpl userResource)
	{
		userResource.setActivePrivacyList(null);
		if (onlineUser.getResourceCount() == 0)
		{
			onlineUser.setDefaultPrivacyList(null);
		}
	}

	public boolean shouldBlockReceivePacket(OnlineUser user, UserResource userResource, Packet packet)
	{
		// TODO Auto-generated method stub
		return false;
	}

	public boolean shouldBlockSend2ClientPacket(OnlineUserImpl onlineUser, UserResourceImpl userResourceImpl, Packet packet)
	{
		// TODO Auto-generated method stub
		return false;
	}
	
}
