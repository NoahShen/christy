/**
 * 
 */
package com.google.code.christy.xmpp.disco;

import com.google.code.christy.xmpp.JID;

/**
 * @author Noah
 * 
 */
public class DiscoItem
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6097450110553506370L;

	private JID jid;

	private String node;

	private String name;

	public DiscoItem(JID jid)
	{
		this.jid = jid;
	}

	public DiscoItem(JID jid, String name)
	{
		this.jid = jid;
		this.name = name;
	}

	/**
	 * @return the jid
	 */
	public JID getJid()
	{
		return jid;
	}

	/**
	 * @param jid
	 *                  the jid to set
	 */
	public void setJid(JID jid)
	{
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
	 * @param node
	 *                  the node to set
	 */
	public void setNode(String node)
	{
		this.node = node;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name
	 *                  the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}
}
