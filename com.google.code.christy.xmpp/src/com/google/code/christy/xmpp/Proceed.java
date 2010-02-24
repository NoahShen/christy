package com.google.code.christy.xmpp;

/**
 * TLS proceed element
 * @author noah
 *
 */
public class Proceed implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8229318942917414106L;


	public Proceed()
	{
	}
	
	
	@Override
	public String toXml()
	{
		return "<proceed xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>";
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Proceed proceed = (Proceed) super.clone();
		return proceed;
	}
	
	
}
