package com.google.code.christy.router;

import java.net.SocketAddress;

import com.google.code.christy.routemessage.RouteMessage;

public interface ModuleSession
{
	/**
	 * 
	 * @return
	 */
	public String getInternalStreamId();
	
	/**
	 * 
	 * @return
	 */
	public String getSubDomain();
	
	/**
	 * 
	 * @return
	 */
	public SocketAddress getModuleAddress();
	
	/**
	 * 
	 * @param routeMessage
	 */
	public void write(RouteMessage routeMessage);
	
	/**
	 * 
	 * @param xml
	 */
	public void write(String xml);
	
	/**
	 * 
	 * @return
	 */
	public boolean isConnected();
	
	/**
	 * 
	 */
	public void close();
	
	
}
