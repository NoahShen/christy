/**
 * 
 */
package net.sf.christy.xmpp;

import java.io.Serializable;

/**
 * @author noah
 *
 */
public interface XMLStanza extends Serializable, Cloneable
{

	/**
	 * Returns the XML reppresentation of the data.
	 * 
	 * @return the xml stanza.
	 */
	public String toXML();
	
	/**
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException;
}
