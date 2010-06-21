package com.google.code.christy.dbhelper;

public interface LastPublishTimeDbHelper
{
	public LastPublishTime getLastPublishTime() throws Exception;
	
	public void updateLastPublishTime(long time) throws Exception;
}
