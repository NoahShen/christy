package com.google.code.christy.log.impl;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.LoggerFactory;

import com.google.code.christy.log.Logger;

public class Slf4jLogger implements Logger
{
	private final org.slf4j.Logger logger = LoggerFactory.getLogger(Slf4jLogger.class);
	
	public Slf4jLogger()
	{
		PropertyConfigurator.configure(Slf4jLogger.class.getResource("log4j.properties"));
	}
	
	@Override
	public void debug(String msg)
	{
		logger.debug(msg);
	}

	@Override
	public void debug(String format, Object arg)
	{
		logger.debug(format, arg);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2)
	{
		logger.debug(format, arg1, arg2);
	}

	@Override
	public void debug(String format, Object[] argArray)
	{
		logger.debug(format, argArray);
	}

	@Override
	public void debug(String msg, Throwable t)
	{
		logger.debug(msg, t);
	}

	@Override
	public void error(String msg)
	{
		logger.error(msg);
	}

	@Override
	public void error(String format, Object arg)
	{
		logger.error(format, arg);
	}

	@Override
	public void error(String format, Object arg1, Object arg2)
	{
		logger.error(format, arg1, arg2);
	}

	@Override
	public void error(String format, Object[] argArray)
	{
		logger.error(format, argArray);
	}

	@Override
	public void error(String msg, Throwable t)
	{
		logger.error(msg, t);
	}

	@Override
	public String getName()
	{
		return logger.getName();
	}

	@Override
	public void info(String msg)
	{
		logger.info(msg);
	}

	@Override
	public void info(String format, Object arg)
	{
		logger.info(format, arg);
	}

	@Override
	public void info(String format, Object arg1, Object arg2)
	{
		logger.info(format, arg1, arg2);
	}

	@Override
	public void info(String format, Object[] argArray)
	{
		logger.info(format, argArray);
	}

	@Override
	public void info(String msg, Throwable t)
	{
		logger.info(msg, t);
	}

	@Override
	public boolean isDebugEnabled()
	{
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isErrorEnabled()
	{
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isInfoEnabled()
	{
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled()
	{
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(String msg)
	{
		logger.warn(msg);
	}

	@Override
	public void warn(String format, Object arg)
	{
		logger.warn(format, arg);
	}

	@Override
	public void warn(String format, Object[] argArray)
	{
		logger.warn(format, argArray);
	}

	@Override
	public void warn(String format, Object arg1, Object arg2)
	{
		logger.warn(format, arg1, arg2);
	}

	@Override
	public void warn(String msg, Throwable t)
	{
		logger.warn(msg, t);
	}

}
