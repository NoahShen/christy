/**
 * 
 */
package net.sf.christy.xmpp;

/**
 * @author noah
 *
 */
public class Compressed implements XMLStanza
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8139421041715865783L;

	/* (non-Javadoc)
	 * @see net.sf.mxlosgi.mxlosgixmppbundle.XMLStanza#toXML()
	 */
	@Override
	public String toXML()
	{
		return "<compressed xmlns=\"http://jabber.org/protocol/compress\"/>";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Compressed compressed = (Compressed) super.clone();
		return compressed;
	}

}
