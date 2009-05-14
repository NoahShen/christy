/**
 * 
 */
package net.sf.christy.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author noah
 * 
 */
public abstract class AbstractHasAttribute implements IHasAttribute
{
	private Map<String, Object> attributes;

	private List<AttributeListener> attributeListeners;
	
	
	/* (non-Javadoc)
	 * @see net.sf.mxlosgi.mxlosgiutilsbundle.IHasAttribute#addAttributeListener(net.sf.mxlosgi.mxlosgiutilsbundle.IHasAttribute.AttributeListener)
	 */
	@Override
	public void addAttributeListener(AttributeListener listener)
	{
		getListeners().add(listener);
	}

	/* (non-Javadoc)
	 * @see net.sf.mxlosgi.mxlosgiutilsbundle.IHasAttribute#getAttributeListeners()
	 */
	@Override
	public AttributeListener[] getAttributeListeners()
	{
		if (attributeListeners == null)
		{
			return new AttributeListener[]{};
		}
		
		return attributeListeners.toArray(new AttributeListener[]{});
	}

	/* (non-Javadoc)
	 * @see net.sf.mxlosgi.mxlosgiutilsbundle.IHasAttribute#removeAttributeListener(net.sf.mxlosgi.mxlosgiutilsbundle.IHasAttribute.AttributeListener)
	 */
	@Override
	public void removeAttributeListener(AttributeListener listener)
	{
		getListeners().remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.joah.joahutilsbundle.IHasAttribute#containsAttribute(java.lang.String)
	 */
	@Override
	public boolean containsAttribute(String key)
	{
		if (attributes == null)
		{
			return false;
		}
		
		return getAttributes().containsKey(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.joah.joahutilsbundle.IHasAttribute#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String key)
	{
		if (attributes == null)
		{
			return null;
		}
		
		return getAttributes().get(key);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.joah.joahutilsbundle.IHasAttribute#getAttributeKeys()
	 */
	@Override
	public Set<String> getAttributeKeys()
	{
		if (attributes == null)
		{
			return Collections.emptySet();
		}
		
		synchronized (attributes)
		{
			return new HashSet<String>(attributes.keySet());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.joah.joahutilsbundle.IHasAttribute#removeAttribute(java.lang.String)
	 */
	@Override
	public Object removeAttribute(String key)
	{
		if (attributes == null)
		{
			return null;
		}
		
		return getAttributes().remove(key);
	}

	/* (non-Javadoc)
	 * @see net.sf.joah.joahutilsbundle.IHasAttribute#removeAllAttributes()
	 */
	@Override
	public void removeAllAttributes()
	{
		if (attributes == null)
		{
			return;
		}
		getAttributes().clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.joah.joahutilsbundle.IHasAttribute#setAttribute(java.lang.String,
	 *      java.lang.Object)
	 */
	@Override
	public Object setAttribute(String key, Object value)
	{
		if (value == null)
		{
			return removeAttribute(key);
		}
		else
		{
			return getAttributes().put(key, value);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sf.joah.joahutilsbundle.IHasAttribute#setAttribute(java.lang.String)
	 */
	@Override
	public Object setAttribute(String key)
	{
		return setAttribute(key, Boolean.TRUE);
	}
	
	@Override
	public void fireAttributeUpdated(String attributeKey, Object newValue, Object oldValue)
	{
		if (attributeListeners == null)
		{
			return;
		}
		
		for (AttributeListener listener : attributeListeners)
		{
			listener.attributeUpdated(this, attributeKey, newValue, oldValue);
		}
	}
	
	@Override
	public void fireAttributeRemoved(String attributeKey, Object oldValue)
	{
		if (attributeListeners == null)
		{
			return;
		}
		
		for (AttributeListener listener : attributeListeners)
		{
			listener.attributeRemoved(this, attributeKey, oldValue);
		}
	}

	/**
	 * @return the attributes
	 */
	private Map<String, Object> getAttributes()
	{
		if (attributes == null)
		{
			synchronized(this)
			{
				if (attributes == null)
				{
					attributes = new ConcurrentHashMap<String, Object>();
				}
			}
		}
		return attributes;
	}

	private List<AttributeListener> getListeners()
	{
		if (attributeListeners == null)
		{
			synchronized(this)
			{
				if (attributeListeners == null)
				{
					attributeListeners = new CopyOnWriteArrayList<AttributeListener>();
				}
			}
		}
		
		return attributeListeners;
	}
}
