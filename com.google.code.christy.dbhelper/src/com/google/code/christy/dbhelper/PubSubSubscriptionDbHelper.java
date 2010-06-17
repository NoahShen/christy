package com.google.code.christy.dbhelper;

import java.util.Collection;

public interface PubSubSubscriptionDbHelper
{
	public Collection<PubSubSubscription> getPubSubSubscriptions(String subscriber, String nodeId) throws Exception;
	
	public void addPubSubSubscription(PubSubSubscription subscription) throws Exception;
}
