/**
 * 
 */
package net.sf.christy.router;

import java.net.SocketAddress;

import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.util.Propertied;

/**
 * @author noah
 *
 */
public interface C2sSession extends Propertied
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
	public String getC2sName();
	
	/**
	 * 
	 * @return
	 */
	public SocketAddress getC2sAddress();
	
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
