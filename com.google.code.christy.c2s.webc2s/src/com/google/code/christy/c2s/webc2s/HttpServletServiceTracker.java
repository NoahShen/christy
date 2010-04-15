package com.google.code.christy.c2s.webc2s;

import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class HttpServletServiceTracker extends ServiceTracker
{

	public HttpServletServiceTracker(BundleContext context)
	{
		super(context, HttpServlet.class.getName(), null);
	}

	
}
