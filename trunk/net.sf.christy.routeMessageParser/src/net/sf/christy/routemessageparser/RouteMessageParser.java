/**
 * 
 */
package net.sf.christy.routemessageparser;

import org.xmlpull.v1.XmlPullParser;

import net.sf.christy.routemessage.RouteMessage;

/**
 * @author noah
 *
 */
public interface RouteMessageParser
{
	/**
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	public RouteMessage parseXml(String xml) throws Exception;
	
	/**
	 * 
	 * @param parser
	 * @return
	 * @throws Exception
	 */
	public RouteMessage parseParser(XmlPullParser parser) throws Exception;
	
	/**
	 * 
	 * @param elementName
	 * @param namespace
	 * @return
	 */
	public RouteExtensionParser getRouteExtensionParser(String elementName, String namespace);

}
