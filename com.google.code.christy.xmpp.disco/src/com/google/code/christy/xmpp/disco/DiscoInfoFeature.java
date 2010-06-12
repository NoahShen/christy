package com.google.code.christy.xmpp.disco;

/**
 * @author noah
 * 
 */
public class DiscoInfoFeature
{
	private String node;

	private String feature;

	/**
	 * @param node
	 * @param feature
	 */
	public DiscoInfoFeature(String node, String feature)
	{
		this.node = node;
		this.feature = feature;
	}

	public String getNode()
	{
		return node;
	}

	public String getFeature()
	{
		return feature;
	}
}