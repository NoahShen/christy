package com.google.code.christy.router.impl;

import java.net.SocketAddress;

import org.apache.mina.common.IoSession;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.router.C2sSession;
import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.xmpp.CloseStream;


public class C2sSessionImpl extends AbstractPropertied implements C2sSession
{
	private String internalStreamId;
	
	private String c2sName;
	
	private IoSession iosession;
	
	private RouterManagerImpl routerManager;
	
	/**
	 * 
	 * @param internalStreamId
	 * @param c2sName
	 * @param iosession
	 * @param routerManager
	 */
	public C2sSessionImpl(String internalStreamId, String c2sName, IoSession iosession, RouterManagerImpl routerManager)
	{
		this.internalStreamId = internalStreamId;
		this.c2sName = c2sName;
		this.iosession = iosession;
		this.routerManager = routerManager;
		routerManager.addC2sSession(c2sName, this);
	}

	@Override
	public synchronized void close()
	{
		if (iosession.isConnected())
		{
			iosession.write(CloseStream.getCloseStream());
			iosession.close();
		}
		routerManager.removeC2sSession(c2sName);
		
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
	public void write(RouteMessage routeMessage)
	{
		iosession.write(routeMessage.toXml());
	}

	@Override
	public void write(String xml)
	{
		iosession.write(xml);
	}

}
