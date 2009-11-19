/**
 * 
 */
package net.sf.christy.routemessage;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import net.sf.christy.xmpp.XmlStanza;

/**
 * @author Noah
 * 
 */
public class RouteMessage implements XmlStanza
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7481258505544785743L;

	private String from;

	private String to;

	private String toUserNode;
	
	private String streamId;

	private boolean closeStream;
	
	private XmlStanza xmlStanza;
	
	private List<RouteExtension> routeExtensions = new CopyOnWriteArrayList<RouteExtension>();

	public RouteMessage(String from)
	{
		this.from = from;
	}

	
	public RouteMessage(String from, String streamId)
	{

		this.from = from;
		this.streamId = streamId;
	}

	/**
	 * @param from
	 * @param to
	 * @param streamId
	 */
	public RouteMessage(String from, String to, String streamId)
	{
		this.from = from;
		this.to = to;
		this.streamId = streamId;
	}

	/**
	 * @return the toUserNode
	 */
	public String getToUserNode()
	{
		return toUserNode;
	}

	/**
	 * 
	 * @return
	 */
	public String getPrepedUserNode()
	{
		if (toUserNode == null)
		{
			return null;
		}
		return toUserNode.toLowerCase();
	}
	
	/**
	 * @param toUserNode the toUserNode to set
	 */
	public void setToUserNode(String toUserNode)
	{
		this.toUserNode = toUserNode;
	}

	/**
	 * 
	 * @return
	 */
	public XmlStanza getXmlStanza()
	{

		return xmlStanza;
	}

	public String getFrom()
	{

		return from;
	}

	public void setFrom(String from)
	{

		this.from = from;
	}

	public String getTo()
	{

		return to;
	}

	public void setTo(String to)
	{

		this.to = to;
	}

	public String getStreamId()
	{

		return streamId;
	}

	public void setStreamId(String streamId)
	{

		this.streamId = streamId;
	}

	public boolean isCloseStream()
	{
		return closeStream;
	}

	public void setCloseStream(boolean closeStream)
	{
		this.closeStream = closeStream;
	}

	public void setXmlStanza(XmlStanza xmlStanza)
	{

		this.xmlStanza = xmlStanza;
	}

	/**
	 * 
	 * @return
	 */
	public RouteExtension[] getAllRouteExtension()
	{

		return routeExtensions.toArray(new RouteExtension[] {});

	}

	/**
	 * 
	 * @param elementName
	 * @param namespace
	 * @return
	 */
	public RouteExtension getRouteExtension(String elementName, String namespace)
	{

		for (RouteExtension extension : routeExtensions)
		{

			if (elementName.equals(extension.getElementName()) && namespace.equals(extension.getNamespace()))
			{

				return extension;
			}
		}
		return null;
	}

	public void addRouteExtension(RouteExtension extension)
	{
		routeExtensions.add(extension);
	}

	public void removeRouteExtension(RouteExtension extension)
	{
		routeExtensions.remove(extension);
	}
	
	public RouteExtension removeRouteExtension(String elementName, String namespace)
	{
		for (int i = routeExtensions.size() - 1; i >= 0; --i)
		{
			RouteExtension extension = routeExtensions.get(i);
			if (elementName.equals(extension.getElementName()) 
					&& namespace.equals(extension.getNamespace()))
			{
				return routeExtensions.remove(i);
			}
		}
		return null;
	}
	
	public void removeAllRouteExtension()
	{
		routeExtensions.clear();
	}
	
	public boolean containExtension(String elementName, String namespace)
	{
		for (RouteExtension extension : routeExtensions)
		{

			if (elementName.equals(extension.getElementName()) 
					&& namespace.equals(extension.getNamespace()))
			{

				return true;
			}
		}
		return false;
	}
	
	public boolean containExtension(RouteExtension routeExtension)
	{
		return routeExtensions.contains(routeExtension);
	}
	
	public boolean isExtensionEmpty()
	{
		return routeExtensions.size() == 0;
	}
	
	protected String getRouteExtensionXml()
	{

		StringBuilder builder = new StringBuilder("");

		for (RouteExtension extension : routeExtensions)
		{

			builder.append(extension.toXml());
		}
		
		return builder.toString();
	}

	/**
	 * 
	 * @return
	 */
	public String toXml()
	{

		StringBuilder builder = new StringBuilder();
		builder.append("<route");
		if (getFrom() != null)
		{
			builder.append(" from=\"").append(getFrom()).append("\"");
		}

		if (getTo() != null)
		{
			builder.append(" to=\"").append(getTo()).append("\"");
		}

		if (getStreamId() != null)
		{
			builder.append(" streamid=\"").append(getStreamId()).append("\"");
		}
		
		if (getToUserNode() != null)
		{
			builder.append(" toUserNode=\"").append(getToUserNode()).append("\"");
		}
		
		builder.append(">");
		
		if (isCloseStream())
		{
			builder.append("<closeStream/>");
		}
		if (getXmlStanza() != null)
		{
			builder.append(getXmlStanza().toXml());
		}

		builder.append(getRouteExtensionXml());

		builder.append("</route>");

		return builder.toString();
	}

	public Object clone() throws CloneNotSupportedException
	{

		RouteMessage routeMessage = (RouteMessage) super.clone();

		routeMessage.to = this.to;
		routeMessage.from = this.from;
		routeMessage.streamId = this.streamId;

		routeMessage.xmlStanza = (XmlStanza) this.xmlStanza.clone();
		
		routeMessage.routeExtensions = new CopyOnWriteArrayList<RouteExtension>();
		for (RouteExtension extension : routeExtensions)
		{
			routeMessage.addRouteExtension((RouteExtension) extension.clone());
		}

		return routeMessage;
	}
}
