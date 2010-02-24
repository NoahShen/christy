/**
 * 
 */
package com.google.code.christy.router;

import java.net.SocketAddress;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.util.Propertied;


/**
 * @author noah
 *
 */
public interface SmSession extends Propertied
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
	public String getSmName();
	
	/**
	 * 
	 * @return
	 */
	public SocketAddress getSmAddress();
	
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
