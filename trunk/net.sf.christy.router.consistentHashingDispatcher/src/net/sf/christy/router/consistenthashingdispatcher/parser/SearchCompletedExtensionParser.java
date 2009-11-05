package net.sf.christy.router.consistenthashingdispatcher.parser;

import org.xmlpull.v1.XmlPullParser;

import net.sf.christy.routemessage.RouteExtension;
import net.sf.christy.routemessageparser.RouteExtensionParser;
import net.sf.christy.routemessageparser.RouteMessageParser;
import net.sf.christy.router.consistenthashingdispatcher.SearchCompletedExtension;


public class SearchCompletedExtensionParser implements RouteExtensionParser {


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
	public RouteExtension parseExtension(XmlPullParser parser, RouteMessageParser routeParser) throws Exception
	{
		SearchCompletedExtension extension = new SearchCompletedExtension();
		boolean done = false;
		while (!done)
		{
			int eventType = parser.next();
			String elementName = parser.getName();
			if (eventType == XmlPullParser.START_TAG)
			{
				
			}
			else if (eventType == XmlPullParser.END_TAG)
			{
				if (ELEMENTNAME.equals(elementName))
				{
					done = true;
				}
			}
		}
		return extension;
	}

}
