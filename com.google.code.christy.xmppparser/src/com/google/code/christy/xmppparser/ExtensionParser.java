package com.google.code.christy.xmppparser;



import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.xmpp.PacketExtension;



/**
 * @author noah
 *
 */
public interface ExtensionParser
{
	/**
	 * 
	 * @return
	 */
	public String getElementName();
	
	/**
	 * 
	 * @return
	 */
	public String getNamespace();
	
	/**
	 * 
	 * @param parser
	 * @return
	 */
	public PacketExtension parseExtension(XmlPullParser parser, XmppParser xmppParser) throws Exception;
}
