/**
 * 
 */
package com.google.code.christy.xmppparser;

import com.google.code.christy.xmpp.PacketExtension;



/**
 * @author noah
 *
 */
public class UnknownPacketExtension implements PacketExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5857737950098970484L;

	private String elementName;
	
	private String namespace;
	
	private String content;

	/**
	 * @param elementName
	 * @param namespace
	 */
	public UnknownPacketExtension(String elementName, String namespace)
	{
		this.elementName = elementName;
		this.namespace = namespace;
	}

	
	public String getContent()
	{
		return content;
	}


	public void setContent(String content)
	{
		this.content = content;
	}


	/* (non-Javadoc)
	 * @see net.sf.christy.christyxmppbundle.PacketExtension#getElementName()
	 */
	@Override
	public String getElementName()
	{
		return elementName;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.christyxmppbundle.PacketExtension#getNamespace()
	 */
	@Override
	public String getNamespace()
	{
		return namespace;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.christyxmppbundle.XMLData#toXML()
	 */
	@Override
	public String toXml()
	{
		return content;
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		UnknownPacketExtension extension = (UnknownPacketExtension) super.clone();
		extension.elementName = this.elementName;
		extension.namespace = this.namespace;
		extension.content = this.content;
		return extension;
	}
	
	

}
