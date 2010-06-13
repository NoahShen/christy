package com.google.code.christy.module.pubsub.impl;

import java.util.Collection;
import java.util.Collections;

import com.google.code.christy.dbhelper.PubSubAffiliation;
import com.google.code.christy.dbhelper.PubSubAffiliationDbHelper;
import com.google.code.christy.dbhelper.PubSubAffiliationDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubItem;
import com.google.code.christy.dbhelper.PubSubItemDbHelper;
import com.google.code.christy.dbhelper.PubSubItemDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubNode;
import com.google.code.christy.dbhelper.PubSubNodeDbHelper;
import com.google.code.christy.dbhelper.PubSubNodeDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubSubscription;
import com.google.code.christy.dbhelper.PubSubSubscriptionDbHelper;
import com.google.code.christy.dbhelper.PubSubSubscriptionDbHelperTracker;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.disco.DiscoInfoExtension;
import com.google.code.christy.xmpp.disco.DiscoItemsExtension;

public class PubSubEngine
{
	private DiscoInfoExtension discoInfo;
	private DiscoItemsExtension emptyDiscoItemsExtension = new DiscoItemsExtension();
	private DiscoInfoExtension emptyDiscoInfoExtension = new DiscoInfoExtension();
	private PubSubManagerImpl pubSubManager;
	private PubSubNodeDbHelperTracker pubSubNodeDbHelperTracker;
	private PubSubItemDbHelperTracker pubSubItemDbHelperTracker;
	private PubSubSubscriptionDbHelperTracker pubSubSubscriptionDbHelperTracker;
	private PubSubAffiliationDbHelperTracker pubSubAffiliationDbHelperTracker;
	
	public PubSubEngine(PubSubManagerImpl pubSubManager, 
				PubSubNodeDbHelperTracker pubSubNodeDbHelperTracker, 
				PubSubItemDbHelperTracker pubSubItemDbHelperTracker, 
				PubSubSubscriptionDbHelperTracker pubSubSubscriptionDbHelperTracker, 
				PubSubAffiliationDbHelperTracker pubSubAffiliationDbHelperTracker)
	{
		this.pubSubManager = pubSubManager; 
		this.pubSubNodeDbHelperTracker = pubSubNodeDbHelperTracker;
		this.pubSubItemDbHelperTracker = pubSubItemDbHelperTracker;
		this.pubSubSubscriptionDbHelperTracker = pubSubSubscriptionDbHelperTracker;
		this.pubSubAffiliationDbHelperTracker = pubSubAffiliationDbHelperTracker;
		
		discoInfo = new DiscoInfoExtension();
		discoInfo.addFeature(new DiscoInfoExtension.Feature("http://jabber.org/protocol/pubsub"));
	}
	
	public DiscoInfoExtension getDiscoInfo(String node)
	{
		if (node == null)
		{
			return discoInfo;
		}
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		if (pubSubNodeDbHelper != null)
		{
			try
			{
				DiscoInfoExtension discoInfoExtension = new DiscoInfoExtension();
				discoInfoExtension.setNode(node);
				
				PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
				if (pubSubNode == null)
				{
					return null;
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
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return emptyDiscoInfoExtension;
	}
	
	public DiscoItemsExtension getDiscoItem(String node)
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubItemDbHelper pubSubItemDbHelper = pubSubItemDbHelperTracker.getPubSubItemDbHelper();
		if (pubSubNodeDbHelper != null && pubSubItemDbHelper != null)
		{
			try
			{
				DiscoItemsExtension discoItemsExtension = new DiscoItemsExtension();
				discoItemsExtension.setNode(node);
				
				PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
				if (pubSubNode == null)
				{
					return null;
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
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		emptyDiscoItemsExtension.setNode(node);
		return emptyDiscoItemsExtension;
	}
	
	
	public Collection<PubSubSubscription> getPubSubSubscriptions(String subscriber, String node)
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubSubscriptionDbHelper pubSubSubscriptionDbHelper = pubSubSubscriptionDbHelperTracker.getPubSubSubscriptionDbHelper();
		if (pubSubNodeDbHelper != null && pubSubSubscriptionDbHelper != null)
		{
			try
			{
				if (node != null)
				{
					PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
					if (pubSubNode == null)
					{
						return null;
					}
				}
				
				Collection<PubSubSubscription> subs = pubSubSubscriptionDbHelper.getPubSubSubscriptions(subscriber, node);
				return subs;
				
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}
	
	
	public Collection<PubSubAffiliation> getPubSubAffiliation(String jid, String node)
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		PubSubAffiliationDbHelper pubSubAffiliationDbHelper = pubSubAffiliationDbHelperTracker.getPubSubAffiliationDbHelper();
		if (pubSubNodeDbHelper != null && pubSubAffiliationDbHelper != null)
		{
			try
			{
				if (node != null)
				{
					PubSubNode pubSubNode = pubSubNodeDbHelper.getNode(node);
					if (pubSubNode == null)
					{
						return null;
					}
				}
				
				Collection<PubSubAffiliation> pubSubAffiliations = pubSubAffiliationDbHelper.getPubSubAffiliation(jid, node);
				return pubSubAffiliations;
				
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Collections.emptyList();
	}
	
}
