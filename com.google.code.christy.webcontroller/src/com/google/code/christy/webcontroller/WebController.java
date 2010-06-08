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

package com.google.code.christy.webcontroller;


import org.apache.log4j.PropertyConfigurator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;


public class WebController
{
	public static void main(String[] args) throws Exception
	{
		PropertyConfigurator.configure(WebController.class.getResource("log4j.properties"));
		
		Server server = new Server(17777);

		ServletContextHandler servletContextHandler = new ServletContextHandler();
		servletContextHandler.setContextPath("/");
		
		servletContextHandler.addServlet(ControllerServlet.class, "/controller.do");
		
//		FilterHolder fHolder = new FilterHolder(org.eclipse.jetty.servlets.GzipFilter.class);
//		fHolder.setName("compress");
//		
//		webappcontext.addFilter(fHolder, "*.js", FilterMapping.ALL);
//		webappcontext.addFilter(fHolder, "*.css", FilterMapping.ALL);
//		webappcontext.addFilter(fHolder, "*.html", FilterMapping.ALL);
//		webappcontext.addFilter(fHolder, "*.jsp", FilterMapping.ALL);
		
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setWelcomeFiles(new String[] { "index.html" });
		resource_handler.setResourceBase("webpage/");
		
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { servletContextHandler, resource_handler, new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
		server.join();
	}
}
