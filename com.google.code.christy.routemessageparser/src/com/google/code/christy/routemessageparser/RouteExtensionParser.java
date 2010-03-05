/**
 * 
 */
package com.google.code.christy.routemessageparser;


import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.routemessage.RouteExtension;

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
