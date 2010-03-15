package com.google.code.christy.log;

import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class LoggerServiceTracker extends ServiceTracker
{

	
	public LoggerServiceTracker(BundleContext context)
	{
		super(context, Logger.class.getName(), null);
	}
	

	
	public void debug(String msg)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.debug(msg);
		}
	}

	
	public void debug(String format, Object arg)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.debug(format, arg);
		}
	}

	
	public void debug(String format, Object arg1, Object arg2)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.debug(format, arg1, arg2);
		}
	}

	
	public void debug(String format, Object[] argArray)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.debug(format, argArray);
		}
	}

	
	public void debug(String msg, Throwable t)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.debug(msg, t);
		}
	}

	
	public void error(String msg)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.error(msg);
		}
	}

	
	public void error(String format, Object arg)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.error(format, arg);
		}
	}

	
	public void error(String format, Object arg1, Object arg2)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.error(format, arg1, arg2);
		}
	}

	
	public void error(String format, Object[] argArray)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.error(format, argArray);
		}
	}

	
	public void error(String msg, Throwable t)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.error(msg, t);
		}
	}

	
	public void info(String msg)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.info(msg);
		}
	}

	
	public void info(String format, Object arg)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.info(format, arg);
		}
	}

	
	public void info(String format, Object arg1, Object arg2)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.info(format, arg1, arg2);
		}
	}

	
	public void info(String format, Object[] argArray)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.info(format, argArray);
		}
	}

	
	public void info(String msg, Throwable t)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.info(msg, t);
		}
	}

	
	public boolean isDebugEnabled()
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			return logger.isDebugEnabled();
		}
		return false;
	}

	
	public boolean isErrorEnabled()
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.isErrorEnabled();
		}
		return false;
	}

	
	public boolean isInfoEnabled()
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.isInfoEnabled();
		}
		return false;
	}

	
	public boolean isWarnEnabled()
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.isWarnEnabled();
		}
		return false;
	}

	
	public void warn(String msg)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.warn(msg);
		}
	}

	
	public void warn(String format, Object arg)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.warn(format, arg);
		}
	}

	
	public void warn(String format, Object[] argArray)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.warn(format, argArray);
		}
	}

	
	public void warn(String format, Object arg1, Object arg2)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.warn(format, arg1, arg2);
		}
	}

	
	public void warn(String msg, Throwable t)
	{
		Logger logger = (Logger) getService();
		if (logger != null)
		{
			logger.warn(msg, t);
		}
	}

}
