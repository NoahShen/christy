package com.google.code.christy.module.pubsub.impl;

import java.util.Collection;
import java.util.List;

import com.google.code.christy.dbhelper.LastPublishTime;
import com.google.code.christy.dbhelper.LastPublishTimeDbHelper;
import com.google.code.christy.dbhelper.LastPublishTimeDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubItem;
import com.google.code.christy.dbhelper.PubSubItemDbHelper;
import com.google.code.christy.dbhelper.PubSubItemDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubSubscriptionDbHelper;
import com.google.code.christy.dbhelper.PubSubSubscriptionDbHelperTracker;
import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.Message;
import com.google.code.christy.xmpp.pubsub.PubSubEventExtension;
import com.google.code.christy.xmpp.pubsub.PubSubItems;

public class PubSubItemSender
{
	private PubSubManagerImpl pubSubManager;
	
	private PubSubItemDbHelperTracker pubSubItemDbHelperTracker;
	
	private LastPublishTimeDbHelperTracker lastPublishTimeDbHelperTracker;

	private PubSubSubscriptionDbHelperTracker pubSubSubscriptionDbHelperTracker;

	private SendThread sendThread;
	
	private long oldLastTime;
	
	public PubSubItemSender(PubSubManagerImpl pubSubManager, 
				PubSubItemDbHelperTracker pubSubItemDbHelperTracker,
				LastPublishTimeDbHelperTracker lastPublishTimeDbHelperTracker,
				PubSubSubscriptionDbHelperTracker pubSubSubscriptionDbHelperTracker)
	{
		this.pubSubManager = pubSubManager;
		this.pubSubItemDbHelperTracker = pubSubItemDbHelperTracker;
		this.lastPublishTimeDbHelperTracker = lastPublishTimeDbHelperTracker;
		this.pubSubSubscriptionDbHelperTracker = pubSubSubscriptionDbHelperTracker;
		this.sendThread = new SendThread();
	}
	
	public void start()
	{
		this.sendThread.setStop(false);
		this.sendThread.start();
	}
	
	public void stop()
	{
		this.sendThread.setStop(true);
	}
	
	public synchronized void doSend() throws Exception
	{
		LastPublishTimeDbHelper lastPublishTimeDbHelper = lastPublishTimeDbHelperTracker.getLastPublishTimeDbHelper();
		PubSubItemDbHelper pubSubItemDbHelper = pubSubItemDbHelperTracker.getPubSubItemDbHelper();
		PubSubSubscriptionDbHelper pubSubSubscriptionDbHelper = pubSubSubscriptionDbHelperTracker.getPubSubSubscriptionDbHelper();
		if (lastPublishTimeDbHelper == null
			|| pubSubItemDbHelper == null
			|| pubSubSubscriptionDbHelper == null)
		{
			throw new Exception("lastPublishTimeDbHelper" +
						" or pubSubItemDbHelper" +
						" or pubSubSubscriptionDbHelper" +
						" is null");
		}
		
		LastPublishTime lastPublishTime = lastPublishTimeDbHelper.getLastPublishTime();
		long lastTime = lastPublishTime.getTime();
		
		if (oldLastTime == lastTime)
		{
			return;
		}
		
		oldLastTime = lastTime;
		List<PubSubItem> pubSubItems = pubSubItemDbHelper.getPubSubItemByTime(lastTime);
		if (pubSubItems.isEmpty())
		{
			return;
		}
		
		long lastItemTime = pubSubItems.get(0).getCreationDate();
		
		JID from = new JID(pubSubManager.getSubDomain());
		for (PubSubItem item : pubSubItems)
		{
			String nodeId = item.getNodeId();
			
			RouteMessage routeMessage = new RouteMessage(pubSubManager.getSubDomain());
			
			Message message = new Message();
			message.setFrom(from);
			
			PubSubEventExtension pubSubEvent = new PubSubEventExtension();
			
			
			PubSubItems items = new PubSubItems(nodeId);
			
			PubSubItems.Item pubSubItemsItem = new PubSubItems.Item(item.getItemId());
			pubSubItemsItem.setPayload(item.getPayload());
			items.addItem(pubSubItemsItem);
			
			pubSubEvent.addStanza(items);
			
			message.addExtension(pubSubEvent);
			
			routeMessage.setXmlStanza(message);
			
			Collection<String> jids = pubSubSubscriptionDbHelper.getSubscribedJid(nodeId);
			
			for (String jidStr : jids)
			{
				JID jid = new JID(jidStr);
				if (jid.getDomain().equals(pubSubManager.getDomain()))
				{
					routeMessage.setToUserNode(jid.getNode());
				}
				message.setTo(jid);
				pubSubManager.sendToRouter(routeMessage);
				message.setTo(null);
				routeMessage.setToUserNode(null);
			}
		}
		
		lastPublishTimeDbHelper.updateLastPublishTime(lastItemTime);
	}
	
	private class SendThread extends Thread
	{

		private boolean stop = false;
		
		public boolean isStop()
		{
			return stop;
		}

		public void setStop(boolean stop)
		{
			this.stop = stop;
		}

		@Override
		public void run()
		{
			while(!stop)
			{
				try
				{
					doSend();
					Thread.sleep(pubSubManager.getSendSleep());
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					setStop(true);
				}
			}
		}
		
	}
	
}
