package net.sf.christy.routemessage.searchextension;

import java.util.ArrayList;
import java.util.List;

import net.sf.christy.xmpp.Presence;
import net.sf.christy.xmpp.XmlStanza;

public class CheckedNode implements XmlStanza
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3866164937119070109L;

	private String name;
	
	private List<BindedResouce> bindedResouces = new ArrayList<BindedResouce>();
	
	/**
	 * @param name
	 */
	public CheckedNode(String name)
	{
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	public void addBindedResouce(BindedResouce bindedResouce)
	{
		bindedResouces.add(bindedResouce);
	}
	
	public void removeBindedResouce(BindedResouce bindedResouce)
	{
		bindedResouces.remove(bindedResouce);
	}
	
	public BindedResouce[] getBindedResouces()
	{
		return bindedResouces.toArray(new BindedResouce[]{});
	}
	
	@Override
	public String toXml()
	{
		StringBuilder builder = 
			new StringBuilder("<checkedNode name=")
			.append("\"").append(getName()).append("\"");
		
		if (bindedResouces.isEmpty())
		{
			builder.append("/>");
		}
		else
		{
			builder.append(">");
			for (BindedResouce res : bindedResouces)
			{
				builder.append(res.toXml());
			}
			
			builder.append("</checkedNode>");
		}
			
		return builder.toString();
	}

	public Object clone() throws CloneNotSupportedException
	{
		CheckedNode checkedNode = (CheckedNode) super.clone();
		checkedNode.name = this.name;
		checkedNode.bindedResouces = new ArrayList<BindedResouce>();
		for (BindedResouce bindedResouce : bindedResouces)
		{
			checkedNode.bindedResouces.add((BindedResouce) bindedResouce.clone());
		}
		return checkedNode;
	}
	
	public class BindedResouce implements XmlStanza
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -8836293806710477629L;

		private String name;
		
		private String relatedC2s;
		
		private String streamId;
		
		private boolean sesseionBinded;

		private Presence presence;
		
		public BindedResouce(String name, String relatedC2s, String streamId, boolean sessionBinded)
		{
			super();
			this.name = name;
			this.relatedC2s = relatedC2s;
			this.streamId = streamId;
			this.sesseionBinded = sessionBinded;
		}

		public Presence getPresence()
		{
			return presence;
		}

		public void setPresence(Presence presence)
		{
			this.presence = presence;
		}

		/**
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * @return the relatedC2s
		 */
		public String getRelatedC2s()
		{
			return relatedC2s;
		}


		public String getStreamId()
		{
			return streamId;
		}

		public boolean isSesseionBinded()
		{
			return sesseionBinded;
		}

		public Object clone() throws CloneNotSupportedException
		{
			BindedResouce bindedResource = (BindedResouce) super.clone();
			bindedResource.name = this.name;
			bindedResource.relatedC2s = this.relatedC2s;
			bindedResource.streamId = this.streamId;
			return bindedResource;
		}

		@Override
		public String toXml()
		{
			return "<bindedResouce name=\"" + getName() + "\"" +
					" relatedC2s=\"" + getRelatedC2s() + "\"" +
					" streamId=\"" + getStreamId() +"\"" +
					" sessionBinded=\"" + isSesseionBinded() + "/>";
		}
		
	}
}