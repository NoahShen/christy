/**
 * 
 */
package net.sf.christy.router.impl;

import java.net.SocketAddress;

import org.apache.mina.common.IoSession;

import net.sf.christy.router.SmSession;
import net.sf.christy.util.AbstractPropertied;
import net.sf.christy.xmpp.CloseStream;
import net.sf.christy.xmpp.XmlStanza;

/**
 * @author noah
 *
 */
public class SmSessionImpl extends AbstractPropertied implements SmSession
{
	private String internalStreamId;
	
	private String smName;
	
	private IoSession iosession;
	
	private RouterManagerImpl routerManager;

	/**
	 * @param internalStreamId
	 * @param smName
	 * @param iosession
	 * @param routerManager
	 */
	public SmSessionImpl(String internalStreamId, String smName, IoSession iosession, RouterManagerImpl routerManager)
	{
		this.internalStreamId = internalStreamId;
		this.smName = smName;
		this.iosession = iosession;
		this.routerManager = routerManager;
		routerManager.addSmSession(smName, this);
	}

	@Override
	public void close()
	{
		iosession.write(CloseStream.getCloseStream());
		iosession.close();
		routerManager.removeSmSession(smName);
	}

	@Override
	public String getInternalStreamId()
	{
		return internalStreamId;
	}


	@Override
	public SocketAddress getSmAddress()
	{
		return iosession.getRemoteAddress();
	}

	
	@Override
	public String getSmName()
	{
		return smName;
	}

	@Override
	public boolean isConnected()
	{
		return iosession.isConnected();
	}

	@Override
	public void write(XmlStanza stanza)
	{
		iosession.write(stanza);
	}

	@Override
	public void write(String xml)
	{
		iosession.write(xml);
	}

}
