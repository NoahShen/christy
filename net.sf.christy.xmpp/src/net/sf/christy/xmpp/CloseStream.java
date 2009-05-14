/**
 * 
 */
package net.sf.christy.xmpp;

/**
 * @author noah
 *
 */
public class CloseStream implements XMLStanza
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5140309785638570439L;

	/* (non-Javadoc)
	 * @see net.sf.christy.christyxmppbundle.XMLData#toXML()
	 */
	@Override
	public String toXML()
	{
		return "</stream:stream>";
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}

	
}
