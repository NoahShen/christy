package com.google.code.christy.dbhelper.db4odbhelpler;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.code.christy.sm.privacy.UserPrivacyList;
import com.google.code.christy.sm.privacy.UserPrivacyListDbHelper;


/**
 * 
 * @author Noah
 *
 */
public class UserPrivacyListDbHelperImpl implements UserPrivacyListDbHelper
{
	private ObjectContainer objectContainer;
	
	public UserPrivacyListDbHelperImpl()
	{
		objectContainer = ObjectContainerInstance.getInstance();
		Db4o.configure().objectClass(UserPrivacyList.class.getName()).cascadeOnDelete(true);
		Db4o.configure().objectClass(UserPrivacyList.class.getName()).cascadeOnUpdate(true);
	}

	@Override
	public void cancelDefaultPrivacyList(final String username) throws Exception
	{
		ObjectSet<UserPrivacyList> objSet = objectContainer.query(new Predicate<UserPrivacyList>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 2108413401399968175L;

			@Override
			public boolean match(UserPrivacyList userPrivacyList)
			{
				String usernameList = userPrivacyList.getUsername();
				return (usernameList.equalsIgnoreCase(username)
						&& userPrivacyList.isDefaultList());

			}
			
		});
		
		if (!objSet.isEmpty())
		{
			UserPrivacyList list = objSet.get(0);
			list.setDefaultList(false);
			objectContainer.set(list);
			objectContainer.commit();
		}
		
	}

	@Override
	public void deleteUserPrivacyList(final String username, final String privacyName) throws Exception
	{
		ObjectSet<UserPrivacyList> objSet = objectContainer.query(new Predicate<UserPrivacyList>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 2108413401399968175L;

			@Override
			public boolean match(UserPrivacyList userPrivacyList)
			{
				String usernameList = userPrivacyList.getUsername();
				return (usernameList.equalsIgnoreCase(username)
						&& privacyName.equals(userPrivacyList.getPrivacyName()));

			}
			
		});
		
		if (!objSet.isEmpty())
		{
			UserPrivacyList list = objSet.get(0);
			objectContainer.delete(list);
			objectContainer.commit();
		}
	}

	@Override
	public UserPrivacyList getDefaultUserPrivacyList(final String username) throws Exception
	{
		ObjectSet<UserPrivacyList> objSet = objectContainer.query(new Predicate<UserPrivacyList>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = -2674234258743355848L;

			@Override
			public boolean match(UserPrivacyList userPrivacyList)
			{
				String usernameList = userPrivacyList.getUsername();
				return (usernameList.equalsIgnoreCase(username)
						&& userPrivacyList.isDefaultList());

			}
			
		});
		
		if (!objSet.isEmpty())
		{
			UserPrivacyList list = objSet.get(0);
			return list;
		}
		
		return null;
	}

	@Override
	public UserPrivacyList getUserPrivacyList(final String username, final String privacyName) throws Exception
	{
		ObjectSet<UserPrivacyList> objSet = objectContainer.query(new Predicate<UserPrivacyList>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 2108413401399968175L;

			@Override
			public boolean match(UserPrivacyList userPrivacyList)
			{
				String usernameList = userPrivacyList.getUsername();
				return (usernameList.equalsIgnoreCase(username)
						&& privacyName.equals(userPrivacyList.getPrivacyName()));
			}
			
		});
		
		if (!objSet.isEmpty())
		{
			UserPrivacyList list = objSet.get(0);
			return list;
		}
		
		return null;
	}

	@Override
	public UserPrivacyList[] getUserPrivacyLists(final String username) throws Exception
	{
		ObjectSet<UserPrivacyList> objSet = objectContainer.query(new Predicate<UserPrivacyList>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = -1810975632229145091L;

			@Override
			public boolean match(UserPrivacyList userPrivacyList)
			{
				String usernameList = userPrivacyList.getUsername();
				return (usernameList.equalsIgnoreCase(username));
			}
			
		});
		
		if (!objSet.isEmpty())
		{
			return objSet.toArray(new UserPrivacyList[]{});
		}
		
		return new UserPrivacyList[]{};
	}

	@Override
	public void insertUserPrivacyList(UserPrivacyList userPrivacyList) throws Exception
	{
		objectContainer.set(userPrivacyList);
		objectContainer.commit();
	}

	@Override
	public void setDefaultPrivacyList(final String username, final String privacyName) throws Exception
	{
		ObjectSet<UserPrivacyList> objSet = objectContainer.query(new Predicate<UserPrivacyList>(){


			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean match(UserPrivacyList userPrivacyList)
			{
				String usernameList = userPrivacyList.getUsername();
				return (usernameList.equalsIgnoreCase(username)
						&& privacyName.equals(userPrivacyList.getPrivacyName()));
			}
			
		});
		
		if (!objSet.isEmpty())
		{
			UserPrivacyList list = objSet.get(0);
			list.setDefaultList(true);
			objectContainer.set(list);
			objectContainer.commit();
		}
	}

	@Override
	public void updateUserPrivacyList(UserPrivacyList userPrivacyList) throws Exception
	{
		String username = userPrivacyList.getUsername();
		String privacyName = userPrivacyList.getPrivacyName();
		UserPrivacyList oldUserPrivacyList = getUserPrivacyList(username, privacyName);
		if (oldUserPrivacyList != null)
		{
			objectContainer.delete(oldUserPrivacyList);
		}
		objectContainer.set(userPrivacyList);
		objectContainer.commit();
	}

}
