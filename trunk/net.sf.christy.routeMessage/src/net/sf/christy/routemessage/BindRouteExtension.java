package net.sf.christy.routemessage;

public class BindRouteExtension implements RouteExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4488865867947619256L;

	private String jidNode;
	
	private String bindedResource;
	
	/**
	 * @param jidNode
	 */
	public BindRouteExtension(String jidNode)
	{
		this.jidNode = jidNode;
	}

	/**
	 * @return the jidNode
	 */
	public String getJidNode()
	{
		return jidNode;
	}

	/**
	 * @return the bindedResource
	 */
	public String getBindedResource()
	{
		return bindedResource;
	}

	/**
	 * @param jidNode the jidNode to set
	 */
	public void setJidNode(String jidNode)
	{
		this.jidNode = jidNode;
	}

	/**
	 * @param bindedResource the bindedResource to set
	 */
	public void setBindedResource(String bindedResource)
	{
		this.bindedResource = bindedResource;
	}

	@Override
	public String getElementName()
	{
		return "bindResource";
	}

	@Override
	public String getNamespace()
	{
		return "christy:internal:bindResource";
	}

	@Override
	public String toXml()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<").append(getElementName());
		if (getJidNode() != null)
		{
			builder.append(" jidNode=\"").append(getJidNode()).append("\"");
		}
		if (getBindedResource() != null)
		{
			builder.append(" bindedResource=\"").append(getBindedResource()).append("\"");
		}
		
		builder.append(" xmlns=\"").append(getNamespace()).append("\"/>");
		return builder.toString();
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		BindRouteExtension extension = (BindRouteExtension) super.clone();
		extension.jidNode = this.jidNode;
		extension.bindedResource = this.bindedResource;
		return extension;
	}

}
