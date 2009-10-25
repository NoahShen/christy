package net.sf.christy.routemessage;

public class BindRouteExtension implements RouteExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4488865867947619256L;

	public static final String ELEMENTNAME = "bindResource";
	
	public static final String NAMESPACE = "christy:internal:bindResource";

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
		StringBuilder builder = new StringBuilder();
		builder.append("<").append(getElementName())		
			.append(" xmlns=\"")
			.append(getNamespace()).append("\"/>");
		return builder.toString();
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		BindRouteExtension extension = (BindRouteExtension) super.clone();
		return extension;
	}

}
