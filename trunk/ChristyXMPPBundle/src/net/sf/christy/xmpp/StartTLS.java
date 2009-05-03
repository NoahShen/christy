/**
 * 
 */
package net.sf.christy.xmpp;

/**
 * @author noah
 *
 */
public class StartTLS implements XMLStanza
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1477469171102671284L;

	public StartTLS()
	{
	}
	/* (non-Javadoc)
	 * @see net.sf.christy.christyxmppbundle.XMLData#toXML()
	 */
	@Override
	public String toXML()
	{
		return "<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>";
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		StartTLS starttls = (StartTLS) super.clone();
		return starttls;
	}

}
