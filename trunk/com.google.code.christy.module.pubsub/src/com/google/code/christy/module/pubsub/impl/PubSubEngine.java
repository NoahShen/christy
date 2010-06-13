package com.google.code.christy.module.pubsub.impl;

import java.util.Collection;

import com.google.code.christy.dbhelper.PubSubItem;
import com.google.code.christy.dbhelper.PubSubItemDbHelper;
import com.google.code.christy.dbhelper.PubSubItemDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubNode;
import com.google.code.christy.dbhelper.PubSubNodeDbHelper;
import com.google.code.christy.dbhelper.PubSubNodeDbHelperTracker;
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
	
	public PubSubEngine(PubSubManagerImpl pubSubManager, 
				PubSubNodeDbHelperTracker pubSubNodeDbHelperTracker, 
				PubSubItemDbHelperTracker pubSubItemDbHelperTracker)
	{
		this.pubSubManager = pubSubManager; 
		this.pubSubNodeDbHelperTracker = pubSubNodeDbHelperTracker;
		this.pubSubItemDbHelperTracker = pubSubItemDbHelperTracker;
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
	
	
}