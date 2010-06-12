package com.google.code.christy.module.pubsub.impl;

import java.util.Collection;

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
	private PubSubManagerImpl pubSubManager;
	private PubSubNodeDbHelperTracker pubSubNodeDbHelperTracker;
	
	public PubSubEngine(PubSubManagerImpl pubSubManager, PubSubNodeDbHelperTracker pubSubNodeDbHelperTracker)
	{
		this.pubSubManager = pubSubManager; 
		this.pubSubNodeDbHelperTracker = pubSubNodeDbHelperTracker;
		discoInfo = new DiscoInfoExtension();
		discoInfo.addFeature(new DiscoInfoExtension.Feature("http://jabber.org/protocol/pubsub"));
	}
	
	public DiscoInfoExtension getDiscoInfo()
	{
		return discoInfo;
	}
	
	public DiscoItemsExtension getDiscoItem(String node)
	{
		PubSubNodeDbHelper pubSubNodeDbHelper = pubSubNodeDbHelperTracker.getPubSubNodeDbHelper();
		if (pubSubNodeDbHelper != null)
		{
			try
			{
				DiscoItemsExtension discoItemsExtension = new DiscoItemsExtension();
				discoItemsExtension.setNode(node);
				Collection<PubSubNode> nodes = pubSubNodeDbHelper.getNodes(node);
				for(PubSubNode pubSubNode : nodes)
				{
					DiscoItemsExtension.Item item = 
						new DiscoItemsExtension.Item(new JID(null, pubSubManager.getSubDomain(), null), pubSubNode.getName());
					item.setNode(pubSubNode.getNodeId());
					discoItemsExtension.addItem(item);
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
