package com.google.code.christy.module.pubsub.impl;

import com.google.code.christy.dbhelper.PubSubNode;
import com.google.code.christy.module.pubsub.SubscribeModel;

/**
 * 
 * @author Noah
 * 
 */
public class OpenSubscribeModel implements SubscribeModel
{
	
	@Override
	public boolean canSubscribe(PubSubNode node, String subscriber)
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "open";
	}

}
