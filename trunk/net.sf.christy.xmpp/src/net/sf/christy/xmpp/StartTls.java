/**
 * 
 */
package net.sf.christy.xmpp;

/**
 * @author noah
 *
 */
public class StartTls implements XmlStanza
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1477469171102671284L;

	public StartTls()
	{
	}
	/* (non-Javadoc)
	 * @see net.sf.christy.christyxmppbundle.XMLData#toXML()
	 */
	@Override
	public String toXml()
	{
		return "<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>";
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		StartTls starttls = (StartTls) super.clone();
		return starttls;
	}

}
