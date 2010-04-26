package com.google.code.christy.sm.privatexml;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmppparser.UnknownPacketExtension;

public class PrivateXmlExtension implements PacketExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7301480498988191920L;

	public static final String ELEMENTNAME = "query";

	public static final String NAMESPACE = "jabber:iq:private";
	
	private UnknownPacketExtension unknownPacketExtension;
	
	


	/**
	 * @return the unknownPacketExtension
	 */
	public UnknownPacketExtension getUnknownPacketExtension()
	{
		return unknownPacketExtension;
	}

	/**
	 * @param unknownPacketExtension the unknownPacketExtension to set
	 */
	public void setUnknownPacketExtension(UnknownPacketExtension unknownPacketExtension)
	{
		this.unknownPacketExtension = unknownPacketExtension;
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
		StringBuffer buf = new StringBuffer();
		buf.append("<" + getElementName() + " " + "xmlns=\"" + getNamespace() + "\"");
		if (unknownPacketExtension != null)
		{
			buf.append(">");
			buf.append(unknownPacketExtension.toXml());
			buf.append("</" + getElementName() + ">");
		}
		else
		{
			buf.append("/>");
		}
		
		
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PrivateXmlExtension privateXml = (PrivateXmlExtension) super.clone();
		privateXml.unknownPacketExtension = (UnknownPacketExtension) this.unknownPacketExtension.clone();
		return privateXml;
	}
}
