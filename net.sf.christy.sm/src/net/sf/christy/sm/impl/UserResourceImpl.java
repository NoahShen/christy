package net.sf.christy.sm.impl;

import net.sf.christy.sm.UserResource;
import net.sf.christy.util.AbstractPropertied;

public class UserResourceImpl extends AbstractPropertied implements UserResource
{
	private String node;
	
	private String resource;
	
	
	/**
	 * @param node
	 * @param resource
	 */
	public UserResourceImpl(String node, String resource)
	{
		this.node = node;
		this.resource = resource;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(String node)
	{
		this.node = node;
	}

	/**
	 * @param resource the resource to set
	 */
	public void setResource(String resource)
	{
		this.resource = resource;
	}

	@Override
	public String getNode()
	{
		return node;
	}

	@Override
	public String getResource()
	{
		return resource;
	}

}
