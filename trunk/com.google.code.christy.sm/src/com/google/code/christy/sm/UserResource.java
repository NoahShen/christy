/**
 * 
 */
package com.google.code.christy.sm;

import com.google.code.christy.util.Propertied;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.Presence;
import com.google.code.christy.xmpp.PrivacyList;
import com.google.code.christy.xmpp.XmlStanza;


/**
 * @author noah
 *
 */
public interface UserResource extends Propertied
{
	/**
	 * 
	 * @return
	 */
	public OnlineUser getOnlineUser();
	
	/**
	 * 
	 * @return
	 */
	public JID getFullJid();
	
	/**
	 * 
	 * @return
	 */
	public String getRelatedC2s();
	
	/**
	 * 
	 * @return
	 */
	public String getStreamId();
	
	/**
	 * 
	 * @return
	 */
	public String getResource();
	
	/**
	 * 
	 * @return
	 */
	public PrivacyList getActivePrivacyList();
	
	/**
	 * 
	 * @param stanza
	 */
	public void sendToOtherUser(XmlStanza stanza);
	
	/**
	 * 
	 * @return
	 */
	public boolean isSessionBinded();
	
	/**
	 * 
	 * @param stanza
	 */
	public void sendToSelfClient(XmlStanza stanza);
	
	/**
	 * 
	 * @return
	 */
	public Presence getPresence();

	/**
	 * 
	 * @return
	 */
	public boolean isAvailable();

	/**
	 * 
	 * @return
	 */
	public int getPriority();

	/**
	 * 
	 * @param presence
	 */
	public void setPresence(Presence presence);
	
	/**
	 * 
	 */
	public void logOut();
}
