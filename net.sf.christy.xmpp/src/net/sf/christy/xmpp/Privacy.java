/**
 * 
 */
package net.sf.christy.xmpp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author noah
 *
 */
public class Privacy implements PacketExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1856644233316694470L;

	public static final String ELEMENTNAME = "query";
	
	public static final String NAMESPACE = "jabber:iq:privacy";
	
	/** activeName is the name associated with the active list set for the session **/
	private String activeName;
	
	/** defaultName is the name of the default list that applies to the user as a whole **/
	private String defaultName;
	
	/** itemLists holds the set of privacy items classified in lists. It is a map where the 
	 * key is the name of the list and the value a collection with privacy items. **/
	private Map<String, PrivacyList> privacyLists = new HashMap<String, PrivacyList>();
	
	/** declineActiveList is true when the user declines the use of the active list **/
	private boolean declineActiveList=false;
	
	/** declineDefaultList is true when the user declines the use of the default list **/
	private boolean declineDefaultList=false;
	
	
	public Privacy()
	{
		
	}

	public String getActiveName()
	{
		return activeName;
	}

	public void setActiveName(String activeName)
	{
		this.activeName = activeName;
	}

	public String getDefaultName()
	{
		return defaultName;
	}

	public void setDefaultName(String defaultName)
	{
		this.defaultName = defaultName;
	}

	public void addPrivacyList(PrivacyList privacyList)
	{
		privacyLists.put(privacyList.getListName(), privacyList);
	}
	
	public void removePrivacyList(String listName)
	{
		privacyLists.remove(listName);
	}
	
	public PrivacyList getPrivacyList(String listName)
	{
		return privacyLists.get(listName);
	}
	
	public Collection<PrivacyList> getPrivacyLists()
	{
		return Collections.unmodifiableCollection(privacyLists.values());
	}
	
	
	public boolean isDeclineActiveList()
	{
		return declineActiveList;
	}

	public void setDeclineActiveList(boolean declineActiveList)
	{
		this.declineActiveList = declineActiveList;
	}

	public boolean isDeclineDefaultList()
	{
		return declineDefaultList;
	}

	public void setDeclineDefaultList(boolean declineDefaultList)
	{
		this.declineDefaultList = declineDefaultList;
	}

	@Override
	public String getElementName()
	{
		return ELEMENTNAME;
	}

	@Override
	public String getNamespace()
	{
		return NAMESPACE;
	}

	@Override
	public String toXml()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("<" + getElementName() + " xmlns=\"" + getNamespace() + "\">");

		// Add the active tag
		if (this.isDeclineActiveList())
		{
			buf.append("<active/>");
		}
		else
		{
			if (getActiveName() != null)
			{
				buf.append("<active name=\"").append(getActiveName()).append("\"/>");
			}
		}
		// Add the default tag
		if (isDeclineDefaultList())
		{
			buf.append("<default/>");
		}
		else
		{
			if (getDefaultName() != null)
			{
				buf.append("<default name=\"").append(getDefaultName()).append("\"/>");
			}
		}
		
		Iterator<PrivacyList> it = getPrivacyLists().iterator();
		while (it.hasNext())
		{
			PrivacyList privacyList = it.next();
			buf.append(privacyList.toXml());
		}
		
		buf.append("</" + getElementName() + ">");
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		Privacy privacy = (Privacy) super.clone();
		privacy.activeName = this.activeName;
		privacy.defaultName = this.defaultName;
		privacy.declineActiveList = this.declineActiveList;
		privacy.declineDefaultList = this.declineDefaultList;
		
		privacy.privacyLists = new HashMap<String, PrivacyList>();
		for(Map.Entry<String, PrivacyList> entry : this.privacyLists.entrySet())
		{
			privacy.privacyLists.put(entry.getKey(), (PrivacyList) entry.getValue().clone());
		}
		
		return privacy;
	}
	
	

}
