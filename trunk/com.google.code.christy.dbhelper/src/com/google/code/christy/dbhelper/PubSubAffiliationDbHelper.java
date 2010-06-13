package com.google.code.christy.dbhelper;

import java.util.Collection;

public interface PubSubAffiliationDbHelper
{
	public Collection<PubSubAffiliation> getPubSubAffiliation(String jid, String nodeId) throws Exception;
}
