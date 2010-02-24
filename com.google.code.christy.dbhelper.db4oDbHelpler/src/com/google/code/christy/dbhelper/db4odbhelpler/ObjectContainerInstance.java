/**
 * 
 */
package com.google.code.christy.dbhelper.db4odbhelpler;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;

/**
 * @author noah
 *
 */
public class ObjectContainerInstance
{
	private static ObjectContainer objectContainer;
	
	private ObjectContainerInstance()
	{
	}
	
	public static ObjectContainer getInstance()
	{
		if (objectContainer == null)
		{
			synchronized(ObjectContainer.class)
			{
				if (objectContainer == null)
				{
					objectContainer = Db4o.openFile("christy.db");
				}
			}
		}
		return objectContainer;
	}
	
	
	
}
