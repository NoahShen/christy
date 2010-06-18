package com.google.code.christy.module.pubsub.impl;

import java.io.StringReader;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;

import com.google.code.christy.dbhelper.PubSubAffiliation;
import com.google.code.christy.dbhelper.PubSubAffiliationDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubItem;
import com.google.code.christy.dbhelper.PubSubItemDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubNodeConfigDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubNodeDbHelperTracker;
import com.google.code.christy.dbhelper.PubSubSubscription;
import com.google.code.christy.dbhelper.PubSubSubscriptionDbHelperTracker;
import com.google.code.christy.log.LoggerServiceTracker;
import com.google.code.christy.mina.XmppCodecFactory;
import com.google.code.christy.module.pubsub.PubSubManager;
import com.google.code.christy.routemessage.RouteMessage;
import com.google.code.christy.routemessageparser.RouteMessageParserServiceTracker;
import com.google.code.christy.util.AbstractPropertied;
import com.google.code.christy.xmpp.Iq;
import com.google.code.christy.xmpp.JID;
import com.google.code.christy.xmpp.PacketUtils;
import com.google.code.christy.xmpp.XmlStanza;
import com.google.code.christy.xmpp.XmppError;
import com.google.code.christy.xmpp.dataform.DataForm;
import com.google.code.christy.xmpp.dataform.FormField;
import com.google.code.christy.xmpp.disco.DiscoInfoExtension;
import com.google.code.christy.xmpp.disco.DiscoItemsExtension;
import com.google.code.christy.xmpp.pubsub.PubSubAffiliations;
import com.google.code.christy.xmpp.pubsub.PubSubExtension;
import com.google.code.christy.xmpp.pubsub.PubSubItems;
import com.google.code.christy.xmpp.pubsub.PubSubOptions;
import com.google.code.christy.xmpp.pubsub.PubSubSubscribe;
import com.google.code.christy.xmpp.pubsub.PubSubSubscriptionItem;
import com.google.code.christy.xmpp.pubsub.PubSubSubscriptions;
import com.google.code.christy.xmpp.pubsub.PubSubUnsubscribe;

public class PubSubManagerImpl extends AbstractPropertied implements PubSubManager
{
	public static final String MODULEROUTER_NAMESPACE = "christy:internal:module2router";
	
	public static final String MODULEROUTER_AUTH_NAMESPACE = "christy:internal:module2router:auth";
	
	private String domain;
	
	private String subDomain;
	
	private String serviceId;
	
	private String routerIp;
	
	private String routerPassword;
	
	private int routerPort = 8790;
	
	private boolean started = false;

	private boolean routerConnected = false;
	
	private IoSession routerSession;
	
	private LoggerServiceTracker loggerServiceTracker;
	
	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;

	private SocketConnector routerConnector;
	
	private PubSubEngine pubSubEngine;

	private int maxItems;

	
	public PubSubManagerImpl(LoggerServiceTracker loggerServiceTracker, 
				RouteMessageParserServiceTracker routeMessageParserServiceTracker, 
				PubSubNodeDbHelperTracker pubSubNodeDbHelperTracker, 
				PubSubItemDbHelperTracker pubSubItemDbHelperTracker, 
				PubSubSubscriptionDbHelperTracker pubSubSubscriptionDbHelperTracker, 
				PubSubAffiliationDbHelperTracker pubSubAffiliationDbHelperTracker, 
				PubSubNodeConfigDbHelperTracker pubSubNodeConfigDbHelperTracker, 
				AccessModelTracker accessModelTracker)
	{
		super();
		this.loggerServiceTracker = loggerServiceTracker;
		this.routeMessageParserServiceTracker = routeMessageParserServiceTracker;
		
		pubSubEngine = new PubSubEngine(this, pubSubNodeDbHelperTracker, 
						pubSubItemDbHelperTracker,
						pubSubSubscriptionDbHelperTracker,
						pubSubAffiliationDbHelperTracker,
						pubSubNodeConfigDbHelperTracker,
						accessModelTracker);
	}

	public String getDomain()
	{
		return domain;
	}

	public void setDomain(String domain)
	{
		this.domain = domain;
	}

	/**
	 * @return the subDomain
	 */
	public String getSubDomain()
	{
		return subDomain;
	}

	/**
	 * @param subDomain the subDomain to set
	 */
	public void setSubDomain(String subDomain)
	{
		this.subDomain = subDomain;
	}



	public void setServiceId(String serviceId)
	{
		this.serviceId = serviceId;
	}

	@Override
	public String getServiceId()
	{
		return serviceId;
	}

	@Override
	public void start()
	{
		if (isStarted())
		{
			throw new IllegalStateException("sm has started");
		}
		loggerServiceTracker.info("sm starting...");
		
		if (getDomain() == null || getDomain().isEmpty())
		{
			loggerServiceTracker.error("domain has not been set");
			throw new IllegalStateException("domain has not been set");
		}
		
		if (getServiceId() == null || getServiceId().isEmpty())
		{
			loggerServiceTracker.error("service id has not been set");
			throw new IllegalStateException("service id has not been set");
		}
		
		if (getRouterIp() == null || getRouterIp().isEmpty())
		{
			loggerServiceTracker.error("routerIp has not been set");
			throw new IllegalStateException("routerIp has not been set");
		}
		
		if (getRouterPassword() == null || getRouterPassword().isEmpty())
		{
			loggerServiceTracker.error("routerPassword has not been set");
			throw new IllegalStateException("routerPassword has not been set");
		}
		
		loggerServiceTracker.info("connecting to router");
		
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
			loggerServiceTracker.error("c2s starting failure: connecting to router failure");
			exit();
			return;
		}
		
		started = true;
		loggerServiceTracker.info("connecting to router successful");
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
	public void exit()
	{
		stop();
		System.exit(0);
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
	public void sendToRouter(RouteMessage routeMessage)
	{
		routerSession.write(routeMessage.toXml());		
	}

	@Override
	public void setRouterIp(String routerIp)
	{
		if (isStarted())
		{
			throw new IllegalStateException("pubsub module has started");
		}
		this.routerIp = routerIp;
	}

	@Override
	public void setRouterPassword(String routerPassword)
	{
		if (isStarted())
		{
			throw new IllegalStateException("pubsub module has started");
		}
		this.routerPassword = routerPassword;
	}

	@Override
	public void setRouterPort(int routerPort)
	{
		if (isStarted())
		{
			throw new IllegalStateException("pubsub module has started");
		}
		this.routerPort = routerPort;
	}

	public void setMaxItems(int maxItems)
	{
		this.maxItems = maxItems;
	}
	
	/**
	 * @return the maxItems
	 */
	public int getMaxItems()
	{
		return maxItems;
	}
	
	private class RouterHandler implements IoHandler
	{

		@Override
		public void exceptionCaught(IoSession session, Throwable cause) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": exceptionCaught:" + cause.getMessage());
			cause.printStackTrace();
		}

		@Override
		public void messageReceived(IoSession session, Object message) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": messageReceived:\n" + message);

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
				loggerServiceTracker.info("router auth successful");
			}
			else if ("failed".equals(elementName))
			{
				loggerServiceTracker.error("password error");
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
			XmlStanza stanza = routeMessage.getXmlStanza();
			if (stanza instanceof Iq)
			{
				Iq iq = (Iq) stanza;
				handlerIq(routeMessage, iq, session);
			}
		}

		private void handlerIq(RouteMessage routeMessage, Iq iq, IoSession session)
		{
			Iq iqResponse = null;
			
			JID from = iq.getFrom();
			if (from == null)
			{
				return;
			}
			
			DiscoInfoExtension discoInfo = (DiscoInfoExtension) iq.getExtension(DiscoInfoExtension.ELEMENTNAME, DiscoInfoExtension.NAMESPACE);
			if (discoInfo != null)
			{
				iqResponse = handleDiscoInfo(from, routeMessage, iq, discoInfo, session);
			}
			
			DiscoItemsExtension discoItems = (DiscoItemsExtension) iq.getExtension(DiscoItemsExtension.ELEMENTNAME, DiscoItemsExtension.NAMESPACE);
			if (discoItems != null)
			{
				iqResponse = handleDiscoItems(from, routeMessage, iq, discoItems, session);
			}
			
			PubSubExtension pubSubExtension = (PubSubExtension) iq.getExtension(PubSubExtension.ELEMENTNAME, PubSubExtension.NAMESPACE);
			if (pubSubExtension != null)
			{
				iqResponse = handlePubSubExtension(from, routeMessage, iq, pubSubExtension, session);
			}
			
			if (iqResponse != null)
			{
				RouteMessage responseRouteMessage = new RouteMessage(getSubDomain(), routeMessage.getStreamId());
				
				//local user
				if (from.getDomain().equals(getDomain()))
				{
					responseRouteMessage.setToUserNode(from.getNode());
				}
				
				responseRouteMessage.setXmlStanza(iqResponse);
				
				sendToRouter(responseRouteMessage);
			}
			
			
		}

		private Iq handlePubSubExtension(JID from, RouteMessage routeMessage, Iq iq, PubSubExtension pubSubExtension, IoSession session)
		{
			Iq iqResponse = null;
			List<XmlStanza> stanzas = pubSubExtension.getStanzas();
			XmlStanza stanza = null;
			if (stanzas != null && !stanzas.isEmpty())
			{
				stanza = stanzas.get(0);
				if (stanza instanceof PubSubSubscriptions)
				{
					iqResponse = handlePubSubSubscriptions(from, stanza, iq, pubSubExtension);
				}
				else if (stanza instanceof PubSubAffiliations)
				{
					iqResponse = handlePubSubAffiliations(from, stanza, iq, pubSubExtension);
				}
				else if (stanza instanceof PubSubSubscribe)
				{
					iqResponse = handlePubSubSubscribe(from, stanza, iq, pubSubExtension);
				}
				else if (stanza instanceof PubSubUnsubscribe)
				{
					iqResponse = handlePubSubUnsubscribe(from, stanza, iq, pubSubExtension);
				}
				else if (stanza instanceof PubSubOptions)
				{
					iqResponse = handlePubSubOptions(from, stanza, iq, pubSubExtension);
				}
				else if (stanza instanceof PubSubItems)
				{
					iqResponse = handlePubSubItems(from, stanza, iq, pubSubExtension);
				}
			}
			
			return iqResponse;
		}

		private Iq handlePubSubItems(JID from, XmlStanza stanza, Iq iq, PubSubExtension pubSubExtension)
		{
			Iq iqResponse = null;
			Iq.Type type = iq.getType();
			if (type != Iq.Type.get)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.bad_request));
				if (iqResponse.getFrom() == null)
				{
					iqResponse.setFrom(new JID(null, getSubDomain(), null));
				}
				return iqResponse;
			}
			
			PubSubItems pubSubItems = (PubSubItems) stanza;
			
			String nodeId = pubSubItems.getNode();
			String subId = pubSubItems.getSubId();
			int maxItems = pubSubItems.getMaxItems();
			String itemId = null;
			if (!pubSubItems.getItems().isEmpty())
			{
				itemId = pubSubItems.getItems().get(0).getId();
			}
			
			try
			{
				// TODO check case sentity
				Collection<PubSubItem> items = pubSubEngine.getPubSubItems(from.toBareJID(), nodeId, subId, itemId, (maxItems == 0 ? PubSubManagerImpl.this.getMaxItems() : maxItems));
				
				PubSubExtension pubSubExtensionResponse = new PubSubExtension(pubSubExtension.getNamespace());
				PubSubItems pubSubItemsResponse = new PubSubItems(nodeId);
				pubSubItemsResponse.setSubId(subId);
				pubSubItemsResponse.setMaxItems(maxItems);
				
				for (PubSubItem pubSubItem : items)
				{
					PubSubItems.Item item = new PubSubItems.Item(pubSubItem.getItemId());
					item.setPayload(pubSubItem.getPayload());
					pubSubItemsResponse.addItem(item);
				}
				pubSubExtensionResponse.addStanza(pubSubItemsResponse);
				
				iqResponse = PacketUtils.createResultIq(iq);
				iqResponse.addExtension(pubSubExtensionResponse);
				
			}
			catch (NodeNotExistException e)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.item_not_found));
			}
			catch (CannotAccessException e)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.not_authorized));
			}
			catch (InvalidSubIdException e)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				XmppError error = new XmppError(XmppError.Condition.no_acceptable);
				error.addOtherCondition("invalid-subid", "http://jabber.org/protocol/pubsub#errors");
				iqResponse.setError(error);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return iqResponse;
		}

		private Iq handlePubSubOptions(JID from, XmlStanza stanza, Iq iq, PubSubExtension pubSubExtension)
		{
			Iq iqResponse = null;
			Iq.Type type = iq.getType();
			if (type == Iq.Type.get)
			{
				iqResponse = handleGetPubSubOptions(from, stanza, iq, pubSubExtension);
			}
			else if (type == Iq.Type.set)
			{
				iqResponse = handleSetPubSubOptions(from, stanza, iq, pubSubExtension);
			}
			return iqResponse;
		}

		private Iq handleSetPubSubOptions(JID from, XmlStanza stanza, Iq iq, PubSubExtension pubSubExtension)
		{
			Iq iqResponse = null;
			
			PubSubOptions pubSubOptions = (PubSubOptions) stanza;
			String node = pubSubOptions.getNode();
			JID jid = pubSubOptions.getJid();
			String subId = pubSubOptions.getSubId();
			if (!from.equalsWithBareJid(jid))
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				
				XmppError error = new XmppError(XmppError.Condition.bad_request);
				error.addOtherCondition("invalid-jid", "http://jabber.org/protocol/pubsub#errors");
				
				iqResponse.setError(error);
			}
			else if (subId == null)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				
				XmppError error = new XmppError(XmppError.Condition.bad_request);
				error.addOtherCondition("subid-required", "http://jabber.org/protocol/pubsub#errors");
				
				iqResponse.setError(error);
			}
			else
			{
				try
				{
					DataForm dataForm = pubSubOptions.getDataForm();
					Map<String, Object> config = new HashMap<String, Object>();
					if (dataForm != null && dataForm.getType() == DataForm.Type.submit)
					{
						for (FormField field : dataForm.getFields())
						{
							String var = field.getVariable();
							
							if ("pubsub#deliver".equals(var))
							{
								String valueStr = field.getValues().next();
								int value = parseBoolean(valueStr);
								config.put("deliver", value);
							}
							else if ("pubsub#digest".equals(var))
							{
								String valueStr = field.getValues().next();
								int value = parseBoolean(valueStr);
								config.put("digest", value);
							}
							else if ("pubsub#digest_frequency".equals(var))
							{
								String valueStr = field.getValues().next();
								config.put("digest_frequency", Integer.parseInt(valueStr));
							}
							else if ("pubsub#expire".equals(var))
							{
								String valueStr = field.getValues().next();
								config.put("expire", Integer.parseInt(valueStr));
							}
							else if ("pubsub#include_body".equals(var))
							{
								String valueStr = field.getValues().next();
								int value = parseBoolean(valueStr);
								config.put("includeBody", value);
							}
							else if ("pubsub#show-values".equals(var))
							{
								Iterator<String> it = field.getValues();
								StringBuilder value = new StringBuilder();
								while(it.hasNext())
								{
									value.append(it.next() + ";");
								}
								config.put("showValues", value);
							}
							else if ("pubsub#subscription_depth".equals(var))
							{
								String value = field.getValues().next();
								config.put("subscriptionDepth", value);
							}
							
						}
						// TODO check case sentity
						pubSubEngine.updateSubscriptionConfigure(jid.toPrepedBareJID(), node, subId, config);
						
						iqResponse = PacketUtils.createResultIq(iq);
					}
					else
					{
						iqResponse = PacketUtils.createErrorIq(iq);
						iqResponse.setError(new XmppError(XmppError.Condition.bad_request));
					}
				}
				catch (NodeNotExistException e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.item_not_found));
				}
				catch (TooManySubscriptionsException e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					XmppError error = new XmppError(XmppError.Condition.policy_violation);
					error.addOtherCondition("too-many-subscriptions", "http://jabber.org/protocol/pubsub#errors");
					iqResponse.setError(error);
				}
				catch (Exception e)
				{
					// TODO
					e.printStackTrace();
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.internal_server_error));
				}
			}
			
			return iqResponse;
		}

		private Iq handleGetPubSubOptions(JID from, XmlStanza stanza, Iq iq, PubSubExtension pubSubExtension)
		{
			Iq iqResponse = null;
			
			PubSubOptions pubSubOptions = (PubSubOptions) stanza;
			String node = pubSubOptions.getNode();
			JID jid = pubSubOptions.getJid();
			String subId = pubSubOptions.getSubId();
			if (!from.equalsWithBareJid(jid))
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				
				XmppError error = new XmppError(XmppError.Condition.bad_request);
				error.addOtherCondition("invalid-jid", "http://jabber.org/protocol/pubsub#errors");
				
				iqResponse.setError(error);
			}
			else if (subId == null)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				
				XmppError error = new XmppError(XmppError.Condition.bad_request);
				error.addOtherCondition("subid-required", "http://jabber.org/protocol/pubsub#errors");
				
				iqResponse.setError(error);
			}
			else
			{
				try
				{
					// TODO check case sentity
					PubSubSubscription pubSubSubscription = pubSubEngine.getPubSubSubscription(jid.toPrepedBareJID(), node, subId);
					
					if (pubSubSubscription == null)
					{
						// Subscription should have a default config
						throw new NodeNotExistException();
					}
					
					PubSubExtension pubSubExtensionResponse = new PubSubExtension(PubSubExtension.NAMESPACE);
					
					PubSubOptions pubSubOptionsResponse = new PubSubOptions(node, jid);
					pubSubOptionsResponse.setSubId(subId);
					
					//---FORM_TYPE
					DataForm dataForm = new DataForm();
					dataForm.setType(DataForm.Type.form);
					
					FormField field = new FormField("FORM_TYPE");
					field.setType(FormField.TYPE_HIDDEN);
					field.addValue("http://jabber.org/protocol/pubsub#subscribe_options");
					
					dataForm.addField(field);
					
					//---pubsub#deliver
					FormField deliverField = new FormField("pubsub#deliver");
					deliverField.setLabel("Whether an entity wants to receive or disable notifications");
					deliverField.setType(FormField.TYPE_BOOLEAN);
					deliverField.addValue(String.valueOf(pubSubSubscription.isDeliver()));
					
					dataForm.addField(deliverField);
					
					//---pubsub#digest
					FormField digestField = new FormField("pubsub#digest");
					digestField.setLabel("Whether an entity wants to receive digests (aggregations) of notifications or all notifications individually");
					digestField.setType(FormField.TYPE_BOOLEAN);
					digestField.addValue(String.valueOf(pubSubSubscription.isDigest()));
					
					dataForm.addField(digestField);
					
					//---pubsub#digest_frequency
					FormField digestFrequencyField = new FormField("pubsub#digest_frequency");
					digestFrequencyField.setLabel("The minimum number of milliseconds between sending any two notification digests");
					digestFrequencyField.setType(FormField.TYPE_TEXT_SINGLE);
					digestFrequencyField.addValue(String.valueOf(pubSubSubscription.getDigestFrequency()));
					
					dataForm.addField(digestFrequencyField);
					
					//---pubsub#expire
					FormField expireField = new FormField("pubsub#expire");
					expireField.setLabel("The DateTime at which a leased subscription will end or has ended");
					expireField.setType(FormField.TYPE_TEXT_SINGLE);
					expireField.addValue(String.valueOf(pubSubSubscription.getExpire()));
					
					dataForm.addField(expireField);
					
					//---pubsub#include_body
					FormField includeBodyField = new FormField("pubsub#include_body");
					includeBodyField.setLabel("Whether an entity wants to receive an XMPP message body in addition to the payload format");
					includeBodyField.setType(FormField.TYPE_BOOLEAN);
					includeBodyField.addValue(String.valueOf(pubSubSubscription.isIncludeBody()));
					
					dataForm.addField(includeBodyField);
					
					//---pubsub#show-values
					FormField showValuesField = new FormField("pubsub#show-values");
					showValuesField.setLabel("The presence states for which an entity wants to receive notifications");
					showValuesField.setType(FormField.TYPE_LIST_MULTI);
					
					FormField.Option chatOption = new FormField.Option("Want to Chat", "chat");
					showValuesField.addOption(chatOption);
					FormField.Option onlineOption = new FormField.Option("Available", "online");
					showValuesField.addOption(onlineOption);
					FormField.Option awayOption = new FormField.Option("Away", "away");
					showValuesField.addOption(awayOption);
					FormField.Option dndOption = new FormField.Option("Do Not Disturb", "dnd");
					showValuesField.addOption(dndOption);
					
					for (String value : pubSubSubscription.getShowValues())
					{
						showValuesField.addValue(value);
					}					
					
					dataForm.addField(showValuesField);
					
					//---pubsub#subscription_depth
					FormField subscriptionDepthField = new FormField("pubsub#subscription_depth");
					subscriptionDepthField.setLabel("Receive notification from direct child nodes only or all descendent nodes");
					subscriptionDepthField.setType(FormField.TYPE_LIST_SINGLE);
					
					FormField.Option childOnlyOption = new FormField.Option("Receive notification from direct child nodes only", "1");
					showValuesField.addOption(childOnlyOption);
					
					FormField.Option allNodeOption = new FormField.Option("Receive notification from all descendent nodes", "all");
					showValuesField.addOption(allNodeOption);
					
					if (pubSubSubscription.getSubscriptionDepth() != null)
					{
						subscriptionDepthField.addValue(pubSubSubscription.getSubscriptionDepth());
					}
					
					
					dataForm.addField(subscriptionDepthField);
					
					
					pubSubOptionsResponse.setDataForm(dataForm);
					
					pubSubExtensionResponse.addStanza(pubSubOptionsResponse);
					
					iqResponse = PacketUtils.createResultIq(iq);
					
					iqResponse.addExtension(pubSubExtensionResponse);
					return iqResponse;
				}
				catch (InvalidSubIdException e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					XmppError error = new XmppError(XmppError.Condition.no_acceptable);
					error.addOtherCondition("invalid-subid", "http://jabber.org/protocol/pubsub#errors");
					iqResponse.setError(error);
				}
				catch (NodeNotExistException e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.item_not_found));
				}
				catch (Exception e)
				{
					// TODO
					e.printStackTrace();
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.internal_server_error));
				}
			}
			
			return iqResponse;
		}

		private Iq handlePubSubUnsubscribe(JID from, XmlStanza stanza, Iq iq, PubSubExtension pubSubExtension)
		{
			Iq iqResponse = null;
			Iq.Type type = iq.getType();
			if (type != Iq.Type.set)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.bad_request));
				if (iqResponse.getFrom() == null)
				{
					iqResponse.setFrom(new JID(null, getSubDomain(), null));
				}
				return iqResponse;
			}
			
			PubSubUnsubscribe pubSubUnsubscribe = (PubSubUnsubscribe) stanza;
			String node = pubSubUnsubscribe.getNode();
			JID jid = pubSubUnsubscribe.getJid();
			String subId = pubSubUnsubscribe.getSubId();
			if (!from.equalsWithBareJid(jid))
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				
				XmppError error = new XmppError(XmppError.Condition.bad_request);
				error.addOtherCondition("invalid-jid", "http://jabber.org/protocol/pubsub#errors");
				
				iqResponse.setError(error);
			}
			else if (subId == null)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				
				XmppError error = new XmppError(XmppError.Condition.bad_request);
				error.addOtherCondition("subid-required", "http://jabber.org/protocol/pubsub#errors");
				
				iqResponse.setError(error);
			}
			else
			{
				try
				{
					// TODO check case sentity
					pubSubEngine.unsubscribeNode(jid.toPrepedBareJID(), node, subId);
					iqResponse = PacketUtils.createResultIq(iq);

					return iqResponse;
				}
				catch (NodeNotExistException e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.item_not_found));
				}
				catch (Exception e)
				{
					// TODO
					e.printStackTrace();
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.internal_server_error));
				}
			}
			
			return iqResponse;
		}

		private Iq handlePubSubSubscribe(JID from, XmlStanza stanza, Iq iq, PubSubExtension pubSubExtension)
		{
			Iq iqResponse = null;
			Iq.Type type = iq.getType();
			if (type != Iq.Type.set)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.bad_request));
				if (iqResponse.getFrom() == null)
				{
					iqResponse.setFrom(new JID(null, getSubDomain(), null));
				}
				return iqResponse;
			}

			PubSubSubscribe pubSubSubscribe = (PubSubSubscribe) stanza;
			String node = pubSubSubscribe.getNode();
			JID subscriberJid = pubSubSubscribe.getSubscriberJid();
			
			if (!from.equalsWithBareJid(subscriberJid))
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				
				XmppError error = new XmppError(XmppError.Condition.bad_request);
				error.addOtherCondition("invalid-jid", "http://jabber.org/protocol/pubsub#errors");
				
				iqResponse.setError(error);
			}
			else
			{
				try
				{
					// TODO check case sentity
					String jid = subscriberJid.toPrepedBareJID();
					PubSubSubscription pubSubSubscription = pubSubEngine.subscribeNode(jid, node);
					
					DataForm configForm = null;
					List<XmlStanza> stanzas = pubSubExtension.getStanzas();
					if (stanzas.size() > 1)
					{
						PubSubOptions options = (PubSubOptions) stanzas.get(1);
						DataForm dataForm = options.getDataForm();
						Map<String, Object> config = new HashMap<String, Object>();
						if (dataForm != null && dataForm.getType() == DataForm.Type.submit)
						{
							for (FormField field : dataForm.getFields())
							{
								String var = field.getVariable();
								
								if ("pubsub#deliver".equals(var))
								{
									String valueStr = field.getValues().next();
									int value = parseBoolean(valueStr);
									config.put("deliver", value);
								}
								else if ("pubsub#digest".equals(var))
								{
									String valueStr = field.getValues().next();
									int value = parseBoolean(valueStr);
									config.put("digest", value);
								}
								else if ("pubsub#digest_frequency".equals(var))
								{
									String valueStr = field.getValues().next();
									config.put("digest_frequency", Integer.parseInt(valueStr));
								}
								else if ("pubsub#expire".equals(var))
								{
									String valueStr = field.getValues().next();
									config.put("expire", Integer.parseInt(valueStr));
								}
								else if ("pubsub#include_body".equals(var))
								{
									String valueStr = field.getValues().next();
									int value = parseBoolean(valueStr);
									config.put("includeBody", value);
								}
								else if ("pubsub#show-values".equals(var))
								{
									Iterator<String> it = field.getValues();
									StringBuilder value = new StringBuilder();
									while(it.hasNext())
									{
										value.append(it.next() + ";");
									}
									config.put("showValues", value);
								}
								else if ("pubsub#subscription_depth".equals(var))
								{
									String value = field.getValues().next();
									config.put("subscriptionDepth", value);
								}
								
							}
							pubSubEngine.updateSubscriptionConfigure(jid, node, pubSubSubscription.getSubId(), config);
							configForm = dataForm;
							configForm.setType(DataForm.Type.result);
						}
					}
					
					iqResponse = PacketUtils.createResultIq(iq);
					PubSubExtension pubSubExtensionResponse = new PubSubExtension(PubSubExtension.NAMESPACE);
					
					PubSubSubscriptionItem item = new PubSubSubscriptionItem(pubSubSubscription.getNodeId(), 
											pubSubSubscription.getJid(), 
											PubSubSubscriptionItem.SubscriptionType.valueOf(pubSubSubscription.getSubscription().name()));
					item.setSubId(pubSubSubscription.getSubId());
					
					pubSubExtensionResponse.addStanza(item);
					if (configForm != null)
					{
						
						pubSubExtensionResponse.addStanza(configForm);
					}
					iqResponse.addExtension(pubSubExtensionResponse);
				}
				catch (NodeNotExistException e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.item_not_found));
				}
				catch (TooManySubscriptionsException e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					XmppError error = new XmppError(XmppError.Condition.policy_violation);
					error.addOtherCondition("too-many-subscriptions", "http://jabber.org/protocol/pubsub#errors");
					iqResponse.setError(error);
				}
				catch (Exception e)
				{
					// TODO
					e.printStackTrace();
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.internal_server_error));
				}
			}
			
			return iqResponse;
		}

		private int parseBoolean(String valueStr)
		{
			return ("true".equalsIgnoreCase(valueStr) || "1".equals(valueStr)) ? 1 : 0;
			
		}

		private Iq handlePubSubAffiliations(JID from, XmlStanza stanza, Iq iq, PubSubExtension pubSubExtension)
		{

			Iq iqResponse = null;
			PubSubAffiliations pubSubAffiliations = (PubSubAffiliations) stanza;
			String node = pubSubAffiliations.getNode();
			Collection<PubSubAffiliation> affiliations;
			try
			{
				affiliations = pubSubEngine.getPubSubAffiliation(from.toBareJID(), node);
				PubSubExtension pubSubExtensionResponse = new PubSubExtension(pubSubExtension.getNamespace());
				PubSubAffiliations pubSubAffiliationsResponse = new PubSubAffiliations();
				pubSubAffiliationsResponse.setNode(node);
				
				for (PubSubAffiliation pubSubAffiliation : affiliations)
				{
					PubSubAffiliations.Affiliation affi = 
						new PubSubAffiliations.Affiliation(pubSubAffiliation.getNodeId(), 
										PubSubAffiliations.AffiliationType.valueOf(pubSubAffiliation.getAffiliationType().name()));
					pubSubAffiliationsResponse.addAffiliation(affi);
				}
				
				pubSubExtensionResponse.addStanza(pubSubAffiliationsResponse);
				
				iqResponse = PacketUtils.createResultIq(iq);
				iqResponse.addExtension(pubSubExtensionResponse);
			}
			catch (NodeNotExistException e)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.item_not_found));
			}
			catch (Exception e)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.internal_server_error));
			}
			
			return iqResponse;
		}

		private Iq handlePubSubSubscriptions(JID from, XmlStanza stanza, Iq iq, PubSubExtension pubSubExtension)
		{
			Iq iqResponse = null;
			PubSubSubscriptions pubSubSubscriptions = (PubSubSubscriptions) stanza;
			String node = pubSubSubscriptions.getNode();
			Collection<PubSubSubscription> subs;
			try
			{
				subs = pubSubEngine.getPubSubSubscriptions(from.toBareJID(), node);
				PubSubExtension pubSubExtensionResponse = new PubSubExtension(pubSubExtension.getNamespace());
				PubSubSubscriptions pubSubSubscriptionsResponse = new PubSubSubscriptions();
				pubSubSubscriptionsResponse.setNode(node);
				
				for (PubSubSubscription subscription : subs)
				{
					PubSubSubscriptionItem sub = 
						new PubSubSubscriptionItem(subscription.getNodeId(), 
										subscription.getJid(), 
										PubSubSubscriptionItem.SubscriptionType.valueOf(subscription.getSubscription().name()));
					sub.setSubId(subscription.getSubId());
					pubSubSubscriptionsResponse.addPubSubSubscription(sub);
				}
				
				pubSubExtensionResponse.addStanza(pubSubSubscriptionsResponse);
				
				iqResponse = PacketUtils.createResultIq(iq);
				iqResponse.addExtension(pubSubExtensionResponse);
			}
			catch (NodeNotExistException e)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.item_not_found));
			}
			catch (Exception e)
			{
				// TODO
				e.printStackTrace();
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.internal_server_error));
			}
			
			return iqResponse;
		}

		private Iq handleDiscoItems(JID from, RouteMessage routeMessage, Iq iq, DiscoItemsExtension discoItems, IoSession session)
		{
			
			Iq iqResponse = null;
			if (iq.getType() != Iq.Type.get)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.bad_request));
				if (iqResponse.getFrom() == null)
				{
					iqResponse.setFrom(new JID(null, getSubDomain(), null));
				}
			}
			else
			{
				String node = discoItems.getNode();
				DiscoItemsExtension discoItemsResponse;
				try
				{
					discoItemsResponse = pubSubEngine.getDiscoItem(node);
					iqResponse = PacketUtils.createResultIq(iq);
					iqResponse.addExtension(discoItemsResponse);
				}
				catch (NodeNotExistException e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.item_not_found));
				}
				catch (Exception e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.internal_server_error));
				}
				
			}
			
			return iqResponse;
			
		}

		private Iq handleDiscoInfo(JID from, RouteMessage routeMessage, Iq iq, DiscoInfoExtension discoInfo, IoSession session)
		{			
			Iq iqResponse = null;
			if (iq.getType() != Iq.Type.get)
			{
				iqResponse = PacketUtils.createErrorIq(iq);
				iqResponse.setError(new XmppError(XmppError.Condition.bad_request));
				if (iqResponse.getFrom() == null)
				{
					iqResponse.setFrom(new JID(null, getSubDomain(), null));
				}
			}
			else
			{
				DiscoInfoExtension discoInfoResponse;
				try
				{
					discoInfoResponse = pubSubEngine.getDiscoInfo(discoInfo.getNode());
					iqResponse = PacketUtils.createResultIq(iq);
					iqResponse.addExtension(discoInfoResponse);
					
					iqResponse = PacketUtils.createResultIq(iq);
					iqResponse.addExtension(discoInfoResponse);
				}
				catch (NodeNotExistException e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.item_not_found));
				}
				catch (Exception e)
				{
					iqResponse = PacketUtils.createErrorIq(iq);
					iqResponse.setError(new XmppError(XmppError.Condition.internal_server_error));
				}
				
			}
			
			return iqResponse;
		}

		private void handleStream(XmlPullParser parser, IoSession session)
		{
			String xmlns = parser.getNamespace(null);
			String from = parser.getAttributeValue("", "from");
			String id = parser.getAttributeValue("", "id");
			
			if (!MODULEROUTER_NAMESPACE.equals(xmlns))
			{
				loggerServiceTracker.error("namespace error:" + xmlns);
				session.close();
				return;
			}
			if (!"router".equals(from))
			{
				loggerServiceTracker.error("from error:" + from);
				session.close();
				return;
			}
			session.setAttribute("streamId", id);
			
			loggerServiceTracker.debug("open stream successful");
			
			session.write("<internal xmlns='" + MODULEROUTER_NAMESPACE + "'" +
						" subdomain='" + getSubDomain() + "' password='" + getRouterPassword() + "'/>");
		}
		
		
		@Override
		public void messageSent(IoSession session, Object message) throws Exception
		{
			if (loggerServiceTracker.isDebugEnabled())
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
				loggerServiceTracker.debug("session" + session + ": messageSent:\n" + s);
			}
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": sessionClosed");
			exit();
		}

		@Override
		public void sessionCreated(IoSession session) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": sessionCreated");
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status) throws Exception
		{
			// TODO Auto-generated method stub
			
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception
		{
			loggerServiceTracker.debug("session" + session + ": sessionOpened");
			
			session.write("<stream:stream xmlns='" + MODULEROUTER_NAMESPACE + "'" +
						" xmlns:stream='http://etherx.jabber.org/streams'" +
						" to='router'" +
						" domain='" + getDomain() + "'>");
		}
	}
	
	
}
