package com.google.code.christy.dbhelper;

import java.util.Collection;

public interface PubSubItemDbHelper
{
	public Collection<PubSubItem> getPubSbuItem(String nodeId) throws Exception;
}
