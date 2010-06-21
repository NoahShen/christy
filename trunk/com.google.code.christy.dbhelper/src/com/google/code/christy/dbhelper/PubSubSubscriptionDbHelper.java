package com.google.code.christy.dbhelper;

import java.util.Collection;
import java.util.Map;

public interface PubSubSubscriptionDbHelper
{
	public Collection<PubSubSubscription> getPubSubSubscriptions(String subscriber, String nodeId) throws Exception;
	
	public PubSubSubscription getPubSubSubscription(String subscriber, String nodeId, String subId) throws Exception;
	
	public void addPubSubSubscription(PubSubSubscription subscription) throws Exception;
	
	public void removePubSubSubscription(String jid, String nodeId, String subId) throws Exception;
	
	public void updatePubSubSubscription(String jid, String nodeId, String subId, Map<String, Object> config) throws Exception;
	
	public Collection<String> getSubscribedJid(String nodeId) throws Exception;
}
