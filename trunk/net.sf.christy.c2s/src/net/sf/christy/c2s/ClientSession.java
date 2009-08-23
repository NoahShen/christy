package net.sf.christy.c2s;

import java.net.SocketAddress;

import net.sf.christy.util.Propertied;
import net.sf.christy.xmpp.XMLStanza;

/**
 * @author noah
 *
 */
public interface ClientSession extends Propertied
{
	/**
	 * 
	 * @return
	 */
	public String getStreamId();
	
	/**
	 * 
	 * @return
	 */
	public SocketAddress getClientAddress();
	
	/**
	 * 
	 * @param stanza
	 */
	public void write(String stanza);
	
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
	 * @return
	 */
	public boolean isAuthenticated();
	
	/**
	 * 
	 * @return
	 */
	public boolean isUsingTLS();
	
	/**
	 * 
	 * @return
	 */
	public boolean isUsingCompression();
	
	/**
	 * 
	 * @return
	 */
	public boolean isSessionBinded();
	
	/**
	 * 
	 * @return
	 */
	public boolean isResourceBinded();
	
	/**
	 * 
	 */
	public void close();
	
	
}
