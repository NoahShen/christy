package net.sf.christy.xmpp;

/**
 * IQ packet used to bind a resource and to obtain the jid assigned by
 * the server.
 * 
 * @see http://www.ietf.org/rfc/rfc3921.txt
 * @author noah
 */
public class IQBind implements PacketExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6690374851149855246L;

	private String resource = null;

	private JID jid = null;


	public IQBind()
	{

	}

	public String getResource()
	{
		return resource;
	}

	/**
	 * set <resource/> element
	 * @param resource
	 */
	public void setResource(String resource)
	{
		this.resource = resource;
	}

	public JID getJid()
	{
		return jid;
	}

	/**
	 * set <JID/> element
	 * @param jid
	 */
	public void setJid(JID jid)
	{
		this.jid = jid;
	}

	@Override
	public String getElementName()
	{
		return "bind";
	}

	@Override
	public String getNamespace()
	{
		return "urn:ietf:params:xml:ns:xmpp-bind";
	}

	@Override
	public String toXML()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<" + getElementName() + " xmlns=\"" + getNamespace() + "\">");
		if (resource != null)
		{
			buf.append("<resource>").append(resource).append("</resource>");
		}
		if (jid != null)
		{
			buf.append("<jid>").append(jid.toFullJID()).append("</jid>");
		}
		buf.append("</" +  getElementName() + ">");
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		IQBind iqBind = (IQBind) super.clone();
		iqBind.setResource(this.resource);
		iqBind.setJid(this.jid);
		return iqBind;
	}
	
	
}
