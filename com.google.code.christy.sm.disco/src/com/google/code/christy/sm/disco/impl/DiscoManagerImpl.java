/**
 * 
 */
package com.google.code.christy.sm.disco.impl;

import com.google.code.christy.sm.OnlineUser;
import com.google.code.christy.sm.SmHandler;
import com.google.code.christy.sm.SmManager;
import com.google.code.christy.sm.UserResource;
import com.google.code.christy.sm.disco.DiscoManager;
import com.google.code.christy.xmpp.Iq;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.Packet;
import com.google.code.christy.xmpp.PacketUtils;
import com.google.code.christy.xmpp.XmppError;
import com.google.code.christy.xmpp.disco.DiscoInfoExtension;
import com.google.code.christy.xmpp.disco.DiscoInfoFeature;
import com.google.code.christy.xmpp.disco.DiscoInfoIdentity;
import com.google.code.christy.xmpp.disco.DiscoItem;
import com.google.code.christy.xmpp.disco.DiscoItemsExtension;


/**
 * @author Noah
 *
 */
public class DiscoManagerImpl implements DiscoManager,SmHandler
{
	private DiscoInfoFeatureServiceTracker discoInfoFeatureServiceTracker;
	
	private DiscoInfoIdentityServiceTracker discoInfoIdentityServiceTracker;
	
	private DiscoItemServiceTracker discoItemServiceTracker;
	
	private Object discoInfoLockObj = new Object();
	
	private DiscoInfoExtension discoInfoExtensionCache;
	
	private Object discoItemLockObj = new Object();
	
	private DiscoItemsExtension discoItemsExtensionCache;
	
	public DiscoManagerImpl(DiscoInfoFeatureServiceTracker discoInfoFeatureServiceTracker, 
				DiscoInfoIdentityServiceTracker discoInfoIdentityServiceTracker,
				DiscoItemServiceTracker discoItemServiceTracker)
	{
		this.discoInfoFeatureServiceTracker = discoInfoFeatureServiceTracker;
		this.discoInfoIdentityServiceTracker = discoInfoIdentityServiceTracker;
		this.discoItemServiceTracker = discoItemServiceTracker;
	}
	

	@Override
	public DiscoInfoExtension getDiscoInfo(String node)
	{
		synchronized (discoInfoLockObj)
		{
			if (discoInfoExtensionCache != null)
			{
				return discoInfoExtensionCache;
			}
		}
		
		
		DiscoInfoExtension discoInfo = new DiscoInfoExtension();
		discoInfo.setNode(node);
		
		DiscoInfoIdentity[] identities = discoInfoIdentityServiceTracker.getDiscoInfoIdentities(node);
		for (DiscoInfoIdentity iden : identities)
		{
			DiscoInfoExtension.Identity identity = new DiscoInfoExtension.Identity(iden.getCategory(), iden.getType());
			identity.setName(iden.getName());
			discoInfo.addIdentity(identity);
		}
		
		DiscoInfoFeature[] features = discoInfoFeatureServiceTracker.getDiscoInfoFeatures(node);
		for (DiscoInfoFeature feature : features)
		{
			DiscoInfoExtension.Feature feat = new DiscoInfoExtension.Feature(feature.getFeature());
			discoInfo.addFeature(feat);
		}
		
		synchronized (discoInfoLockObj)
		{
			discoInfoExtensionCache = discoInfo;
			return discoInfoExtensionCache;
		}
		
	}


	@Override
	public DiscoItemsExtension getDiscoItems(String node)
	{
		synchronized (discoItemLockObj)
		{
			if (discoItemsExtensionCache != null)
			{
				return discoItemsExtensionCache;
			}
		}
		
		

		DiscoItemsExtension discoItem = new DiscoItemsExtension();
		discoItem.setNode(node);
		
		DiscoItem[] items = discoItemServiceTracker.getDiscoItems(node);
		for (DiscoItem item : items)
		{
			DiscoItemsExtension.Item itemEx = new DiscoItemsExtension.Item(item.getJid(), item.getName());
			itemEx.setNode(item.getNode());
			discoItem.addItem(itemEx);
		}
		
		synchronized (discoItemLockObj)
		{
			discoItemsExtensionCache = discoItem;
			return discoItemsExtensionCache;
		}
		
		
	}

	
	void discoInfoChanged()
	{
		synchronized (discoInfoLockObj)
		{
			discoInfoExtensionCache = null;
		}
		
	}
	
	void discoItemChanged()
	{
		synchronized (discoItemLockObj)
		{
			discoItemsExtensionCache = null;
		}
		
	}


	@Override
	public boolean accept(SmManager smManager, OnlineUser onlineUser, Packet packet)
	{
		
		return ((packet instanceof Iq)
				&& (packet.getExtension(DiscoInfoExtension.ELEMENTNAME, DiscoInfoExtension.NAMESPACE) != null
				|| packet.getExtension(DiscoItemsExtension.ELEMENTNAME, DiscoItemsExtension.NAMESPACE) != null));
	}


	@Override
	public void handleClientPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		DiscoInfoExtension discoInfo = 
			(DiscoInfoExtension) packet.getExtension(DiscoInfoExtension.ELEMENTNAME, DiscoInfoExtension.NAMESPACE);
		if (discoInfo != null)
		{
			handleClientDiscoInfo(smManager, onlineUser, userResource, packet, discoInfo);
			return;
		}
		
		
		DiscoItemsExtension discoItems = 
			(DiscoItemsExtension) packet.getExtension(DiscoItemsExtension.ELEMENTNAME, DiscoItemsExtension.NAMESPACE);
		if (discoItems != null)
		{
			handleClientDiscoItems(smManager, onlineUser, userResource, packet, discoItems);
			return;
		}
	}


	private void handleClientDiscoItems(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet,
			DiscoItemsExtension discoItems)
	{
		JID to = packet.getTo();
		Iq requestIq = (Iq) packet;
		// server's discoItem
		if (to == null || to.equalsWithBareJid(new JID(null, smManager.getDomain(), null)))
		{
			String node = discoItems.getNode();
			DiscoItemsExtension items = getDiscoItems(node);
			
			
			Iq resultIq = PacketUtils.createResultIq(requestIq);
			resultIq.addExtension(items);
			userResource.sendToSelfClient(resultIq);
			
		}
		else
		{
			//Bare JID
			if (to.getDomain().equals(smManager.getDomain())
					&& to.getResource() == null)
			{
				Iq errorIq = PacketUtils.createErrorIq(requestIq);
				errorIq.setError(new XmppError(XmppError.Condition.feature_not_implemented));
				userResource.sendToSelfClient(errorIq);
			}
			else
			{
				userResource.sendToOtherUser(packet);
			}
			
		}
		
		
	}

	private void handleClientDiscoInfo(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet,
			DiscoInfoExtension discoInfo)
	{
		JID to = packet.getTo();
		Iq requestIq = (Iq) packet;
		// server's discoItem
		if (to == null || to.equalsWithBareJid(new JID(null, smManager.getDomain(), null)))
		{
			String node = discoInfo.getNode();
			DiscoInfoExtension info = getDiscoInfo(node);
			
			
			Iq resultIq = PacketUtils.createResultIq(requestIq);
			resultIq.addExtension(info);
			userResource.sendToSelfClient(resultIq);
			
		}
		else
		{
			//Bare JID
			if (to.getDomain().equals(smManager.getDomain())
					&& to.getResource() == null)
			{
				Iq errorIq = PacketUtils.createErrorIq(requestIq);
				errorIq.setError(new XmppError(XmppError.Condition.feature_not_implemented));
				userResource.sendToSelfClient(errorIq);
				return;
			}
			userResource.sendToOtherUser(packet);
		}
		
	}


	@Override
	public void handleOtherUserPacket(SmManager smManager, OnlineUser onlineUser, UserResource userResource, Packet packet)
	{
		userResource.sendToSelfClient(packet);
	}


	@Override
	public UserResource[] checkResource(SmManager smManager, OnlineUser onlineUser)
	{
		if (onlineUser != null)
		{
			return onlineUser.getAllActiveUserResources();
		}
		return new UserResource[]{null};
	}
	
	@Override
	public void userResourceAdded(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		
	}


	@Override
	public void userResourceAvailable(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		
	}


	@Override
	public void userResourceRemoved(SmManager smManager, OnlineUser onlineUser, UserResource userResource)
	{
		
	}


}
