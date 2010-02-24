/**
 * 
 */
package com.google.code.christy.routemessage;

import com.google.code.christy.xmpp.XmlStanza;

/**
 * @author Noah
 * 
 */
public interface RouteExtension  extends XmlStanza
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
	 * @return
	 */
	public String toXml();

	/**
	 * 
	 * @return
	 * @throws CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException;
}
