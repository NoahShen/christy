/**
 * 
 */
package net.sf.christy.util;

import java.util.Set;

/**
 * @author noah
 * 
 */
public interface Propertied
{
	/**
	 * 
	 * @param listener
	 */
	public void addPropertyListener(PropertyListener listener);
	
	/**
	 * 
	 * @param listener
	 */
	public void removePropertyListener(PropertyListener listener);
	
	/**
	 * 
	 * @return
	 */
	public PropertyListener[] getPropertyListeners();
	
	/**
	 * Returns the value of user-defined property of this session.
	 * 
	 * @param key
	 *                  the key of the attribute
	 * @return <tt>null</tt> if there is no property with the specified
	 *         key
	 */
	public Object getProperty(String key);

	/**
	 * Sets a user-defined property.
	 * 
	 * @param key
	 *                  the key of the property
	 * @param value
	 *                  the value of the property
	 * @return The old value of the property. <tt>null</tt> if it is
	 *         new.
	 */
	public Object setProperty(String key, Object value);

	/**
	 * Sets a user defined attribute without a value. This is useful when
	 * you just want to put a 'mark' attribute. Its value is set to
	 * {@link Boolean#TRUE}.
	 * 
	 * @param key
	 *                  the key of the attribute
	 * @return The old value of the attribute. <tt>null</tt> if it is
	 *         new.
	 */
	public Object setProperty(String key);

	/**
	 * Removes a user-defined attribute with the specified key.
	 * 
	 * @return The old value of the attribute. <tt>null</tt> if not
	 *         found.
	 */
	public Object removeProperty(String key);

	/**
	 * Returns <tt>true</tt> if this session contains the attribute with
	 * the specified <tt>key</tt>.
	 */
	public boolean containsProperty(String key);

	/**
	 * Returns the set of keys of all user-defined attributes.
	 */
	public Set<String> getPropertyKeys();
	
	/**
	 * 
	 */
	public void removeAllProperties();
	
	/**
	 * 
	 * @param key
	 * @param newValue
	 * @param oldValue
	 */
	public void firePropertyUpdated(String key, Object newValue, Object oldValue);
	
	/**
	 * 
	 * @param key
	 * @param oldValue
	 */
	public void firePropertyRemoved(String key, Object oldValue);
	
	/**
	 * 
	 * @author noah
	 *
	 */
	public interface PropertyListener
	{
		/**
		 * 
		 * @param source
		 * @param key
		 * @param newValue
		 * @param oldValue
		 */
		public void propertyUpdated(Propertied source, String key, Object newValue, Object oldValue);
		
		/**
		 * 
		 * @param source
		 * @param key
		 * @param oldValue
		 */
		public void attributeRemoved(Propertied source, String key, Object oldValue);
	}
}
