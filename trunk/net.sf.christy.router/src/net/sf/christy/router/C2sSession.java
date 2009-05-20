/**
 * 
 */
package net.sf.christy.router;

import java.net.SocketAddress;

import net.sf.christy.util.Propertied;
import net.sf.christy.xmpp.XMLStanza;

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
	 * @param stanza
	 */
	public void write(XMLStanza stanza);
	
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
