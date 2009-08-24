package net.sf.christy.xmpp;

/**
 * Auth element used when sasl
 * @author noah
 *
 */
public class Auth implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2051249386226034071L;

	private String mechanism;
	
	private String content;

	public Auth()
	{
	}

	/**
	 * @return the mechanism
	 */
	public String getMechanism()
	{
		return mechanism;
	}

	/**
	 * @param mechanism the mechanism to set
	 */
	public void setMechanism(String mechanism)
	{
		this.mechanism = mechanism;
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
	public String toXML()
	{
		StringBuffer buf = new StringBuffer();
		
		buf.append("<auth");
		if (getMechanism() != null)
		{
			buf.append(" mechanism=\"").append(getMechanism()).append("\"");
		}
		buf.append(" xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"");
		if (getContent() != null)
		{
			buf.append(">");
			buf.append(getContent());
			buf.append("</auth>");
		}
		else
		{
			buf.append("/>");
		}
		
		
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Auth auth = (Auth) super.clone();
		auth.setMechanism(this.mechanism);
		auth.setContent(this.content);
		
		return auth;
	}
}
