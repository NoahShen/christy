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
public abstract class AbstractPropertied implements Propertied
{
	private Map<String, Object> properties;

	private List<PropertyListener> propertyListeners;
	

	@Override
	public void addPropertyListener(PropertyListener listener)
	{
		getListeners().add(listener);
	}

	@Override
	public PropertyListener[] getPropertyListeners()
	{
		if (propertyListeners == null)
		{
			return new PropertyListener[]{};
		}
		
		return propertyListeners.toArray(new PropertyListener[]{});
	}

	@Override
	public void removePropertyListener(PropertyListener listener)
	{
		getListeners().remove(listener);
	}

	@Override
	public boolean containsProperty(String key)
	{
		if (properties == null)
		{
			return false;
		}
		
		return getProperties().containsKey(key);
	}

	@Override
	public Object getProperty(String key)
	{
		if (properties == null)
		{
			return null;
		}
		
		return getProperties().get(key);
	}

	@Override
	public Set<String> getPropertyKeys()
	{
		if (properties == null)
		{
			return Collections.emptySet();
		}
		
		synchronized (properties)
		{
			return new HashSet<String>(properties.keySet());
		}
	}

	@Override
	public Object removeProperty(String key)
	{
		if (properties == null)
		{
			return null;
		}
		
		return getProperties().remove(key);
	}


	@Override
	public void removeAllProperties()
	{
		if (properties == null)
		{
			return;
		}
		getProperties().clear();
	}

	@Override
	public Object setProperty(String key, Object value)
	{
		if (value == null)
		{
			return removeProperty(key);
		}
		else
		{
			return getProperties().put(key, value);
		}
	}

	@Override
	public Object setProperty(String key)
	{
		return setProperty(key, Boolean.TRUE);
	}
	
	@Override
	public void firePropertyUpdated(String key, Object newValue, Object oldValue)
	{
		if (propertyListeners == null)
		{
			return;
		}
		
		for (PropertyListener listener : propertyListeners)
		{
			listener.propertyUpdated(this, key, newValue, oldValue);
		}
	}
	
	@Override
	public void firePropertyRemoved(String key, Object oldValue)
	{
		if (propertyListeners == null)
		{
			return;
		}
		
		for (PropertyListener listener : propertyListeners)
		{
			listener.attributeRemoved(this, key, oldValue);
		}
	}

	/**
	 * @return the attributes
	 */
	private Map<String, Object> getProperties()
	{
		if (properties == null)
		{
			synchronized(this)
			{
				if (properties == null)
				{
					properties = new ConcurrentHashMap<String, Object>();
				}
			}
		}
		return properties;
	}

	private List<PropertyListener> getListeners()
	{
		if (propertyListeners == null)
		{
			synchronized(this)
			{
				if (propertyListeners == null)
				{
					propertyListeners = new CopyOnWriteArrayList<PropertyListener>();
				}
			}
		}
		
		return propertyListeners;
	}
}
