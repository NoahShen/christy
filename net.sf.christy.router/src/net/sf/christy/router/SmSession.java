/**
 * 
 */
package net.sf.christy.router;

import java.net.SocketAddress;

import net.sf.christy.util.Propertied;
import net.sf.christy.xmpp.XmlStanza;

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
	 * @param stanza
	 */
	public void write(XmlStanza stanza);
	
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
