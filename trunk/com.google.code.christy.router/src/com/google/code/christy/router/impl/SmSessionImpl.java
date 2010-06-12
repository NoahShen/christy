/**
 * 
 */
package com.google.code.christy.router.impl;

import java.net.SocketAddress;

import org.apache.mina.common.IoSession;

import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.router.SmSession;
import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.xmpp.CloseStream;


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

	private RouterToSmInterceptorServiceTracker routerToSmInterceptorServiceTracker;

	/**
	 * @param internalStreamId
	 * @param smName
	 * @param iosession
	 * @param routerManager
	 * @param routerToSmInterceptorServiceTracker 
	 */
	public SmSessionImpl(String internalStreamId, String smName, IoSession iosession, 
						RouterManagerImpl routerManager, 
						RouterToSmInterceptorServiceTracker routerToSmInterceptorServiceTracker)
	{
		this.internalStreamId = internalStreamId;
		this.smName = smName;
		this.iosession = iosession;
		this.routerManager = routerManager;
		this.routerToSmInterceptorServiceTracker = routerToSmInterceptorServiceTracker;
		routerManager.addSmSession(smName, this);
	}

	@Override
	public synchronized void close()
	{
		if (iosession.isConnected())
		{
			iosession.write(CloseStream.getCloseStream());
			iosession.close();
		}
		
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
	public void write(RouteMessage routeMessage)
	{
		if (routerToSmInterceptorServiceTracker.fireRouteMessageSent(routerManager, routeMessage, this))
		{
			return;
		}
		
		iosession.write(routeMessage.toXml());
	}

	@Override
	public void write(String xml)
	{
		iosession.write(xml);
	}

}
