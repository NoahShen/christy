// ========================================================================
// Copyright 2006 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================

package com.google.code.christy.c2s.webc2s;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.jabber.JabberHTTPBind.JHBServlet;

public class ManyServletContexts
{
	public static void main(String[] args) throws Exception
	{
		Server server = new Server(8787);

		ContextHandlerCollection contexts = new ContextHandlerCollection();
		server.setHandler(contexts);

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setWelcomeFiles(new String[] { "index.html" });
		resource_handler.setResourceBase("xmppWebClient/");

		ServletContextHandler root = new ServletContextHandler(contexts, "/webclient", ServletContextHandler.SESSIONS);
		root.addServlet(new ServletHolder(new JHBServlet()), "/JHB.do");

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { root, resource_handler, new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
//		server.join();
		
		System.out.println("D");
	}

}
