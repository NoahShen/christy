package com.google.code.christy.dbhelper;

import java.util.Collection;

public interface PubSubItemDbHelper
{
	public Collection<PubSubItem> getPubSubItems(String nodeId, int max) throws Exception;
	
	public PubSubItem getPubSubItem(String nodeId, String itemId) throws Exception;
	
	public void addPubSubItem(PubSubItem... pubSubItems) throws Exception;
}
