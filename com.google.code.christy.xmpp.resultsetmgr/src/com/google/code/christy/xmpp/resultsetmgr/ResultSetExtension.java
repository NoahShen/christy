package com.google.code.christy.xmpp.resultsetmgr;

import com.google.code.christy.xmpp.PacketExtension;

public class ResultSetExtension implements PacketExtension
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4961548223756197998L;

	public static final String ELEMENTNAME = "set";

	public static final String NAMESPACE = "http://jabber.org/protocol/rsm";
	
	private String first;
	
	private int firstIndex = Integer.MIN_VALUE;
	
	private int index = Integer.MIN_VALUE;
	
	private String last;
	
	private String after;
	
	private int max = Integer.MIN_VALUE;
	
	private int count = Integer.MIN_VALUE;
	
	
	/**
	 * @return the first
	 */
	public String getFirst()
	{
		return first;
	}

	/**
	 * @return the firstIndex
	 */
	public int getFirstIndex()
	{
		return firstIndex;
	}

	/**
	 * @return the last
	 */
	public String getLast()
	{
		return last;
	}

	/**
	 * @return the after
	 */
	public String getAfter()
	{
		return after;
	}

	/**
	 * @return the max
	 */
	public int getMax()
	{
		return max;
	}

	/**
	 * @return the count
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * @param first the first to set
	 */
	public void setFirst(String first)
	{
		this.first = first;
	}

	/**
	 * @param firstIndex the firstIndex to set
	 */
	public void setFirstIndex(int firstIndex)
	{
		this.firstIndex = firstIndex;
	}

	/**
	 * @param last the last to set
	 */
	public void setLast(String last)
	{
		this.last = last;
	}

	/**
	 * @param after the after to set
	 */
	public void setAfter(String after)
	{
		this.after = after;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(int max)
	{
		this.max = max;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count)
	{
		this.count = count;
	}

	/**
	 * @return the index
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * @param index the index to set
	 */
	public void setIndex(int index)
	{
		this.index = index;
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
		StringBuffer buf = new StringBuffer();
		buf.append("<" + getElementName() + " " + "xmlns=\"" + getNamespace() + "\">");
		
		if (this.getFirst() != null)
		{
			buf.append("<first");
			if (this.firstIndex != Integer.MIN_VALUE)
			{
				buf.append(" index=\"").append(getFirstIndex()).append("\">");
			}
			else
			{
				buf.append(">");
			}
			buf.append(getFirst()).append("</first>");
		}
		
		if (this.getLast() != null)
		{
			buf.append("<last>").append(this.getLast()).append("</last>");
		}
		
		if (this.getMax() != Integer.MIN_VALUE)
		{
			buf.append("<max>").append(this.getMax()).append("</max>");
		}
		
		if (this.getAfter() != null)
		{
			buf.append("<after>").append(this.getAfter()).append("</after>");
		}
		
		if (this.getCount() != Integer.MIN_VALUE)
		{
			buf.append("<count>").append(this.getCount()).append("</count>");
		}
		
		if (this.getIndex() != Integer.MIN_VALUE)
		{
			buf.append("<index>").append(this.getIndex()).append("</index>");
		}
		
		buf.append("</" + getElementName() + ">");
		return buf.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		ResultSetExtension resultSetExtension = (ResultSetExtension) super.clone();
		resultSetExtension.after = this.after;
		resultSetExtension.count = this.count;
		resultSetExtension.first = this.first;
		resultSetExtension.firstIndex =this.firstIndex;
		resultSetExtension.last = this.last;
		resultSetExtension.max = this.max;
		
		return resultSetExtension;
	}

}
