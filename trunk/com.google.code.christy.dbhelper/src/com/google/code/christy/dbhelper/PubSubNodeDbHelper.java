package com.google.code.christy.dbhelper;

import java.util.Collection;

public interface PubSubNodeDbHelper
{
	public Collection<PubSubNode> getNodes(String parent) throws Exception;
}
