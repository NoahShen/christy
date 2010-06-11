package com.google.code.christy.c2s.webc2s;

import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.code.christy.c2s.C2SManager;
import com.google.code.christy.c2s.ChristyStreamFeature;
import com.google.code.christy.c2s.UserAuthenticator;
import com.google.code.christy.c2s.webc2s.controller.WebC2sController;
import com.google.code.christy.lib.ConnectionPool;
import com.google.code.christy.log.LoggerServiceTracker;
import com.google.code.christy.routemessageparser.RouteMessageParserServiceTracker;

public class Activator implements BundleActivator
{

	private RouteMessageParserServiceTracker routeMessageParserServiceTracker;

	private XmppParserServiceTracker xmppParserServiceTracker;

	private ServiceRegistration c2sManagerRegistration;

	private ChristyStreamFeatureServiceTracker streamFeatureStracker;

	private UserAuthenticatorTracker userAuthenticatorTracker;

	private ServiceRegistration resourceBindFeatureRegistration;

	private ServiceRegistration sessionFeatureRegistration;

	private ServiceRegistration plainUserAuthenticatorRegistration;

	private LoggerServiceTracker loggerServiceTracker;

	private WebC2sController webc2sController;

	private HttpServletServiceTracker httpServletServiceTracker;

	private ConnectionPool connPool;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception
	{
		
		//---------streamFeature

		ChristyStreamFeature resourceBindFeature = 
			new ChristyStreamFeature("bind", "urn:ietf:params:xml:ns:xmpp-bind", ChristyStreamFeature.SupportedType.afterAuth);
		resourceBindFeatureRegistration = context.registerService(ChristyStreamFeature.class.getName(), resourceBindFeature, null);

		ChristyStreamFeature sessionFeature = 
			new ChristyStreamFeature("session", "urn:ietf:params:xml:ns:xmpp-session", ChristyStreamFeature.SupportedType.afterAuth);
		sessionFeatureRegistration = context.registerService(ChristyStreamFeature.class.getName(), sessionFeature, null);
		
		
		streamFeatureStracker = new ChristyStreamFeatureServiceTracker(context);
		streamFeatureStracker.open();
		
		//---------streamFeature
		
		routeMessageParserServiceTracker = new RouteMessageParserServiceTracker(context);
		routeMessageParserServiceTracker.open();

		xmppParserServiceTracker = new XmppParserServiceTracker(context);
		xmppParserServiceTracker.open();

		streamFeatureStracker = new ChristyStreamFeatureServiceTracker(context);
		streamFeatureStracker.open();
		//---------streamFeature
		
		
		String appPath = System.getProperty("appPath");
		XMLConfiguration config = new XMLConfiguration(appPath + "/webc2sconfig.xml");
		
		SubnodeConfiguration subConifg = config.configurationAt("dbconfig");
		
		//authenticator
		
		connPool = new ConnectionPool("com.mysql.jdbc.Driver",
				subConifg.getString("url"),
				subConifg.getString("user"),
				subConifg.getString("password"));
		connPool.createPool();
		
		PlainUserAuthenticatorImpl plainUserAuthenticator = new PlainUserAuthenticatorImpl(connPool);
		plainUserAuthenticatorRegistration = context.registerService(UserAuthenticator.class.getName(), plainUserAuthenticator, null);
		
		userAuthenticatorTracker = new UserAuthenticatorTracker(context);
		userAuthenticatorTracker.open();
		
		//authenticator
		
		httpServletServiceTracker = new HttpServletServiceTracker(context);
		httpServletServiceTracker.open();
		
		
		loggerServiceTracker = new LoggerServiceTracker(context);
		loggerServiceTracker.open();
		
		WebC2SManager c2sManager = new WebC2SManager(routeMessageParserServiceTracker, 
								xmppParserServiceTracker,
								streamFeatureStracker,
								userAuthenticatorTracker,
								httpServletServiceTracker,
								loggerServiceTracker);

		String name = config.getString("name", "c2s_web1");
		String domain = config.getString("domain", "example.com");
		String routerIp = config.getString("router-ip", "localhost");
		String routerPassword = config.getString("router-password", "md5password");
		int routerPort = config.getInt("router-port", 8787);
		int webclientPort = config.getInt("web-client-port", 8080);
		int maxWait = config.getInt("max-wait", 60);
		int minWait = config.getInt("min-wait", 10);
		int inactivity = config.getInt("inactivity", 70);
		int maxHolded = config.getInt("max-holded", 1);
		int clientLimit = config.getInt("client-limit", 0);
		String contextPath = config.getString("context-path", "/webclient");
		String pathSpec = config.getString("path-spec", "/JHB.do");
		String xmppWebClient = config.getString("web-client", "/xmppWebClient");
		
		c2sManager.setName(name);
		c2sManager.setDomain(domain);
		c2sManager.setRouterIp(routerIp);
		c2sManager.setRouterPassword(routerPassword);
		c2sManager.setRouterPort(routerPort);
		c2sManager.setRouterIp(routerIp);
		c2sManager.setWebclientPort(webclientPort);
		c2sManager.setMaxWait(maxWait);
		c2sManager.setMinWait(minWait);
		c2sManager.setInactivity(inactivity);
		c2sManager.setMaxHolded(maxHolded);
		c2sManager.setClientLimit(clientLimit);
		c2sManager.setContextPath(contextPath);
		c2sManager.setPathSpec(pathSpec);
		c2sManager.setResourceBase(appPath + "/" + xmppWebClient);
		
		c2sManager.start();
		
		c2sManagerRegistration = context.registerService(C2SManager.class.getName(), c2sManager, null);
		
		webc2sController = new WebC2sController(c2sManager);
		webc2sController.start();
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception
	{
		
		if (routeMessageParserServiceTracker != null)
		{
			routeMessageParserServiceTracker.close();
			routeMessageParserServiceTracker = null;
		}

		if (xmppParserServiceTracker != null)
		{
			xmppParserServiceTracker.close();
			xmppParserServiceTracker = null;
		}

		if (c2sManagerRegistration != null)
		{
			c2sManagerRegistration.unregister();
			c2sManagerRegistration = null;
		}
		
		if (streamFeatureStracker != null)
		{
			streamFeatureStracker.close();
			streamFeatureStracker = null;
		}
		
		if (userAuthenticatorTracker != null)
		{
			userAuthenticatorTracker.close();
			userAuthenticatorTracker = null;
		}
		
		if (resourceBindFeatureRegistration != null)
		{
			resourceBindFeatureRegistration.unregister();
			resourceBindFeatureRegistration = null;
		}
		
		if (sessionFeatureRegistration != null)
		{
			sessionFeatureRegistration.unregister();
			sessionFeatureRegistration = null;
		}
		
		if (plainUserAuthenticatorRegistration != null)
		{
			plainUserAuthenticatorRegistration.unregister();
			plainUserAuthenticatorRegistration = null;
		}
		
		if (loggerServiceTracker != null)
		{
			loggerServiceTracker.close();
			loggerServiceTracker = null;
		}
		
		if (webc2sController != null)
		{
			webc2sController.stop();
			webc2sController = null;
		}
		
		if (httpServletServiceTracker != null)
		{
			httpServletServiceTracker.close();
			httpServletServiceTracker = null;
		}
		
		if (connPool != null)
		{
			connPool.closeConnectionPool();
		}
	}

}
