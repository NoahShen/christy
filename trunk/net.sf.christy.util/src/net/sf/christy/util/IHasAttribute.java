/**
 * 
 */
package net.sf.christy.util;

import java.util.Set;

/**
 * @author noah
 * 
 */
public interface IHasAttribute
{
	/**
	 * 
	 * @param listener
	 */
	public void addAttributeListener(AttributeListener listener);
	
	/**
	 * 
	 * @param listener
	 */
	public void removeAttributeListener(AttributeListener listener);
	
	/**
	 * 
	 * @return
	 */
	public AttributeListener[] getAttributeListeners();
	
	/**
	 * Returns the value of user-defined attribute of this session.
	 * 
	 * @param key
	 *                  the key of the attribute
	 * @return <tt>null</tt> if there is no attribute with the specified
	 *         key
	 */
	public Object getAttribute(String key);

	/**
	 * Sets a user-defined attribute.
	 * 
	 * @param key
	 *                  the key of the attribute
	 * @param value
	 *                  the value of the attribute
	 * @return The old value of the attribute. <tt>null</tt> if it is
	 *         new.
	 */
	public Object setAttribute(String key, Object value);

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
	public Object setAttribute(String key);

	/**
	 * Removes a user-defined attribute with the specified key.
	 * 
	 * @return The old value of the attribute. <tt>null</tt> if not
	 *         found.
	 */
	public Object removeAttribute(String key);

	/**
	 * Returns <tt>true</tt> if this session contains the attribute with
	 * the specified <tt>key</tt>.
	 */
	public boolean containsAttribute(String key);

	/**
	 * Returns the set of keys of all user-defined attributes.
	 */
	public Set<String> getAttributeKeys();
	
	/**
	 * 
	 */
	public void removeAllAttributes();
	
	/**
	 * 
	 * @param attributeKey
	 * @param newValue
	 * @param oldValue
	 */
	public void fireAttributeUpdated(String attributeKey, Object newValue, Object oldValue);
	
	/**
	 * 
	 * @param attributeKey
	 * @param oldValue
	 */
	public void fireAttributeRemoved(String attributeKey, Object oldValue);
	
	/**
	 * 
	 * @author noah
	 *
	 */
	public interface AttributeListener
	{
		/**
		 * 
		 * @param source
		 * @param attributeKey
		 * @param newValue
		 * @param oldValue
		 */
		public void attributeUpdated(IHasAttribute source, String attributeKey, Object newValue, Object oldValue);
		
		/**
		 * 
		 * @param source
		 * @param attributeKey
		 * @param oldValue
		 */
		public void attributeRemoved(IHasAttribute source, String attributeKey, Object oldValue);
	}
}
