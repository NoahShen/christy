package com.google.code.christy.module.pubsub;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.util.Propertied;

public interface PubSubManager extends Propertied
{
	public String getDomain();
	
	public String getSubDomain();
	
	public String getServiceId();
	
	public void start();
	
	public void stop();

	/**
	 * 
	 * @return
	 */
	public String getRouterIp();
	
	/**
	 * 
	 * @param routerIP
	 */
	public void setRouterIp(String routerIp);
	
	/**
	 * 
	 * @return
	 */
	public int getRouterPort();
	
	/**
	 * 
	 * @param routerPort
	 */
	public void setRouterPort(int routerPort);
	
	/**
	 * 
	 * @return
	 */
	public String getRouterPassword();
	
	/**
	 * 
	 * @param routerPassword
	 */
	public void setRouterPassword(String routerPassword);
	
	/**
	 *  exit program
	 */
	public void exit();
	
	/**
	 * 
	 */
	public boolean isStarted();
	
	/**
	 * 
	 * @return
	 */
	public boolean isRouterConnected();

	/**
	 * 
	 * @param routeMessage
	 */
	public void sendToRouter(RouteMessage routeMessage);
}
