package net.sf.christy.xmpp;

/**
 * IQ packet that will be sent to the server to establish a session.
 * 
 * @see http://www.xmpp.org/specs/rfc3921.html
 * @author noah
 */
public class IqSession implements PacketExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1308104909346824034L;

	public IqSession()
	{
	}

	@Override
	public String getElementName()
	{
		return "session";
	}

	@Override
	public String getNamespace()
	{
		return "urn:ietf:params:xml:ns:xmpp-session";
	}

	@Override
	public String toXML()
	{
		return "<" + getElementName() + " xmlns=\"" + getNamespace() + "\"/>";
	}


	@Override
	public Object clone() throws CloneNotSupportedException
	{
		IqSession session = (IqSession) super.clone();
		return session;
	}
	
	
}