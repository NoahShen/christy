package com.google.code.christy.lib;

import java.util.Map;

public interface ServerController
{
	public Map<String, Object> getServerInfo();
	
	public void stopServer();
}
