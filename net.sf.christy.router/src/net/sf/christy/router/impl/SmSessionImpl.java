/**
 * 
 */
package net.sf.christy.router.impl;

import java.net.SocketAddress;

import org.apache.mina.common.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.router.SmSession;
import net.sf.christy.util.AbstractPropertied;
import net.sf.christy.xmpp.CloseStream;

/**
 * @author noah
 *
 */
public class SmSessionImpl extends AbstractPropertied implements SmSession
{
	private final Logger logger = LoggerFactory.getLogger(SmSessionImpl.class);
	
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
	public void write(RouteMessage routeMessage)
	{
		if (routerToSmInterceptorServiceTracker.fireRouteMessageSent(routeMessage, this))
		{
			logger.debug("Message which will send to " + iosession + "has been intercepted.Message:" + routeMessage.toXml());
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
