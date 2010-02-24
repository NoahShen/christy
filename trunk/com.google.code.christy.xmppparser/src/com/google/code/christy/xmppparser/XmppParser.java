/**
 * 
 */
package com.google.code.christy.xmppparser;



import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.xmpp.PacketExtension;
import com.google.code.christy.xmpp.XmlStanza;



/**
 * @author noah
 *
 */
public interface XmppParser
{
	
	/**
	 * 
	 * @param xml
	 * @return
	 * @throws XmlPullParserException
	 */
	public XmlStanza parseXml(String xml) throws Exception;
	
	/**
	 * 
	 * @param parser
	 * @return
	 * @throws Exception
	 */
	public XmlStanza parseParser(XmlPullParser parser) throws Exception;
	
	/**
	 * 
	 * @param parser
	 * @param elementName
	 * @param namespace
	 * @return
	 * @throws Exception
	 */
	public PacketExtension parseUnknownExtension(XmlPullParser parser, String elementName, String namespace) throws Exception;
	
	/**
	 * 
	 * @param elementName
	 * @param namespace
	 * @return
	 */
	public ExtensionParser getExtensionParser(String elementName, String namespace);
}
