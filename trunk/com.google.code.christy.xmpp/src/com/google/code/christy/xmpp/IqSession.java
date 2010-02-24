package com.google.code.christy.xmpp;

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

	public static final String ELEMENTNAME = "session";
	
	public static final String NAMESPACE = "urn:ietf:params:xml:ns:xmpp-session";
	
	public IqSession()
	{
	}

	@Override
	public String getElementName()
	{
		return ELEMENTNAME;
	}

	@Override
	public String getNamespace()
	{
		return NAMESPACE;
	}

	@Override
	public String toXml()
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