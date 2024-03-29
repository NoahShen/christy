/**
 * 
 */
package net.sf.christy.xmpp;

import java.io.Serializable;

/**
 * @author noah
 *
 */
public interface XmlStanza extends Serializable, Cloneable
{

	/**
	 * Returns the XML reppresentation of the data.
	 * 
	 * @return the xml stanza.
	 */
	public String toXml();
	
	/**
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException;
}
