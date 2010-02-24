package com.google.code.christy.xmpp;

/**
 * 
 * @author noah
 * 
 */
public interface PacketExtension extends XmlStanza
{

	/**
	 * Returns the root element name.
	 * 
	 * @return the element name.
	 */
	public String getElementName();

	/**
	 * Returns the root element XML namespace.
	 * 
	 * @return the namespace.
	 */
	public String getNamespace();

}