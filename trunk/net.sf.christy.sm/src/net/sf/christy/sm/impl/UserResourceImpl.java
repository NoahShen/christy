package net.sf.christy.sm.impl;

import net.sf.christy.sm.UserResource;
import net.sf.christy.util.AbstractPropertied;

public class UserResourceImpl extends AbstractPropertied implements UserResource
{
	private String node;
	
	private String resource;
	
	private String relatedC2s;
	/**
	 * @param node
	 * @param resource
	 */
	public UserResourceImpl(String node, String resource, String relatedC2s)
	{
		this.node = node;
		this.resource = resource;
		this.relatedC2s = relatedC2s;
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

	@Override
	public String getRelatedC2s()
	{

		return relatedC2s;
	}

}
