/**
 * 
 */
package net.sf.christy.sm.impl;

import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

import net.sf.christy.mina.XmppCodecFactory;
import net.sf.christy.routemessage.RouteMessage;
import net.sf.christy.sm.OnlineUser;
import net.sf.christy.sm.SmManager;
import net.sf.christy.sm.UserResource;
import net.sf.christy.sm.contactmgr.ContactManager;
import net.sf.christy.sm.contactmgr.OfflineSubscribeMsgDbHelperTracker;
import net.sf.christy.sm.contactmgr.RosterItemDbHelperTracker;
import net.sf.christy.sm.privacy.PrivacyManager;
import net.sf.christy.sm.privacy.UserPrivacyListDbHelperTracker;
import net.sf.christy.sm.user.UserDbHelperTracker;
import net.sf.christy.util.AbstractPropertied;
import net.sf.christy.xmpp.CloseStream;
import net.sf.christy.xmpp.Iq;
import net.sf.christy.xmpp.IqBind;
import net.sf.christy.xmpp.IqRoster;
import net.sf.christy.xmpp.IqSession;
import net.sf.christy.xmpp.JID;
import net.sf.christy.xmpp.Packet;
import net.sf.christy.xmpp.Presence;
import net.sf.christy.xmpp.Privacy;
import net.sf.christy.xmpp.StreamError;
import net.sf.christy.xmpp.XmlStanza;
import net.sf.christy.xmpp.XmppError;

/**
 * @author noah
 *
 */
public class SmManagerImpl extends AbstractPropertied implements SmManager
{
	
	public static final String SMROUTER_NAMESPACE = "christy:internal:sm2router";
	
	public static final String SMROUTER_AUTH_NAMESPACE = "christy:internal:sm2router:auth";

	private final Logger logger = LoggerFactory.getLogger(SmManagerImpl.class);
	
	private String domain;
	
	private String name;
	
	private String routerIp;
	
	private String routerPassword;
	
	private int routerPort = 8789;
	
	private boolean started = false;

	private boolean routerConnected = false;
	
	private SocketConnector routerConnector;

	private IoSession routerSession;
	
	private Map<String, OnlineUserImpl> onlineUsers = new ConcurrentHashMap<String , OnlineUserImpl>();
	
	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;
	
	private SmToRouterInterceptorServiceTracker smToRouterInterceptorServiceTracker;
	
	private SmHandlerServiceTracker smHandlerServiceTracker;
	
	private int onlineUsersLimit = -1;
	
	private int resourceLimitPerUser = -1;
	
	private HandlerManager handlerManager;

	private PrivacyManager privacyManager;

	private ContactManager contactManager;
	
	public SmManagerImpl(RouteMessageParserServiceTracker routeMessageParserServiceTracker, 
						SmToRouterInterceptorServiceTracker smToRouterInterceptorServiceTracker, 
						SmHandlerServiceTracker packetHandlerServiceTracker,
						UserPrivacyListDbHelperTracker userPrivacyListDbHelperTracker, 
						RosterItemDbHelperTracker rosterItemDbHelperTracker,
						OfflineSubscribeMsgDbHelperTracker offlineSubscribeMsgDbHelperTracker,
						UserDbHelperTracker userDbHelperTracker)
	{
		this.routeMessageParserServiceTracker = routeMessageParserServiceTracker;
		this.smToRouterInterceptorServiceTracker = smToRouterInterceptorServiceTracker;
		this.smHandlerServiceTracker = packetHandlerServiceTracker;
		this.handlerManager = new HandlerManager();
		this.contactManager = 
			new ContactManager(rosterItemDbHelperTracker, 
							offlineSubscribeMsgDbHelperTracker, userDbHelperTracker);
		this.privacyManager = new PrivacyManager(this, userPrivacyListDbHelperTracker);
	}
	
	/**
	 * @return the privacyManager
	 */
	public PrivacyManager getPrivacyManager()
	{
		return privacyManager;
	}

	/**
	 * @return the contactManager
	 */
	public ContactManager getContactManager()
	{
		return contactManager;
	}

	@Override
	public void exit()
	{
		stop();
		System.exit(0);
	}

	@Override
	public String getDomain()
	{
		return domain;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getRouterIp()
	{
		return routerIp;
	}

	@Override
	public String getRouterPassword()
	{
		return routerPassword;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.sm.SmManager#getRouterPort()
	 */
	@Override
	public int getRouterPort()
	{
		return routerPort;
	}

	@Override
	public boolean isRouterConnected()
	{
		return routerConnected;
	}

	@Override
	public boolean isStarted()
	{
		return started;
	}

	@Override
	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public void setRouterIp(String routerIp)
	{
		this.routerIp = routerIp;
	}

	/* (non-Javadoc)
	 * @see net.sf.christy.sm.SmManager#setRouterPassword(java.lang.String)
	 */
	@Override
	public void setRouterPassword(String routerPassword)
	{
		this.routerPassword = routerPassword;
	}

	@Override
	public void setRouterPort(int routerPort)
	{
		this.routerPort = routerPort;
	}

	@Override
	public void start()
	{
		if (isStarted())
		{
			throw new IllegalStateException("sm has started");
		}
		logger.info("sm starting...");
		
		if (getName() == null || getName().isEmpty())
		{
			logger.error("name has not been set");
			throw new IllegalStateException("name has not been set");
		}
		
		if (getDomain() == null || getDomain().isEmpty())
		{
			logger.error("domain has not been set");
			throw new IllegalStateException("domain has not been set");
		}
		
		if (getRouterIp() == null || getRouterIp().isEmpty())
		{
			logger.error("routerIp has not been set");
			throw new IllegalStateException("routerIp has not been set");
		}
		
		if (getRouterPassword() == null || getRouterPassword().isEmpty())
		{
			logger.error("routerPassword has not been set");
			throw new IllegalStateException("routerPassword has not been set");
		}
		
		logger.info("connecting to router");
		
		SocketConnectorConfig socketConnectorConfig = new SocketConnectorConfig();
		socketConnectorConfig.setConnectTimeout(30);
		socketConnectorConfig.getFilterChain().addLast("xmppCodec", new ProtocolCodecFilter(new XmppCodecFactory()));
		socketConnectorConfig.setThreadModel(ThreadModel.MANUAL);
		
		routerConnector = new SocketConnector(Runtime.getRuntime().availableProcessors() + 1, Executors.newCachedThreadPool());
		InetSocketAddress address =new InetSocketAddress(getRouterIp(), getRouterPort());
		
		ConnectFuture future = routerConnector.connect(address, new RouterHandler(), socketConnectorConfig);
		if (!future.join(30 * 1000) || !future.isConnected())
		{
			started = false;
			logger.error("c2s starting failure: connecting to router failure");
			exit();
			return;
		}
		
		started = true;
		logger.info("connecting to router successful");
		
	}


	@Override
	public void stop()
	{
		if (!isStarted())
		{
			return;
		}
		
		if (routerSession != null)
		{
			routerSession.close();
		}
		
		routerConnector = null;
		started = false;
	}


	@Override
	public int getNumberOfOnlineUsers()
	{
		return onlineUsers.size();
	}

	@Override
	public OnlineUser getOnlineUser(String node)
	{
		return onlineUsers.get(node);
	}

	@Override
	public UserResource getUserResource(String node, String resource)
	{
		OnlineUserImpl onlineUser = (OnlineUserImpl) getOnlineUser(node);
		if (onlineUser != null)
		{
			onlineUser.getUserResource(resource);
		}
		return null;
	}

	@Override
	public void removeOnlineUser(OnlineUser onlineUser)
	{
		onlineUsers.remove(onlineUser.getNode());
	}
	
	@Override
	public void removeOnlineUser(String node)
	{
		onlineUsers.remove(node);
	}
	

	@Override
	public UserResource createUserResource(String userNode, String resource, String relatedC2s, String streamId)
	{
		if (getOnlineUsersLimit() > 0)
		{
			if (onlineUsers.size() == getOnlineUsersLimit())
			{
				throw new IllegalStateException("reach onlineuser limit");
			}
		}
		
		OnlineUserImpl onlineUser = (OnlineUserImpl) getOnlineUser(userNode);
		if (onlineUser == null)
		{
			onlineUser = new OnlineUserImpl(userNode, this);
			onlineUsers.put(userNode, onlineUser);
		}
		
		if (getResourceLimitPerUser() > 0)
		{
			if (onlineUser.getResourceCount() == getResourceLimitPerUser())
			{
				throw new IllegalStateException("reach resourceLimitPerUser");
			}
		}
		
		UserResourceImpl userResource = 
			new UserResourceImpl(onlineUser, resource, relatedC2s, streamId, this);
		
		onlineUser.addUserResource(userResource);
		
		fireUserResourceAdded(onlineUser, userResource);
		
		return userResource;
	}
	
	private void fireUserResourceAdded(OnlineUserImpl onlineUser, UserResourceImpl userResource)
	{
		privacyManager.userResourceAdded(onlineUser, userResource);
		smHandlerServiceTracker.userResourceAdded(this, onlineUser, userResource);
	}
	
	void fireUserResourceRemoved(OnlineUserImpl onlineUser, UserResourceImpl userResource)
	{
		privacyManager.userResourceRemoved(onlineUser, userResource);
		smHandlerServiceTracker.userResourceRemoved(this, onlineUser, userResource);
	}


	public void fireUserResourceAvailable(OnlineUser onlineUser, UserResource userResource)
	{
		smHandlerServiceTracker.userResourceAvailable(this, onlineUser, userResource);
	}
	
	@Override
	public boolean containUserResource(String userNode, String resource)
	{
		OnlineUserImpl onlineUser = onlineUsers.get(userNode);
		if (onlineUser != null)
		{
			return onlineUser.containUserResource(resource);
		}
		return false;
	}
	
	@Override
	public void removeUserResource(String node, String resource)
	{
		OnlineUserImpl onlineUser = onlineUsers.get(node);
		if (onlineUser != null)
		{
			onlineUser.removeUserResource(resource);
		}
	}
	
	@Override
	public int getOnlineUsersLimit()
	{
		return onlineUsersLimit;
	}

	@Override
	public int getResourceLimitPerUser()
	{
		return resourceLimitPerUser;
	}

	@Override
	public void setOnlineUsersLimit(int onlineUsersLimit)
	{
		if (isStarted())
		{
			throw new IllegalStateException("sm has started");
		}
		if (onlineUsersLimit < 0)
		{
			throw new IllegalArgumentException("onlineUsersLimit must be > 0");
		}
		this.onlineUsersLimit = onlineUsersLimit;
	}

	@Override
	public void setResourceLimitPerUser(int resourceLimitPerUser)
	{
		if (isStarted())
		{
			throw new IllegalStateException("sm has started");
		}
		if (resourceLimitPerUser < 0)
		{
			throw new IllegalArgumentException("resourceLimitPerUser must be > 0");
		}
		this.resourceLimitPerUser = resourceLimitPerUser;
	}
	

	@Override
	public void sendToRouter(RouteMessage routeMessage)
	{
		String userNode = routeMessage.getToUserNode();
		OnlineUser user = null;
		if (userNode != null)
		{
			user = getOnlineUser(userNode);
		}
		
		
		if (smToRouterInterceptorServiceTracker.fireSmMessageSent(routeMessage, SmManagerImpl.this, user))
		{
			logger.debug("Message which will send to router"
						+ "has been intercepted.Message:"
						+ routeMessage.toXml());
			return;
		}
		
		routerSession.write(routeMessage.toXml());		
	}

	
	private class RouterHandler implements IoHandler
	{

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
			logger.debug("session" + session + ": exceptionCaught:" + cause.getMessage());
			cause.printStackTrace();
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception
		{
			logger.debug("session" + session + ": messageReceived:\n" + message);

			String xml = message.toString();
			if (xml.equals("</stream:stream>"))
			{
				session.close();
				return;
			}

			StringReader strReader = new StringReader(xml);
			XmlPullParser parser = new MXParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
			parser.setInput(strReader);

			try
			{
				parser.nextTag();
			}
			catch (Exception e)
			{
				// e.printStackTrace();
				throw e;
			}

			String elementName = parser.getName();
			if ("stream".equals(elementName) || "stream:stream".equals(elementName))
			{
				handleStream(parser, session);
			}
			else if ("success".equals(elementName))
			{
				routerSession = session;
				routerConnected = true;
				logger.info("router auth successful");
			}
			else if ("failed".equals(elementName))
			{
				logger.error("password error");
				session.close();
			}
			else if ("route".equals(elementName))
			{
				RouteMessage routeMessage = 
					routeMessageParserServiceTracker.getRouteMessageParser().parseParser(parser);
				handleRoute(routeMessage, session);
			}

		}

		private void handleRoute(RouteMessage routeMessage, IoSession session)
		{
			String userNode = routeMessage.getToUserNode();
			OnlineUserImpl onlineUser = onlineUsers.get(userNode);
			
			if (smToRouterInterceptorServiceTracker.fireSmMessageReceived(routeMessage, SmManagerImpl.this, onlineUser))
			{
				logger.debug("Message which recieved from "
							+ session + "has been intercepted.Message:"
							+ routeMessage.toXml());
				return;
			}
			transferToHandlerManager(onlineUser, routeMessage);
		}

		private void transferToHandlerManager(OnlineUserImpl onlineUser, RouteMessage routeMessage)
		{
			
			XmlStanza stanza = routeMessage.getXmlStanza();

			String from = routeMessage.getFrom();
			// message from local user's client
			if (from.startsWith("c2s_"))
			{
				String streamId = routeMessage.getStreamId();
				String node = routeMessage.getToUserNode();
				UserResource userResource = null;
				// user == null when user is not online or to=example.com
				if (onlineUser != null)
				{
					userResource = onlineUser.getUserResourceByStreamId(streamId);
				}
				
				MessageQueueWrapper wrapper = new MessageQueueWrapper(routeMessage, true);
				handlerManager.handleWrapper(node, onlineUser, (UserResourceImpl) userResource, wrapper);
			}
			// message from other user
			else
			{
				if (!(stanza instanceof Packet))
				{
					return;
				}
				
				Packet packet = (Packet) stanza;
				JID to = packet.getTo();
				String node = to.getNode();
				String resource = to.getResource();
				if (onlineUser != null)
				{
					UserResource[] resources = new UserResource[]{};
					// to is fullJID
					if (resource != null)
					{
						UserResource userResource = onlineUser.getUserResource(resource);
						
						if (userResource != null
								&& !userResource.isAvailable())
						{
							return;
						}
						
						resources = new UserResource[]{userResource};
					}
					else
					{
						resources = smHandlerServiceTracker.checkResource(SmManagerImpl.this, onlineUser, packet);
						if (resources == null)
						{
							resources = new UserResource[]{null};
						}
					}
					
					
					for (UserResource res : resources)
					{
						
						//block
						if (privacyManager.shouldBlockReceivePacket(onlineUser, res, packet))
						{
							if (logger.isDebugEnabled())
							{
								logger.debug("block" + onlineUser.getNode() + "/" + 
											res.getResource() + 
											" receivePacket:" + packet.toXml());
							}
							
							return;
						}
						
						MessageQueueWrapper wrapper = new MessageQueueWrapper(routeMessage, false);
						handlerManager.handleWrapper(node, onlineUser, (UserResourceImpl) res, wrapper);
					}
					
					return;
				}

				// no resource online
				MessageQueueWrapper wrapper = new MessageQueueWrapper(routeMessage, false);
				handlerManager.handleWrapper(node, onlineUser, null, wrapper);
				
			}
			
						
			
			
		}
		

		private void handleStream(XmlPullParser parser, IoSession session)
		{
			String xmlns = parser.getNamespace(null);
			String from = parser.getAttributeValue("", "from");
			String id = parser.getAttributeValue("", "id");
			
			if (!SMROUTER_NAMESPACE.equals(xmlns))
			{
				logger.error("namespace error:" + xmlns);
				session.close();
				return;
			}
			if (!"router".equals(from))
			{
				logger.error("from error:" + from);
				session.close();
				return;
			}
			session.setAttribute("streamId", id);
			
			logger.debug("open stream successful");
			
			// TODO test code
			String smname = getName();
			if (id.endsWith("1"))
			{
				smname += "_1";
			}
			session.write("<internal xmlns='" + SMROUTER_AUTH_NAMESPACE + "'" +
						" smname='" + smname + "' password='" + getRouterPassword() + "'/>");
		}
		
		@Override
		public void messageSent(IoSession session, Object message) throws Exception
		{
			if (logger.isDebugEnabled())
			{
				String s = null;
				if (message instanceof String)
				{
					s = message.toString();
				}
				else if (message instanceof XmlStanza)
				{
					s = ((XmlStanza)message).toXml();
				}
				logger.debug("session" + session + ": messageSent:\n" + s);
			}
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionClosed");
			exit();
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionCreated");
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception
		{
			logger.debug("session" + session + ": sessionOpened");
			
			session.write("<stream:stream xmlns='" + SMROUTER_NAMESPACE + "'" +
						" xmlns:stream='http://etherx.jabber.org/streams'" +
						" to='router'" +
						" domain='" + getDomain() + "'>");
		}
	
	}


	/**
	 * confirm there is only one thread which is handling all the packet sent by same user 
	 * @author Noah
	 *
	 */
	private class HandlerManager
	{
		private ListMultimap<String, MessageQueueWrapper> messageQueue;
		
		private Lock lock = new ReentrantLock();
		
		public HandlerManager()
		{
			messageQueue = LinkedListMultimap.create();
			messageQueue = Multimaps.synchronizedListMultimap(messageQueue);
		}
		
		public void handleWrapper(String node, OnlineUserImpl onlineUser, UserResourceImpl resource, MessageQueueWrapper wrapper)
		{
			if (node == null)
			{
				return;
			}
			
			lock.lock();
			try
			{

				if (messageQueue.containsKey(node))
				{
					messageQueue.put(node, wrapper);
					return;
				}
				
				messageQueue.put(node, wrapper);
				
			}
			finally
			{
				lock.unlock();
			}
			
			notifyHandler(onlineUser, node, resource);
		}

		private void notifyHandler(OnlineUserImpl onlineUser, String node, UserResourceImpl userResource)
		{
			MessageQueueWrapper wrapper = getMessage(node);
			do
			{
				
				if (wrapper != null)
				{
					RouteMessage routeMessage = wrapper.getRouteMessage();
					XmlStanza stanza = routeMessage.getXmlStanza();
					if (wrapper.isFromClient())
					{

						if (routeMessage.isCloseStream())
						{
							handleCloseStream(userResource, routeMessage);
						}
						else if (stanza instanceof Iq)
						{
							if (handleIq(onlineUser, userResource, routeMessage, (Iq) stanza))
							{
								return;
							}
						}
						else if (stanza instanceof Presence)
						{
							if (handlePresence(onlineUser, userResource, routeMessage, (Presence) stanza))
							{
								return;
							}
						}
						
						if (!(stanza instanceof Packet))
						{
							return;
						}
						Packet packet = (Packet) stanza;
						if (!smHandlerServiceTracker.handleClientPacket(SmManagerImpl.this, onlineUser, userResource, packet))
						{
							
							JID to = packet.getTo();
							if (to != null && !to.getDomain().equals(getDomain()))
							{
								userResource.sendToOtherUser(packet);
							}
							else
							{
								returnUnsupportError(packet, userResource);
							}
							
						}
					}
					else
					{
						if (stanza instanceof Presence)
						{
							if (handleOtherPresence(onlineUser, userResource, routeMessage, (Presence) stanza))
							{
								return;
							}
						}
						
						if (!(stanza instanceof Packet))
						{
							return;
						}
						Packet packet = (Packet) stanza;
						
						if (!smHandlerServiceTracker.handleOtherUserPacket(SmManagerImpl.this, onlineUser, userResource, packet))
						{
							JID to = packet.getTo();
							if (to != null && !to.getDomain().equals(getDomain()))
							{
								userResource.sendToSelfClient(packet);
							}
							else
							{
								returnUnsupportErrorToOtherUser(packet);
							}
							
						}
					}
					
				}
				
			}
			while((wrapper = getMessage(node)) != null);
		}


		private boolean handleOtherPresence(OnlineUserImpl onlineUser, UserResourceImpl userResource, 
									RouteMessage routeMessage, Presence presence)
		{
			contactManager.handleOtherPresence(SmManagerImpl.this, onlineUser, userResource, presence);
			return true;
		}

		private boolean handlePresence(OnlineUserImpl onlineUser, UserResourceImpl userResource, 
									RouteMessage routeMessage, Presence presence)
		{
			contactManager.handlePresence(SmManagerImpl.this, onlineUser, userResource, presence);
			return true;
		}

		private boolean handleIq(OnlineUserImpl onlineUser, UserResourceImpl userResource, RouteMessage routeMessage, Iq iq)
		{
			IqBind bind = 
				(IqBind) iq.getExtension(IqBind.ELEMENTNAME, IqBind.NAMESPACE);
			if (bind != null)
			{
				handleBindResource(onlineUser, userResource, routeMessage, iq, bind);
				return true;
			}
			
			IqSession iqSession = 
				(IqSession) iq.getExtension(IqSession.ELEMENTNAME, IqSession.NAMESPACE);
			
			if (iqSession != null)
			{
				handleBindSession(onlineUser, userResource, routeMessage, iq, iqSession);
				return true;
			}
			
			IqRoster iqRoster = (IqRoster) iq.getExtension(IqRoster.ELEMENTNAME, IqRoster.NAMESPACE);
			if (iqRoster != null)
			{
				contactManager.handleRoster(SmManagerImpl.this, onlineUser, userResource, iq);
				return true;
			}
			
			Privacy privacy = (Privacy) iq.getExtension(Privacy.ELEMENTNAME, Privacy.NAMESPACE);
			if (privacy != null)
			{
				handlePrivacy(onlineUser, userResource, routeMessage, iq, privacy);
				return true;
			}
			
			Iq.Type type = iq.getType();
			if (type == Iq.Type.result)
			{
				// TODO handle result-iq from client
				return true;
			}
			return false;
		}
		


		private void handlePrivacy(OnlineUserImpl onlineUser, UserResourceImpl userResource, RouteMessage routeMessage, Iq iq, Privacy privacy)
		{
			if (userResource == null)
			{
				return;
			}
			
			SmManagerImpl.this.privacyManager.handlePrivacy(onlineUser, userResource, routeMessage, iq, privacy);
			
		}

		private void handleBindSession(OnlineUserImpl onlineUser, UserResourceImpl userResource, RouteMessage routeMessage, Iq iqRequest, IqSession iqSession)
		{
			if (userResource != null)
			{
				userResource.setSessionBinded(true);
				
				Iq iqResult = new Iq(Iq.Type.result);
				iqResult.setStanzaId(iqRequest.getStanzaId());
				
				userResource.sendToSelfClient(iqResult);
			}
		}
		

		/**
		 * 
		 * @param onlineUser maybe must be null
		 * @param userResource2 must be null
		 * @param routeMessage
		 * @param iqRequest
		 * @param bind
		 */
		private void handleBindResource(OnlineUser onlineUser, UserResource userResource, 
									RouteMessage routeMessage, Iq iqRequest, IqBind bind)
		{
			String node = routeMessage.getToUserNode();
			String c2sName = routeMessage.getFrom();
			String streamId = routeMessage.getStreamId();
			String resource = bind.getResource();
			if (resource == null || resource.isEmpty())
			{
				XmppError error = new XmppError(XmppError.Condition.bad_request);
				
				Iq iqError = new Iq(Iq.Type.error);
				iqError.setStanzaId(iqRequest.getStanzaId());
				iqError.addExtension(bind);
				iqError.setError(error);
				
				
				RouteMessage responseMessage = 
					new RouteMessage(getName(), 
							routeMessage.getFrom(),
							routeMessage.getStreamId());
				responseMessage.setXmlStanza(iqError);
				sendToRouter(responseMessage);
				
				RouteMessage closeRouteMessage = 
					new RouteMessage(getName(), c2sName, streamId);
				closeRouteMessage.setXmlStanza(CloseStream.getCloseStream());
				sendToRouter(closeRouteMessage);
				return;
			}
			
			
			UserResource userResource2 = getUserResource(node, resource);
			if (userResource2 != null)
			{
				// remove old resource
				StreamError streamError = 
					new StreamError(StreamError.Condition.conflict);
				
				userResource2.sendToSelfClient(streamError);
				userResource2.sendToSelfClient(CloseStream.getCloseStream());
				removeUserResource(node, resource);
			}
			
			
			userResource2 = createUserResource(node, resource, c2sName, streamId);
			
			Iq iqResult = new Iq(Iq.Type.result);
			iqResult.setStanzaId(iqRequest.getStanzaId());
			IqBind resultBind = new IqBind();
			resultBind.setJid(new JID(node, getDomain(), resource));
			iqResult.addExtension(resultBind);
			
			userResource2.sendToSelfClient(iqResult);
			
		}
		

		private void handleCloseStream(UserResource userResource, RouteMessage routeMessage)
		{
			if (userResource != null)
			{
				userResource.logOut();
			}
		}

		
		private void returnUnsupportErrorToOtherUser(Packet packet)
		{
			if (packet instanceof Iq)
			{
				Iq iqRequest = (Iq) packet;
				Iq iqError = null;
				try
				{
					iqError = (Iq) iqRequest.clone();
				}
				catch (CloneNotSupportedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				
				iqError.setType(Iq.Type.error);
				iqError.setStanzaId(iqRequest.getStanzaId());
				JID requestFrom = iqRequest.getFrom();
				iqError.setTo(requestFrom);
				
				XmppError error = new XmppError(XmppError.Condition.feature_not_implemented);
				iqError.setError(error);
				
				RouteMessage routeMessage = new RouteMessage(getName());
				routeMessage.setXmlStanza(iqError);
			}
		}

		private void returnUnsupportError(Packet packet, UserResource userResource)
		{
			if (packet instanceof Iq)
			{
				Iq iqRequest = (Iq) packet;
				Iq iqError = new Iq(Iq.Type.error);
				iqError.setStanzaId(iqRequest.getStanzaId());
				JID to = new JID(userResource.getOnlineUser().getNode(), 
						getDomain(), 
						userResource.getResource());
				iqError.setTo(to);
				iqError.addExtensions(iqRequest.getExtensions());
				
				XmppError error = new XmppError(XmppError.Condition.feature_not_implemented);
				iqError.setError(error);
				userResource.sendToSelfClient(iqError);
			}
			
		}

		private MessageQueueWrapper getMessage(String node)
		{
			lock.lock();
			try
			{

				List<MessageQueueWrapper> wrappers = messageQueue.get(node);
				if (wrappers == null || wrappers.isEmpty())
				{
					messageQueue.removeAll(node);
					return null;
				}
				return wrappers.remove(0);
			}
			finally
			{
				lock.unlock();
			}
			
			
		}


	}
	
	private class MessageQueueWrapper
	{
		private RouteMessage routeMessage;
		
		private boolean fromClient;

		public MessageQueueWrapper(RouteMessage routeMessage, boolean fromClient)
		{
			super();
			this.routeMessage = routeMessage;
			this.fromClient = fromClient;
		}

		/**
		 * @return the routeMessage
		 */
		public RouteMessage getRouteMessage()
		{
			return routeMessage;
		}

		/**
		 * @return the fromClient
		 */
		public boolean isFromClient()
		{
			return fromClient;
		}

		
	}

}
