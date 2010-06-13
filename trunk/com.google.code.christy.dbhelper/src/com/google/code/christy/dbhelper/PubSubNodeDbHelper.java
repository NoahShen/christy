package com.google.code.christy.dbhelper;

import java.util.Collection;

public interface PubSubNodeDbHelper
{
	public Collection<PubSubNode> getChildNodes(String parent) throws Exception;
	
	public PubSubNode getNode(String nodeId) throws Exception;
}
