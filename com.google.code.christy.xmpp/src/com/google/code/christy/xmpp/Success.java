package com.google.code.christy.xmpp;

/**
 * SASL success
 * 
 * @author noah
 * 
 */
public class Success implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 83387371059249600L;

	private String successData;
	
	public Success()
	{

	}

	public String getSuccessData()
	{
		return successData;
	}

	public void setSuccessData(String successData)
	{
		this.successData = successData;
	}

	@Override
	public String toXml()
	{
		StringBuffer buf = new StringBuffer();

		buf.append("<success xmlns=\"urn:ietf:params:xml:ns:xmpp-sasl\"");
		
		if (getSuccessData() != null)
		{
			buf.append(">").append(getSuccessData()).append("</success>");
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
		Success success = (Success) super.clone();
		success.successData = this.successData;
		return success;
	}
	
	
}
