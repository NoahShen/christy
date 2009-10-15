package net.sf.christy.xmppparser;


import net.sf.christy.xmpp.PacketExtension;

import org.xmlpull.v1.XmlPullParser;



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
