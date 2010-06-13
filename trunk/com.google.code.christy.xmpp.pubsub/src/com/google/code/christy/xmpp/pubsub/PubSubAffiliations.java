package com.google.code.christy.xmpp.pubsub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.code.christy.xmpp.XmlStanza;

public class PubSubAffiliations implements XmlStanza
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2459443496531416551L;

	private String node;
	
	private List<Affiliation> affiliations = new ArrayList<Affiliation>();
	
	/**
	 * @return the node
	 */
	public String getNode()
	{
		return node;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(String node)
	{
		this.node = node;
	}

	public void addAffiliation(Affiliation affi)
	{
		affiliations.add(affi);
	}
	
	public void removeAffiliation(Affiliation affi)
	{
		affiliations.remove(affi);
	}
	
	public Collection<Affiliation> getAffiliations()
	{
		return affiliations;
	}
	
	@Override
	public String toXml()
	{
		StringBuilder buf = new StringBuilder();
		buf.append("<affiliations");
		
		if (getNode() != null)
		{
			buf.append(" node=\"").append(getNode()).append("\"");
		}
		
		if (affiliations.isEmpty())
		{
			buf.append("/>");
		}
		else
		{
			buf.append(">");
			for (Affiliation affi : affiliations)
			{
				buf.append(affi.toXml());
			}
			buf.append("</affiliations>");
		}
		
		
		return buf.toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		PubSubAffiliations pubSubAffiliations = (PubSubAffiliations) super.clone();
		pubSubAffiliations.node = this.node;
		pubSubAffiliations.affiliations = new ArrayList<Affiliation>();
		for (Affiliation affi : affiliations)
		{
			pubSubAffiliations.affiliations.add((Affiliation) affi.clone());
		}
		return pubSubAffiliations;
	}
	
	public static class Affiliation implements XmlStanza
	{
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -2300898452833714358L;

		private String node;
		
		private AffiliationType affiliationType;
		
		/**
		 * @param node
		 * @param affiliationType
		 */
		public Affiliation(String node, AffiliationType affiliationType)
		{
			this.node = node;
			this.affiliationType = affiliationType;
		}

		/**
		 * @return the node
		 */
		public String getNode()
		{
			return node;
		}

		/**
		 * @return the affiliationType
		 */
		public AffiliationType getAffiliationType()
		{
			return affiliationType;
		}

		@Override
		public String toXml()
		{
			StringBuilder buf = new StringBuilder();
			buf.append("<affiliation");
			if (getNode() != null)
			{
				buf.append(" node=\"").append(getNode()).append("\"");
			}
			
			if (getAffiliationType() != null)
			{
				buf.append(" affiliation=\"").append(getAffiliationType().name()).append("\"");
			}
			
			buf.append("/>");
			
			return buf.toString();
		}
		

		@Override
		public Object clone() throws CloneNotSupportedException
		{
			Affiliation affiliation = (Affiliation) super.clone();
			affiliation.node = this.node;
			affiliation.affiliationType = this.affiliationType;
			return affiliation;
		}
		
	}
	
	public enum AffiliationType
	{
		owner,
		
		publisher,
		
		outcast
	}
}
