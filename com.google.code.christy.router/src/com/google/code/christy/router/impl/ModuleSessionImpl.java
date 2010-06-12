package com.google.code.christy.router.impl;

import java.net.SocketAddress;

import org.apache.mina.common.IoSession;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.router.ModuleSession;
import com.google.code.christy.xmpp.CloseStream;

public class ModuleSessionImpl implements ModuleSession
{
	private String internalStreamId;
	
	private String subDomain;
	
	private IoSession iosession;
	
	private RouterManagerImpl routerManager;
	
	
	/**
	 * @param internalStreamId
	 * @param subDomain
	 * @param iosession
	 * @param routerManager
	 */
	public ModuleSessionImpl(String internalStreamId, String subDomain, IoSession iosession, RouterManagerImpl routerManager)
	{
		this.internalStreamId = internalStreamId;
		this.subDomain = subDomain;
		this.iosession = iosession;
		this.routerManager = routerManager;
		routerManager.addModuleSession(subDomain, this);
	}

	@Override
	public synchronized void close()
	{
		if (iosession.isConnected())
		{
			iosession.write(CloseStream.getCloseStream());
			iosession.close();
		}
		routerManager.removeModuleSession(subDomain);
	}

	@Override
	public String getInternalStreamId()
	{
		return internalStreamId;
	}

	@Override
	public SocketAddress getModuleAddress()
	{
		return iosession.getRemoteAddress();
	}

	@Override
	public String getSubDomain()
	{
		return subDomain;
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
