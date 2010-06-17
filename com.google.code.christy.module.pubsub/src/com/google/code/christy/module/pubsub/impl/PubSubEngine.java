package com.google.code.christy.module.pubsub.impl;

import java.util.Collection;

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
import com.google.code.christy.module.pubsub.SubscribeModel;
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
	private SubscribeModelTracker subscribeModelTracker;
	
	public PubSubEngine(PubSubManagerImpl pubSubManager, 
				PubSubNodeDbHelperTracker pubSubNodeDbHelperTracker, 
				PubSubItemDbHelperTracker pubSubItemDbHelperTracker, 
				PubSubSubscriptionDbHelperTracker pubSubSubscriptionDbHelperTracker, 
				PubSubAffiliationDbHelperTracker pubSubAffiliationDbHelperTracker, 
				PubSubNodeConfigDbHelperTracker pubSubNodeConfigDbHelperTracker, 
				SubscribeModelTracker subscribeModelTracker)
	{
		this.pubSubManager = pubSubManager; 
		this.pubSubNodeDbHelperTracker = pubSubNodeDbHelperTracker;
		this.pubSubItemDbHelperTracker = pubSubItemDbHelperTracker;
		this.pubSubSubscriptionDbHelperTracker = pubSubSubscriptionDbHelperTracker;
		this.pubSubAffiliationDbHelperTracker = pubSubAffiliationDbHelperTracker;
		this.pubSubNodeConfigDbHelperTracker = pubSubNodeConfigDbHelperTracker;
		this.subscribeModelTracker = subscribeModelTracker;
		
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
			Collection<PubSubItem> items = pubSubItemDbHelper.getPubSbuItem(node);
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
		
		if (node != null)
		{
			PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
			if (pubSubNode == null)
			{
				throw new NodeNotExistException();
			}
		}
		
		Collection<PubSubSubscription> subs = pubSubSubscriptionDbHelper.getPubSubSubscriptions(subscriber, node);
		return subs;
	}
	
	
	public Collection<PubSubAffiliation> getPubSubAffiliation(String jid, String node) throws Exception
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubAffiliationDbHelper pubSubAffiliationDbHelper = pubSubAffiliationDbHelperTracker.getPubSubAffiliationDbHelper();
		if (pubSubNodeDbHelper == null || pubSubAffiliationDbHelper == null)
		{
			throw new Exception("pubSubNodeDbHelper or pubSubAffiliationDbHelper is null");
		}
		
		if (node != null)
		{
			PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
	 		if (pubSubNode == null)
			{
				throw new NodeNotExistException();
			}
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
 		SubscribeModel model = subscribeModelTracker.getSubscribeModel(subscribeModel);
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
	
}
