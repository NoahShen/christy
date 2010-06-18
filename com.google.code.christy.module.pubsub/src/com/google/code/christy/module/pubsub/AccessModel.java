package com.google.code.christy.module.pubsub;

import com.google.code.christy.dbhelper.PubSubNode;

/**
 * 
 * @author Noah
 * 
 */
public interface AccessModel
{
	public boolean canSubscribe(PubSubNode node, String subscriber) throws Exception;
	
	public boolean canAccessItems(PubSubNode node, String subscriber) throws Exception;
	
	public String getName();
}
