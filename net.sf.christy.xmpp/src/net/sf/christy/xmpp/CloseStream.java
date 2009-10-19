/**
 * 
 */
package net.sf.christy.xmpp;

/**
 * @author noah
 *
 */
public class CloseStream implements XmlStanza
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5140309785638570439L;

	private static CloseStream closeStream = new CloseStream();
	
	public static CloseStream getCloseStream()
	{
		return closeStream;
	}
	/* (non-Javadoc)
	 * @see net.sf.christy.christyxmppbundle.XMLData#toXML()
	 */
	@Override
	public String toXml()
	{
		return "</stream:stream>";
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	
}
