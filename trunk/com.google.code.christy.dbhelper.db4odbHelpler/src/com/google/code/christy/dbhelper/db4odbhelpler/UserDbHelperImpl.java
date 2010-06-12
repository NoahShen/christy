/**
 * 
 */
package com.google.code.christy.dbhelper.db4odbhelpler;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;
import com.google.code.christy.dbhelper.User;
import com.google.code.christy.dbhelper.UserDbHelper;


/**
 * @author Noah
 *
 */
public class UserDbHelperImpl implements UserDbHelper
{
	private ObjectContainer objectContainer;
	
	public UserDbHelperImpl()
	{
		objectContainer = ObjectContainerInstance.getInstance();
		Db4o.configure().objectClass(User.class.getName()).cascadeOnDelete(true);
		Db4o.configure().objectClass(User.class.getName()).cascadeOnUpdate(true);
	}

	@Override
	public void addUser(User user) throws Exception
	{
		objectContainer.set(user);
		objectContainer.commit();
	}

	@Override
	public User getUser(final String username) throws Exception
	{
		ObjectSet<User> objSet = objectContainer.query(new Predicate<User>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 985534606769456822L;

			@Override
			public boolean match(User user)
			{
				return user.getUsername().equalsIgnoreCase(username);
			}
			
		});
		if (!objSet.isEmpty())
		{
			return objSet.get(0);
		}
		return null;
	}

	@Override
	public void removeUser(String username) throws Exception
	{
		User user = getUser(username);
		objectContainer.delete(user);
		objectContainer.commit();

	}

	@Override
	public void updateUserPlainPassword(String username, String newPassword) throws Exception
	{
		User user = getUser(username);
		if (user != null)
		{
			user.setPassword(newPassword);
			objectContainer.set(user);
			objectContainer.commit();
		}

	}

}
