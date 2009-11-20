/**
 * 
 */
package net.sf.christy.dbhelper.db4odbhelpler;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.db4o.query.Predicate;

import net.sf.christy.sm.privacy.UserPrivacyList;
import net.sf.christy.sm.privacy.UserPrivacyListItem;
import net.sf.christy.sm.privacy.UserPrivacyListItemDbHelper;

/**
 * @author Noah
 *
 */
public class UserPrivacyListItemDbHelperImpl implements UserPrivacyListItemDbHelper
{
	private ObjectContainer objectContainer;
	
	public UserPrivacyListItemDbHelperImpl()
	{
		objectContainer = ObjectContainerInstance.getInstance();
		Db4o.configure().objectClass(UserPrivacyListItem.class.getName()).cascadeOnDelete(true);
		Db4o.configure().objectClass(UserPrivacyListItem.class.getName()).cascadeOnUpdate(true);
	}

	@Override
	public void deleteUserPrivacyListItem(UserPrivacyListItem item) throws Exception
	{
		objectContainer.delete(item);
		objectContainer.commit();
	}

	@Override
	public UserPrivacyListItem[] getUserPrivacyListItems(final UserPrivacyList userPrivacyList) throws Exception
	{
		ObjectSet<UserPrivacyListItem> objSet = objectContainer.query(new Predicate<UserPrivacyListItem>(){


			/**
			 * 
			 */
			private static final long serialVersionUID = -3384075883558806415L;

			@Override
			public boolean match(UserPrivacyListItem userPrivacyListItem)
			{
				String username = userPrivacyListItem.getUsername();
				String privacy = userPrivacyListItem.getPrivacyName();
				return (username.equalsIgnoreCase(userPrivacyList.getUsername())
						&& privacy.equals(userPrivacyList.getPrivacyName()));

			}
			
		});
		
		
		return objSet.toArray(new UserPrivacyListItem[]{});
	}


	@Override
	public UserPrivacyListItem[] getUserPrivacyListItems(final String username, final String privacyName) throws Exception
	{
		ObjectSet<UserPrivacyListItem> objSet = objectContainer.query(new Predicate<UserPrivacyListItem>(){

			/**
			 * 
			 */
			private static final long serialVersionUID = -3384075883558806415L;

			@Override
			public boolean match(UserPrivacyListItem userPrivacyListItem)
			{
				String usernameItem = userPrivacyListItem.getUsername();
				String privacyItem = userPrivacyListItem.getPrivacyName();
				return (usernameItem.equalsIgnoreCase(username)
						&& privacyItem.equals(privacyName));

			}
			
		});
		
		
		return objSet.toArray(new UserPrivacyListItem[]{});
	}
	
	@Override
	public void insertUserPrivacyListItem(UserPrivacyListItem item) throws Exception
	{
		objectContainer.set(item);
		objectContainer.commit();
	}

	@Override
	public void updateUserPrivacyListItem(UserPrivacyListItem item) throws Exception
	{
		objectContainer.set(item);
		objectContainer.commit();
	}


}
