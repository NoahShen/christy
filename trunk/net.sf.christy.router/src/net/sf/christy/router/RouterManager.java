/**
 * 
 */
package net.sf.christy.router;

import net.sf.christy.util.Propertied;

/**
 * @author noah
 *
 */
public interface RouterManager extends Propertied
{
	/**
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * 
	 * @return
	 */
	public String getDomain();
	
	/**
	 * 
	 * @param domain
	 */
	public void setDomain(String domain);
	
	/**
	 * add C2S module's username and md5password to router, so the c2s can connect the router
	 * @param name
	 * @param md5Password
	 */
	public void addC2s(String name, String md5Password);
	
	/**
	 * 
	 * @param name
	 */
	public void removeC2s(String name);
	
	/**
	 * remove Sm module's username and md5password to router, so the sm can connect the router
	 * @param name
	 * @param md5Password
	 */
	public void addSm(String name, String md5Password);
	
	/**
	 * 
	 * @param name
	 */
	public void removeSm(String name);

	/**
	 * obtain max c2s module number
	 * @return
	 */
	public int getC2sLimit();
	
	/**
	 * max c2s module number. 0 stands for no limit
	 * @param c2sLimit
	 */
	public void setC2sLimit(int c2sLimit);

	/**
	 * obtain max sm module number
	 * @return
	 */
	public int getSmLimit();
	
	/**
	 * max sm module number. 0 stands for no limit
	 * @param smLimit
	 */
	public void setSmLimit(int smLimit);
	
	/**
	 * 
	 * @return
	 */
	public int getC2sPort();
	
	/**
	 * 
	 * @param c2sPort
	 */
	public void setC2sPort(int c2sPort);
	
	/**
	 * 
	 * @return
	 */
	public int getS2sPort();
	
	/**
	 * 
	 * @param c2sPort
	 */
	public void setS2sPort(int s2sPort);
	
	/**
	 * 
	 * @return
	 */
	public int getSmPort();
	
	/**
	 * 
	 * @param smPort
	 */
	public void setSmPort(int smPort);
	
	/**
	 * start c2s module
	 */
	public void start();
	
	/**
	 * stop c2s module
	 */
	public void stop();
	
	/**
	 *  exit program
	 */
	public void exit();
	
	/**
	 * 
	 * @return
	 */
	public boolean isStarted();
}
