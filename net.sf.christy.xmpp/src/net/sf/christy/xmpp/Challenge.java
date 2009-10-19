package net.sf.christy.xmpp;

/**
 * @author noah
 *
 */
public class Challenge implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2696055554671963974L;
	
	private String content;

	public Challenge()
	{
	}

	public Challenge(String content)
	{
		this.content = content;
	}

	/**
	 * @return the content
	 */
	public String getContent()
	{
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content)
	{
		this.content = content;
	}
	
	@Override
	public String toXml()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<challenge xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\">").append(getContent()).append("</challenge>");
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		// TODO Auto-generated method stub
		Challenge challenge = (Challenge) super.clone();
		challenge.setContent(this.content);
		
		return challenge;
	}
	
	
}
