/**
 * 
 */
package net.sf.christy.sm;

import net.sf.christy.util.Propertied;
import net.sf.christy.xmpp.JID;
import net.sf.christy.xmpp.Presence;
import net.sf.christy.xmpp.PrivacyList;
import net.sf.christy.xmpp.XmlStanza;

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
