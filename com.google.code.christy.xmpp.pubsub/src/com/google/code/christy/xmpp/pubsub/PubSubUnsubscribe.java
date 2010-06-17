package com.google.code.christy.xmpp.pubsub;

import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.XmlStanza;

public class PubSubUnsubscribe implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2998602175339265133L;

	private String node;
	
	private JID jid;
	
	private String subId;

	/**
	 * @param node
	 * @param jid
	 */
	public PubSubUnsubscribe(String node, JID jid)
	{
		this.node = node;
		this.jid = jid;
	}

	/**
	 * @return the node
	 */
	public String getNode()
	{
		return node;
	}

	/**
	 * @return the jid
	 */
	public JID getJid()
	{
		return jid;
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
		buf.append("<unsubscribe");
		
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
		
		buf.append("/>");
		
		return buf.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PubSubUnsubscribe pubSubUnsubscribe = (PubSubUnsubscribe) super.clone();
		pubSubUnsubscribe.node = this.node;
		pubSubUnsubscribe.jid = this.jid;
		return pubSubUnsubscribe;
	}
	

}
