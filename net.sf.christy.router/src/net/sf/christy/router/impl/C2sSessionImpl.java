package net.sf.christy.router.impl;

import java.net.SocketAddress;

import org.apache.mina.common.IoSession;

import net.sf.christy.router.C2sSession;
import net.sf.christy.util.AbstractPropertied;
import net.sf.christy.xmpp.XMLStanza;

public class C2sSessionImpl extends AbstractPropertied implements C2sSession
{
	private String internalStreamId;
	
	private String c2sName;
	
	private IoSession iosession;
	

	/**
	 * @param internalStreamId
	 * @param name
	 * @param iosession
	 */
	public C2sSessionImpl(String internalStreamId, String name, IoSession iosession)
	{
		this.internalStreamId = internalStreamId;
		c2sName = name;
		this.iosession = iosession;
	}

	@Override
	public void close()
	{
		iosession.close();
	}

	@Override
	public SocketAddress getC2sAddress()
	{
		return iosession.getRemoteAddress();
	}

	@Override
	public String getC2sName()
	{
		return c2sName;
	}

	@Override
	public String getInternalStreamId()
	{
		return internalStreamId;
	}

	@Override
	public boolean isConnected()
	{
		return iosession.isConnected();
	}

	@Override
	public void write(XMLStanza stanza)
	{
		iosession.write(stanza);
	}

}
