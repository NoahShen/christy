package net.sf.christy.sm.consistenthashinginterceptor;

import java.util.ArrayList;
import java.util.List;

import net.sf.christy.routemessage.RouteExtension;

public class SearchRouteExtension implements RouteExtension
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -135747334750316200L;

	public static final String ELEMENTNAME = "search";
	
	public static final String NAMESPACE = "christy:internal:searchResource";
	
	private int times;
	
	private int total;
	
	private String startNode;
	
	private String fromc2s;
	
	private List<CheckedNode> checkedNodes = new ArrayList<CheckedNode>();
	
	/**
	 * @param times
	 * @param total
	 * @param startNode
	 */
	public SearchRouteExtension(int times, int total, String startNode, String fromc2s)
	{
		this.times = times;
		this.total = total;
		this.startNode = startNode;
		this.fromc2s = fromc2s;
	}

	/**
	 * @return the times
	 */
	public int getTimes()
	{
		return times;
	}

	/**
	 * @return the total
	 */
	public int getTotal()
	{
		return total;
	}

	/**
	 * @return the startNode
	 */
	public String getStartNode()
	{
		return startNode;
	}
	
	public String getFromc2s() {
	
		return fromc2s;
	}

	public void addCheckedNode(CheckedNode node)
	{
		checkedNodes.add(node);
	}
	
	public void removeCheckedNode(CheckedNode node)
	{
		checkedNodes.remove(node);
	}
	
	public CheckedNode[] getCheckedNode()
	{
		return checkedNodes.toArray(new CheckedNode[]{});
	}
	
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
		StringBuilder builder = 
			new StringBuilder("<" + ELEMENTNAME + " times=\"")
			.append(getTimes()).append("\"")
			.append(" total=\"").append(getTotal()).append("\"")
			.append(" startNode=\"").append(getStartNode()).append("\"")
			.append("xmlns=\"" + NAMESPACE + "\"");
		
		if (checkedNodes.isEmpty())
		{
			builder.append("/>");
		}
		else
		{
			builder.append(">");
			
			for (CheckedNode node : checkedNodes)
			{
				builder.append(node.toXml());
			}
			
			builder.append("</search>");
		}
		
		return builder.toString();
	}
	
	public Object clone() throws CloneNotSupportedException
	{
		SearchRouteExtension extension = (SearchRouteExtension) super.clone();
		extension.times = this.times;
		extension.total = this.total;
		extension.startNode = this.startNode;
		extension.checkedNodes = new ArrayList<CheckedNode>();
		for (CheckedNode node : checkedNodes)
		{
			extension.checkedNodes.add((CheckedNode) node.clone());
		}
		return extension;
	}
	
}
