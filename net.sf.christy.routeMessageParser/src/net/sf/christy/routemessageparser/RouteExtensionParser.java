/**
 * 
 */
package net.sf.christy.routemessageparser;

import net.sf.christy.routemessage.RouteExtension;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author noah
 * 
 */
public interface RouteExtensionParser
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
	public RouteExtension parseExtension(XmlPullParser parser, RouteMessageParser routeParser) throws Exception;
}
