package com.google.code.christy.c2s;

import com.google.code.christy.util.Propertied;
import com.google.code.christy.xmpp.XmlStanza;


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
	public String getUsername();

	/**
	 * 
	 * @param username
	 */
	public void setUsername(String username);
	
	/**
	 * 
	 * @return
	 */
	public String getStreamId();
	
	/**
	 * 
	 * @param stanza
	 */
	public void write(XmlStanza stanza);
	/**
	 * 
	 */
	public void close();
	
	/**
	 * 
	 * @return
	 */
	public Status getStatus();
	
	public enum Status
	{
		disconnected,
		
		connected,
		
		authenticated
	}
	
}
