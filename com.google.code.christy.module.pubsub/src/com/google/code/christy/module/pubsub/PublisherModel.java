package com.google.code.christy.module.pubsub;

import com.google.code.christy.dbhelper.PubSubNode;

/**
 * 
 * @author Noah
 * 
 */
public interface PublisherModel
{

	public String getName();

	public boolean canPublish(PubSubNode node, String publisher);

}
