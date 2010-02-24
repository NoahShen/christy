/**
 * 
 */
package com.google.code.christy.xmpp;

import com.google.code.christy.util.StringUtils;


/**
 * @author noah
 * 
 */
public abstract class AbstractXmlStanza implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3209002697000842775L;

	/**
	 * Constant used as packetID to indicate that a packet has no id. To
	 * indicate that a packet has no id set this constant as the packet's
	 * id. When the packet is asked for its id the answer will be
	 * <tt>null</tt>.
	 */
	public static final String ID_NOT_AVAILABLE = "ID_NOT_AVAILABLE";

	/**
	 * A prefix helps to make sure that ID's are unique across mutliple
	 * instances.
	 */
	private static String prefix = StringUtils.randomString(5) + "-";

	/**
	 * Keeps track of the current increment, which is appended to the
	 * prefix to forum a unique ID.
	 */
	private static long id = 0;

	/**
	 * Returns the next unique id. Each id made up of a short alphanumeric
	 * prefix along with a unique numeric value.
	 * 
	 * @return the next id.
	 */
	public static synchronized String nextID()
	{
		return prefix + Long.toString(id++);
	}

	private String stanzaId;

	/**
	 * @return the stanzaID
	 */
	public String getStanzaId()
	{
		if (ID_NOT_AVAILABLE.equals(stanzaId))
		{
			return null;
		}

		if (stanzaId == null)
		{
			stanzaId = nextID();
		}
		return stanzaId;
	}

	/**
	 * @param stanzaId
	 *                  the stanzaID to set
	 */
	public void setStanzaId(String stanzaId)
	{
		this.stanzaId = stanzaId;
	}


	@Override
	public Object clone() throws CloneNotSupportedException
	{
		AbstractXmlStanza stanza = (AbstractXmlStanza) super.clone();
		stanza.setStanzaId(this.stanzaId);
		return stanza;
	}
	
	

}
