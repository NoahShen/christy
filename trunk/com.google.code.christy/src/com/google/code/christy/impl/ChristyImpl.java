package com.google.code.christy.impl;

import com.google.code.christy.Christy;
import com.google.code.christy.util.AbstractPropertied;

public class ChristyImpl extends AbstractPropertied implements Christy
{
	private String appPath;
	
	public ChristyImpl()
	{
		appPath = System.getProperty("appPath");
		if (appPath == null)
		{
			appPath = System.getProperty("user.dir");
		}
	}

	@Override
	public String getAppPath()
	{
		return appPath;
	}

}
