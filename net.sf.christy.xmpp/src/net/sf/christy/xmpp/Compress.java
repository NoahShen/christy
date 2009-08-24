/**
 * 
 */
package net.sf.christy.xmpp;

/**
 * @author noah
 * 
 */
public class Compress implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2787727441420788808L;

	private String compressMethod;

	/**
	 * @param compressMethod
	 */
	public Compress(String compressMethod)
	{
		if (compressMethod == null)
		{
			throw new NullPointerException("compressMehtod is null");
		}
		this.compressMethod = compressMethod;
	}

	/**
	 * @return the compressMethod
	 */
	public String getCompressMethod()
	{
		return compressMethod;
	}

	/**
	 * @param compressMethod
	 *                  the compressMethod to set
	 */
	public void setCompressMethod(String compressMethod)
	{
		if (compressMethod == null)
		{
			throw new NullPointerException("compressMehtod is null");
		}
		this.compressMethod = compressMethod;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.mxlosgi.mxlosgixmppbundle.XMLStanza#toXML()
	 */
	@Override
	public String toXML()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<compress xmlns=\"http://jabber.org/protocol/compress\">").append("<method>").append(compressMethod).append(
				"</method>").append("</compress>");
		return buf.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Compress comress = (Compress) super.clone();
		comress.compressMethod = this.compressMethod;
		return comress;
	}

}
