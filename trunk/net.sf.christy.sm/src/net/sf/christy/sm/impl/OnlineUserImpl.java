package net.sf.christy.sm.impl;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.christy.sm.OnlineUser;
import net.sf.christy.sm.UserResource;
import net.sf.christy.util.AbstractPropertied;

public class OnlineUserImpl extends AbstractPropertied implements OnlineUser
{
	private String node;
	
	private List<UserResource> userResources = new CopyOnWriteArrayList<UserResource>();
	
	/**
	 * @param node
	 */
	public OnlineUserImpl(String node)
	{
		this.node = node;
	}

	@Override
	public UserResource[] getAllUserResources()
	{
		return userResources.toArray(new UserResource[]{});
	}

	@Override
	public String getNode()
	{
		return node;
	}

	@Override
	public int getResourceCount()
	{
		return userResources.size();
	}

	@Override
	public UserResource getUserResource(String resource)
	{
		for (UserResource res : userResources)
		{
			if (resource.equals(res.getResource()))
			{
				return res;
			}
		}
		return null;
	}
	
	public boolean addUserResource(UserResource userResource)
	{
		return userResources.add(userResource);
	}
	
	public boolean removeUserResource(UserResource userResource)
	{
		return userResources.remove(userResource);
	}
	
	public UserResource removeUserResource(String resource)
	{
		for (Iterator<UserResource> it = userResources.iterator(); it.hasNext();)
		{
			UserResource res = it.next();
			if (resource.equals(res.getResource()))
			{
				it.remove();
				return res;
			}
		}
		return null;
	}

}
