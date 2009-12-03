package net.sf.christy.sm.user;



/**
 * @author noah
 *
 */
public interface UserDbHelper
{
	/**
	 * 
	 * @param username
	 * @return
	 */
	public User getUser(String username) throws Exception;
	
	/**
	 * 
	 * @param user
	 */
	public void addUser(User user) throws Exception;
	
	/**
	 * 
	 * @param user
	 */
	public void updateUserPlainPassword(String username, String newPassword) throws Exception;
	
	
	/**
	 * 
	 * @param username
	 */
	public void removeUser(String username) throws Exception;
}
