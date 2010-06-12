package com.google.code.christy.dbhelper.db4odbhelpler;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.code.christy.dbhelper.OfflineSubscribeMsg;
import com.google.code.christy.dbhelper.OfflineSubscribeMsgDbHelper;


/**
 * 
 * @author Noah
 *
 */
public class OfflineSubscribeMsgDbHelperImpl implements OfflineSubscribeMsgDbHelper
{
	private ObjectContainer objectContainer;
	
	public OfflineSubscribeMsgDbHelperImpl()
	{
		objectContainer = ObjectContainerInstance.getInstance();
		Db4o.configure().objectClass(OfflineSubscribeMsg.class.getName()).cascadeOnDelete(true);
		Db4o.configure().objectClass(OfflineSubscribeMsg.class.getName()).cascadeOnUpdate(true);
	}
	@Override
	public void addOfflineSubscribeMsg(OfflineSubscribeMsg offlineSubscribeMsg) throws Exception
	{
		objectContainer.set(offlineSubscribeMsg);
		objectContainer.commit();
	}

	@Override
	public OfflineSubscribeMsg[] getAndRemoveOfflineSubscribeMsg(String username) throws Exception
	{
		OfflineSubscribeMsg[] msg = getOfflineSubscribeMsg(username);
		removeOfflineSubscribeMsg(username);
		return msg;
	}

	@Override
	public OfflineSubscribeMsg[] getOfflineSubscribeMsg(final String username) throws Exception
	{
		ObjectSet<OfflineSubscribeMsg> objSet = objectContainer.query(new Predicate<OfflineSubscribeMsg>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 985534606769456822L;

			@Override
			public boolean match(OfflineSubscribeMsg msg)
			{
				return username.equalsIgnoreCase(msg.getUsername());
			}
			
		});
		
		return objSet.toArray(new OfflineSubscribeMsg[]{});
	}

	@Override
	public void removeOfflineSubscribeMsg(OfflineSubscribeMsg offlineSubscribeMsg) throws Exception
	{
		objectContainer.delete(offlineSubscribeMsg);
		objectContainer.commit();
	}
	@Override
	public void removeOfflineSubscribeMsg(String username) throws Exception
	{
		OfflineSubscribeMsg[] msg = getOfflineSubscribeMsg(username);
		for (OfflineSubscribeMsg m : msg)
		{
			objectContainer.delete(m);
		}
		objectContainer.commit();
	}

}
