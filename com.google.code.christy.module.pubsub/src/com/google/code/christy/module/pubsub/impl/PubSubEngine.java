package com.google.code.christy.module.pubsub.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.google.code.christy.dbhelper.PubSubAffiliation;
import com.google.code.christy.dbhelper.PubSubAffiliationDbHelper;
import com.google.code.christy.dbhelper.PubSubAffiliationDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubItem;
import com.google.code.christy.dbhelper.PubSubItemDbHelper;
import com.google.code.christy.dbhelper.PubSubItemDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubNode;
import com.google.code.christy.dbhelper.PubSubNodeConfig;
import com.google.code.christy.dbhelper.PubSubNodeConfigDbHelper;
import com.google.code.christy.dbhelper.PubSubNodeConfigDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubNodeDbHelper;
import com.google.code.christy.dbhelper.PubSubNodeDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubSubscription;
import com.google.code.christy.dbhelper.PubSubSubscriptionDbHelper;
import com.google.code.christy.dbhelper.PubSubSubscriptionDbHelperTracker;
import com.google.code.christy.module.pubsub.AccessModel;
import com.google.code.christy.util.StringUtils;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.disco.DiscoInfoExtension;
import com.google.code.christy.xmpp.disco.DiscoItemsExtension;

public class PubSubEngine
{
	private DiscoInfoExtension discoInfo;
	private PubSubManagerImpl pubSubManager;
	private PubSubNodeDbHelperTracker pubSubNodeDbHelperTracker;
	private PubSubItemDbHelperTracker pubSubItemDbHelperTracker;
	private PubSubSubscriptionDbHelperTracker pubSubSubscriptionDbHelperTracker;
	private PubSubAffiliationDbHelperTracker pubSubAffiliationDbHelperTracker;
	private PubSubNodeConfigDbHelperTracker pubSubNodeConfigDbHelperTracker;
	private AccessModelTracker accessModelTracker;
	
	public PubSubEngine(PubSubManagerImpl pubSubManager, 
				PubSubNodeDbHelperTracker pubSubNodeDbHelperTracker, 
				PubSubItemDbHelperTracker pubSubItemDbHelperTracker, 
				PubSubSubscriptionDbHelperTracker pubSubSubscriptionDbHelperTracker, 
				PubSubAffiliationDbHelperTracker pubSubAffiliationDbHelperTracker, 
				PubSubNodeConfigDbHelperTracker pubSubNodeConfigDbHelperTracker, 
				AccessModelTracker accessModelTracker)
	{
		this.pubSubManager = pubSubManager; 
		this.pubSubNodeDbHelperTracker = pubSubNodeDbHelperTracker;
		this.pubSubItemDbHelperTracker = pubSubItemDbHelperTracker;
		this.pubSubSubscriptionDbHelperTracker = pubSubSubscriptionDbHelperTracker;
		this.pubSubAffiliationDbHelperTracker = pubSubAffiliationDbHelperTracker;
		this.pubSubNodeConfigDbHelperTracker = pubSubNodeConfigDbHelperTracker;
		this.accessModelTracker = accessModelTracker;
		
		discoInfo = new DiscoInfoExtension();
		discoInfo.addFeature(new DiscoInfoExtension.Feature("http://jabber.org/protocol/pubsub"));
	}
	
	public DiscoInfoExtension getDiscoInfo(String node) throws Exception
	{
		if (node == null)
		{
			return discoInfo;
		}
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		
		if (pubSubNodeDbHelper == null)
		{
			throw new Exception("pubSubNodeDbHelper is null");
		}
		
		DiscoInfoExtension discoInfoExtension = new DiscoInfoExtension();
		discoInfoExtension.setNode(node);
		
		PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
		if (pubSubNode == null)
		{
			throw new NodeNotExistException();
		}
		String type = "leaf";
		if (!pubSubNode.isLeaf())
		{
			type = "collection";
		}
		
		DiscoInfoExtension.Identity identity = new DiscoInfoExtension.Identity("pubsub", type);
		discoInfoExtension.addIdentity(identity);
		
		return discoInfoExtension;
	}
	
	public DiscoItemsExtension getDiscoItem(String node) throws Exception
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubItemDbHelper pubSubItemDbHelper = pubSubItemDbHelperTracker.getPubSubItemDbHelper();
		if (pubSubNodeDbHelper == null || pubSubNodeDbHelper == null)
		{
			throw new Exception("pubSubNodeDbHelper or pubSubNodeDbHelper is null");
		}
		
		DiscoItemsExtension discoItemsExtension = new DiscoItemsExtension();
		discoItemsExtension.setNode(node);
		
		PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
		if (pubSubNode == null)
		{
			throw new NodeNotExistException();
		}
		
		if (pubSubNode.isLeaf())
		{
			Collection<PubSubItem> items = pubSubItemDbHelper.getPubSubItems(node, pubSubManager.getMaxItems());
			for(PubSubItem pubSubItem : items)
			{
				DiscoItemsExtension.Item item = 
					new DiscoItemsExtension.Item(new JID(pubSubItem.getJid()), pubSubItem.getItemId());
				discoItemsExtension.addItem(item);
			}
		}
		else
		{
			Collection<PubSubNode> nodes = pubSubNodeDbHelper.getChildNodes(node);
			for(PubSubNode pubSubNode2 : nodes)
			{
				DiscoItemsExtension.Item item = 
					new DiscoItemsExtension.Item(new JID(null, pubSubManager.getSubDomain(), null), pubSubNode2.getName());
				item.setNode(pubSubNode2.getNodeId());
				discoItemsExtension.addItem(item);
			}
		}
		
		return discoItemsExtension;
	}
	
	
	public Collection<PubSubSubscription> getPubSubSubscriptions(String subscriber, String node) throws Exception
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubSubscriptionDbHelper pubSubSubscriptionDbHelper = pubSubSubscriptionDbHelperTracker.getPubSubSubscriptionDbHelper();
		if (pubSubNodeDbHelper == null || pubSubSubscriptionDbHelper == null)
		{
			throw new Exception("pubSubNodeDbHelper or pubSubSubscriptionDbHelper is null");
		}

		
		Collection<PubSubSubscription> subs = pubSubSubscriptionDbHelper.getPubSubSubscriptions(subscriber, node);
		return subs;
	}
	
	public PubSubSubscription getPubSubSubscription(String subscriber, String node, String subId) throws Exception
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubSubscriptionDbHelper pubSubSubscriptionDbHelper = pubSubSubscriptionDbHelperTracker.getPubSubSubscriptionDbHelper();
		if (pubSubNodeDbHelper == null || pubSubSubscriptionDbHelper == null)
		{
			throw new Exception("pubSubNodeDbHelper or pubSubSubscriptionDbHelper is null");
		}


		if (node == null)
		{
			throw new NullPointerException("node is null");
		}
		
		PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
		if (pubSubNode == null)
		{
			throw new NodeNotExistException();
		}
		
		
		PubSubSubscription sub = pubSubSubscriptionDbHelper.getPubSubSubscription(subscriber, node, subId);
		if (sub == null)
		{
			throw new InvalidSubIdException();
		}
		return sub;
	}
	
	
	public Collection<PubSubAffiliation> getPubSubAffiliation(String jid, String node) throws Exception
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubAffiliationDbHelper pubSubAffiliationDbHelper = pubSubAffiliationDbHelperTracker.getPubSubAffiliationDbHelper();
		if (pubSubNodeDbHelper == null || pubSubAffiliationDbHelper == null)
		{
			throw new Exception("pubSubNodeDbHelper or pubSubAffiliationDbHelper is null");
		}
		
		Collection<PubSubAffiliation> pubSubAffiliations = pubSubAffiliationDbHelper.getPubSubAffiliation(jid, node);
		return pubSubAffiliations;
	}
	
	public PubSubSubscription subscribeNode(String subscriber, String node) throws Exception
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubNodeConfigDbHelper pubSubNodeConfigDbHelper = pubSubNodeConfigDbHelperTracker.getPubSubNodeConfigDbHelper();
		PubSubSubscriptionDbHelper pubSubSubscriptionDbHelper = pubSubSubscriptionDbHelperTracker.getPubSubSubscriptionDbHelper();
		if (pubSubNodeDbHelper == null 
				|| pubSubNodeConfigDbHelper == null 
				|| pubSubSubscriptionDbHelper == null)
		{
			throw new Exception("pubSubNodeDbHelper or pubSubNodeConfigDbHelper or pubSubSubscriptionDbHelper is null");
		}
		
		PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
 		if (pubSubNode == null)
		{
			throw new NodeNotExistException();
		}
 		
 		Collection<PubSubSubscription> subs = pubSubSubscriptionDbHelper.getPubSubSubscriptions(subscriber, node);
 		if (subs != null && !subs.isEmpty())
 		{
 			throw new TooManySubscriptionsException();
 		}
 		
 		PubSubNodeConfig pubSubNodeConfig = pubSubNodeConfigDbHelper.getPubSubNodeConfig(node);
 		String subscribeModel = pubSubNodeConfig.getSubscribeModel();
 		AccessModel model = accessModelTracker.getSubscribeModel(subscribeModel);
 		if (model == null)
 		{
 			throw new Exception("Can not get SubscribeModel[" + subscribeModel + "]");
 		}
 		
 		if (model.canSubscribe(pubSubNode, subscriber))
 		{
 			PubSubSubscription subscription = new PubSubSubscription();
 			subscription.setServiceId(pubSubManager.getServiceId());
 			subscription.setNodeId(node);
 			subscription.setSubId(StringUtils.hash(node + subscriber, "MD5"));
 			subscription.setSubscriber(subscriber);
 			subscription.setJid(pubSubManager.getSubDomain());
 			subscription.setSubscription(PubSubSubscription.Subscription.subscribed);
 			
 			pubSubSubscriptionDbHelper.addPubSubSubscription(subscription);
 			return subscription;
 		}
 		
 		return null;
 		
	}

	public void unsubscribeNode(String jid, String node, String subId) throws Exception
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubSubscriptionDbHelper pubSubSubscriptionDbHelper = pubSubSubscriptionDbHelperTracker.getPubSubSubscriptionDbHelper();
		if (pubSubNodeDbHelper == null 
				|| pubSubSubscriptionDbHelper == null)
		{
			throw new Exception("pubSubNodeDbHelper or pubSubSubscriptionDbHelper is null");
		}
		
		PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
 		if (pubSubNode == null)
		{
			throw new NodeNotExistException();
		}
 		
 		pubSubSubscriptionDbHelper.removePubSubSubscription(jid, node, subId);
 		
	}
	
	public void updateSubscriptionConfigure(String jid, String nodeId, String subId, Map<String, Object> config) throws Exception
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubSubscriptionDbHelper pubSubSubscriptionDbHelper = pubSubSubscriptionDbHelperTracker.getPubSubSubscriptionDbHelper();
		if (pubSubNodeDbHelper == null || pubSubSubscriptionDbHelper == null)
		{
			throw new Exception("pubSubNodeDbHelper or pubSubSubscriptionDbHelper is null");
		}

		if (nodeId == null)
		{
			throw new NullPointerException("node is null");
		}
		
		PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(nodeId);
		if (pubSubNode == null)
		{
			throw new NodeNotExistException();
		}
		
		pubSubSubscriptionDbHelper.updatePubSubSubscription(jid, nodeId, subId, config);
	}
	
	public Collection<PubSubItem> getPubSubItems(String jid, String nodeId, String subId, String itemId, int maxItems) throws Exception
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubSubscriptionDbHelper pubSubSubscriptionDbHelper = pubSubSubscriptionDbHelperTracker.getPubSubSubscriptionDbHelper();
		PubSubItemDbHelper pubSubItemDbHelper = pubSubItemDbHelperTracker.getPubSubItemDbHelper();
		PubSubNodeConfigDbHelper pubSubNodeConfigDbHelper = pubSubNodeConfigDbHelperTracker.getPubSubNodeConfigDbHelper();
		if (pubSubNodeDbHelper == null 
			|| pubSubSubscriptionDbHelper == null
			|| pubSubItemDbHelper == null
			|| pubSubNodeConfigDbHelper == null)
		{
			throw new Exception("pubSubNodeDbHelper" +
						" or pubSubSubscriptionDbHelper" +
						" or pubSubItemDbHelper" +
						" or pubSubNodeConfigDbHelper" +
						" is null");
		}

		if (nodeId == null)
		{
			throw new NullPointerException("node is null");
		}
		
		PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(nodeId);
		if (pubSubNode == null)
		{
			throw new NodeNotExistException();
		}
		
		PubSubNodeConfig pubSubNodeConfig = pubSubNodeConfigDbHelper.getPubSubNodeConfig(nodeId);
 		String subscribeModel = pubSubNodeConfig.getSubscribeModel();
 		
		AccessModel model = accessModelTracker.getSubscribeModel(subscribeModel);
 		if (model == null)
 		{
 			throw new Exception("Can not get SubscribeModel[" + subscribeModel + "]");
 		}
 		
 		if (!model.canAccessItems(pubSubNode, jid))
 		{
 			throw new CannotAccessException();
 		}
 		
		PubSubSubscription sub = pubSubSubscriptionDbHelper.getPubSubSubscription(jid, nodeId, subId);
		if (sub == null)
		{
			throw new InvalidSubIdException();
		}
		
		Collection<PubSubItem> items = null;
		if (itemId == null)
		{
			items = pubSubItemDbHelper.getPubSubItems(nodeId, maxItems);
		}
		else
		{
			PubSubItem item = pubSubItemDbHelper.getPubSubItem(nodeId, itemId);
			items = new ArrayList<PubSubItem>(1);
			items.add(item);
		}
		
		return items;
	}
}
