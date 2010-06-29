package com.google.code.christy.module.pubsub;

import com.google.code.christy.dbhelper.PubSubNode;
import com.google.code.christy.module.pubsub.PublisherModel;

public class OpenPublisherModel implements PublisherModel
{

	@Override
	public boolean canPublish(PubSubNode node, String publisher)
	{
		return true;
	}

	@Override
	public String getName()
	{
		return "open";
	}

}
