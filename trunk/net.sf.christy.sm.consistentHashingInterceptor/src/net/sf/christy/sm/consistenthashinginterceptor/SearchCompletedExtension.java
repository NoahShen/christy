package net.sf.christy.sm.consistenthashinginterceptor;


import net.sf.christy.routemessage.RouteExtension;


public class SearchCompletedExtension implements RouteExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4694524735260122559L;

	public static final String ELEMENTNAME = "searchCompleted";
	
	public static final String NAMESPACE = "christy:internal:searchResource";
	
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
		return "<" + ELEMENTNAME + " xmlns=\"" + NAMESPACE + "\"/>";
	}
	
	
	public Object clone() throws CloneNotSupportedException
	{
		SearchCompletedExtension extension = (SearchCompletedExtension) super.clone();
		return extension;
	}
}
