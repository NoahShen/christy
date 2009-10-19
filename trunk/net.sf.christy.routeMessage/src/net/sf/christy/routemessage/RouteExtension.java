/**
 * 
 */
package net.sf.christy.routemessage;

import java.io.Serializable;

/**
 * @author Noah
 * 
 */
public interface RouteExtension extends Serializable, Cloneable
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
