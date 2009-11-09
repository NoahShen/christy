package net.sf.christy.sm.impl;

import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import net.sf.christy.sm.OnlineUser;
import net.sf.christy.sm.UserResource;
import net.sf.christy.util.AbstractPropertied;

public class OnlineUserImpl extends AbstractPropertied implements OnlineUser
{
	private String node;
	
	private Set<UserResource> userResources = new CopyOnWriteArraySet<UserResource>();
	
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
	
	@Override
	public UserResource getUserResourceByStreamId(String streamId)
	{
		for (UserResource res : userResources)
		{
			if (streamId.equals(res.getStreamId()))
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

	public boolean containUserResource(String resource)
	{
		for (UserResource res : userResources)
		{
			if (resource.equals(res.getResource()))
			{
				return true;
			}
		}
		return false;
	}

	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OnlineUserImpl other = (OnlineUserImpl) obj;
		if (node == null)
		{
			if (other.node != null)
				return false;
		}
		else if (!node.equals(other.node))
			return false;
		return true;
	}

	@Override
	public String toString()
	{
		return "OnlineUserImpl [node=" + node + ", userResources=" + userResources + "]";
	}
	
	
}
