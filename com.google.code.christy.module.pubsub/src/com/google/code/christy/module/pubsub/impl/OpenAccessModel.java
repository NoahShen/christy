package com.google.code.christy.module.pubsub.impl;

import com.google.code.christy.dbhelper.PubSubNode;
import com.google.code.christy.module.pubsub.AccessModel;

/**
 * 
 * @author Noah
 * 
 */
public class OpenAccessModel implements AccessModel
{
	@Override
	public String getName()
	{
		return "open";
	}
	
	@Override
	public boolean canSubscribe(PubSubNode node, String subscriber)
	{
		return true;
	}


	@Override
	public boolean canAccessItems(PubSubNode node, String subscriber) throws Exception
	{
		return true;
	}
	
}
