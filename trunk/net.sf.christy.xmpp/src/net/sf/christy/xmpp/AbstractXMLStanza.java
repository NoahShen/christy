/**
 * 
 */
package net.sf.christy.xmpp;

import net.sf.christy.util.StringUtils;


/**
 * @author noah
 * 
 */
public abstract class AbstractXMLStanza implements XMLStanza
{
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

	private String stanzaID;

	/**
	 * @return the stanzaID
	 */
	public String getStanzaID()
	{
		if (ID_NOT_AVAILABLE.equals(stanzaID))
		{
			return null;
		}

		if (stanzaID == null)
		{
			stanzaID = nextID();
		}
		return stanzaID;
	}

	/**
	 * @param stanzaID
	 *                  the stanzaID to set
	 */
	public void setStanzaID(String stanzaID)
	{
		this.stanzaID = stanzaID;
	}

	/* (non-Javadoc)
	 * @see net.sf.mxlosgi.mxlosgixmppbundle.XMLStanza#toXML()
	 */
	@Override
	public String toXML()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		AbstractXMLStanza stanza = (AbstractXMLStanza) super.clone();
		stanza.setStanzaID(this.stanzaID);
		return stanza;
	}
	
	

}
