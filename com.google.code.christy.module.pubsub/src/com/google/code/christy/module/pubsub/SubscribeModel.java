package com.google.code.christy.module.pubsub;

import com.google.code.christy.dbhelper.PubSubNode;

/**
 * 
 * @author Noah
 * 
 */
public interface SubscribeModel
{
	public boolean canSubscribe(PubSubNode node, String subscriber) throws Exception;

	public String getName();
}
