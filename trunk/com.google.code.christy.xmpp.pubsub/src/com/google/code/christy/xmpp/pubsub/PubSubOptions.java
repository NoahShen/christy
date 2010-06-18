package com.google.code.christy.xmpp.pubsub;

import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.XmlStanza;
import com.google.code.christy.xmpp.dataform.DataForm;

public class PubSubOptions implements XmlStanza
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7128024182007619838L;

	private String node;
	
	private JID jid;
	
	private String subId;
	
	private DataForm dataForm;
	
	public PubSubOptions()
	{
	}

	public PubSubOptions(String node, JID jid)
	{
		super();
		this.node = node;
		this.jid = jid;
	}

	public void setNode(String node)
	{
		this.node = node;
	}

	public void setJid(JID jid)
	{
		this.jid = jid;
	}

	public String getNode()
	{
		return node;
	}

	public JID getJid()
	{
		return jid;
	}

	public DataForm getDataForm()
	{
		return dataForm;
	}

	public void setDataForm(DataForm dataForm)
	{
		this.dataForm = dataForm;
	}

	/**
	 * @return the subId
	 */
	public String getSubId()
	{
		return subId;
	}

	/**
	 * @param subId the subId to set
	 */
	public void setSubId(String subId)
	{
		this.subId = subId;
	}
	
	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<options");
		
		if (getNode() != null)
		{
			buf.append(" node=\"").append(getNode()).append("\"");
		}
		
		if (getJid() != null)
		{
			buf.append(" jid=\"").append(getJid().toBareJID()).append("\"");
		}
		if (getSubId() != null)
		{
			buf.append(" subid=\"").append(getSubId()).append("\"");
		}
		
		if (getDataForm() == null)
		{
			buf.append("/>");
		}
		else
		{
			buf.append(">");
			buf.append(getDataForm().toXml());
			buf.append("</options>");
		}
		
		
		return buf.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PubSubOptions pubSubOptions = (PubSubOptions) super.clone();
		pubSubOptions.node = this.node;
		pubSubOptions.jid = this.jid;
		pubSubOptions.subId = this.subId;
		if (this.dataForm != null)
		{
			pubSubOptions.dataForm = (DataForm) this.dataForm.clone();
		}
		
		return pubSubOptions;
	}
	

}
